(ns grmble.lyakf.frontend.event
  (:require
   [grmble.lyakf.frontend.date]
   [grmble.lyakf.frontend.storage.foreign :as foreign]
   [grmble.lyakf.frontend.storage.local]
   [grmble.lyakf.frontend.model.program :as program]
   [ajax.core :as ajax]
   [re-frame.core :as rf]))

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
                 [(rf/inject-cofx :grmble.lyakf.frontend.storage.local/load [:current :exercises :programs])]
                 (fn [{:keys [db current exercises programs]} [_ config]]
                   {:db (cond-> (assoc db :config (merge (:config db) config))
                          true      (assoc-in [:ui :initialized?] true)
                          current   (assoc :current (foreign/js->current current))
                          exercises (assoc :exercises (foreign/js->exercises exercises))
                          programs  (assoc :programs (foreign/js->programs programs)))}))

(rf/reg-event-db :config-not-found
                 (fn [db _]
                   (assoc-in db [:ui :initialized?] true)))


(defn set-testing-weights [db [_ squat bench overhead deadlift]]
  (-> db
      (assoc-in [:current :weights :squat] squat)
      (assoc-in [:current :weights :bench] bench)
      (assoc-in [:current :weights :overhead] overhead)
      (assoc-in [:current :weights :deadlift] deadlift)))

(rf/reg-event-db :set-testing-weights set-testing-weights)


(defn switch-program-handler [{:keys [db]} [_ slug]]
  (let [db (update db :current
                   #(assoc % :slug slug :data nil))]
    {:db db
     :grmble.lyakf.frontend.storage.local/store
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
  (let [slug (get-in db [:current :slug])
        program (-> db :programs (get slug))
        [completed-slugs data] (program/complete-with-slugs repsets program
                                                            (-> db :current :data)
                                                            selector)
        db (-> db
               (update :current
                       #(reduce (incrementer (:exercises db)) % completed-slugs))
               (update :current #(assoc % :data data)))]
    {:db db
     :grmble.lyakf.frontend.storage.local/store
     {:kvs {:current (foreign/current->js (:current db))}
      :db db}
     :grmble.lyakf.frontend.storage.local/append-history
     {:current-date current-date
      :slug slug
      :repsets repsets}}))

(rf/reg-event-fx :complete
                 [(rf/inject-cofx :current-date)]
                 complete-handler)

(comment
  (require '[grmble.lyakf.frontend.model])

  (def db grmble.lyakf.frontend.model/default-db)

  (let [slug (get-in db [:current :slug])
        program (-> db :programs (get slug))
        data (-> db :current :data)]
    (program/complete-with-slugs true program data {:path [0 0]}))



  (complete-handler db [:complete {:path [0 0]} "asdf"]))
