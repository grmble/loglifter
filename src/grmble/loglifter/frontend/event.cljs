(ns grmble.loglifter.frontend.event
  (:require
   [clojure.string :as str]
   [grmble.loglifter.frontend.date]
   [grmble.loglifter.frontend.storage.foreign :as foreign]
   [grmble.loglifter.frontend.storage.local]
   [grmble.loglifter.frontend.model.program :as program]
   [ajax.core :as ajax]
   [medley.core :as medley]
   [re-frame.core :as rf]
   [grmble.loglifter.frontend.model.parser :as parser]))

;;
;; https://day8.github.io/re-frame/Loading-Initial-Data/
;;
(rf/reg-event-fx :load-config
                 (fn [_ _]
                   {:http-xhrio {:uri             "config.json"
                                 :method          :get
                                 :response-format (ajax/json-response-format {:keywords? true})
                                 :on-success [:config-loaded]
                                 :on-error [:config-not-found]}}))
(rf/reg-event-fx :config-loaded
                 [(rf/inject-cofx :grmble.loglifter.frontend.storage.local/load [:current :exercises :programs])]
                 (fn [{:keys [db current exercises programs]} [_ config]]
                   {:db (cond-> (assoc db :config (merge (:config db) config))
                          true      (assoc-in [:transient :initialized?] true)
                          current   (assoc :current (foreign/js->current current))
                          exercises (assoc :exercises (foreign/js->exercises exercises))
                          programs  (assoc :programs (foreign/js->programs programs)))}))

(rf/reg-event-db :config-not-found
                 (fn [db _]
                   (assoc-in db [:transient :initialized?] true)))

;;
;; flash
;;
(rf/reg-event-db :flash
                 (fn [db [_ {:keys [id class msg]}]]
                   (assoc-in db [:transient :flash id] {:class class :msg msg})))
(rf/reg-event-db :remove-flash
                 (fn [db [_ id]]
                   (medley/dissoc-in db [:transient :flash id])))


;;
;; programs and exercises
;;
(defn switch-program-handler [{:keys [db]} [_ slug]]
  (let [db (update db :current
                   #(assoc % :slug slug :data nil))]
    {:db db

     :grmble.loglifter.frontend.storage.local/store
     {:kvs {"current" (foreign/current->js (:current db))}
      :db db}}))

(rf/reg-event-fx :switch-program switch-program-handler)


(defn- incrementer
  "Incrementer for an exercise"
  [exercises]
  (fn [current slug]
    (let [{:keys [weight increment]} (exercises slug)
          w (or (get-in current [:weights slug])
                weight)]
      (assoc-in current [:weights slug] (double (+ w increment))))))

(defn complete-handler [{:keys [db current-date]} [_ selector repsets]]
  (let [slug                   (get-in db [:current :slug])
        program                (-> db :programs (get slug))
        xref                   (program/exercise-ref program selector)
        [completed-slugs data] (program/complete-with-slugs repsets program
                                                            (-> db :current :data)
                                                            selector)
        db (-> db
               (update :current
                       #(reduce (incrementer (:exercises db)) % completed-slugs))
               (update :current #(assoc % :data data)))]
    {:db db

     :grmble.loglifter.frontend.storage.local/store
     {:kvs {:current (foreign/current->js (:current db))}
      :db db}

     :grmble.loglifter.frontend.storage.local/append-history
     {:current-date current-date
      :slug (:slug xref)
      :repsets repsets}}))

(rf/reg-event-fx :complete
                 [(rf/inject-cofx :current-date)]
                 complete-handler)

(rf/reg-event-db :reset-exercise
                 (fn [db [_ slug value]]
                   (assoc-in db [:current :weights slug] (js/parseFloat value))))

;;
;; storage
;;
(rf/reg-event-fx :snapshot-current
                 (fn [{:keys [db]} [_]]
                   {:db db

                    :grmble.loglifter.frontend.storage.local/store
                    {:kvs {:snapshot (foreign/current->js (:current db))}
                     :db db}}))

(rf/reg-event-fx :restore-snapshot
                 [(rf/inject-cofx :grmble.loglifter.frontend.storage.local/load [:snapshot])]
                 (fn [{:keys [db snapshot]} [_]]
                   {:db (cond-> db
                          snapshot  (assoc :current (foreign/js->current snapshot)))

                    :grmble.loglifter.frontend.storage.local/store
                    {:kvs {:current snapshot}
                     :db db}}))


(rf/reg-event-fx :load-history
                 [(rf/inject-cofx :grmble.loglifter.frontend.storage.local/load-history)]
                 (fn [{:keys [db load-history]} [_]]
                   {:db (assoc-in db [:transient :history] load-history)}))

(rf/reg-event-db :dispose-history
                 (fn [db [_]]
                   (medley/dissoc-in db [:transient :history])))

(rf/reg-event-fx :save-history
                 (fn [{:keys [db]} [_ history]]
                   (let [{:keys [errs] :as result}   (parser/parse-history history)]
                     {:db (cond-> db
                            errs  (assoc-in [:transient :flash :save-history]
                                            {:class :is-danger
                                             :msg (str "There were errors in the following lines: "
                                                       (str/join ", " (keys errs)))}))

                      :grmble.loglifter.frontend.storage.local/store-history
                      result})))
