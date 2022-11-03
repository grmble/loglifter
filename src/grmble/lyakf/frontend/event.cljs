(ns grmble.lyakf.frontend.event
  (:require
   [grmble.lyakf.frontend.model.exercise :as exercise]
   [grmble.lyakf.frontend.storage.foreign :as foreign]
   [grmble.lyakf.frontend.storage.local]
   [grmble.lyakf.frontend.model.program :as program]
   [re-frame.core :as rf]))

(defn set-testing-weights [db [_ squat bench overhead deadlift]]
  (-> db
      (assoc-in [:exercises :squat :weight] squat)
      (assoc-in [:exercises :bench :weight] bench)
      (assoc-in [:exercises :overhead :weight] overhead)
      (assoc-in [:exercises :deadlift :weight] deadlift)))

(rf/reg-event-db :set-testing-weights set-testing-weights)


(defn switch-program-handler [{:keys [db]} [_ slug]]
  (let [db (update db :current
                   #(assoc % :slug slug :data nil))]
    {:db db
     :grmble.lyakf.frontend.storage.local/store
     {:kvs {"current" (foreign/current->js (:current db))}
      :db db}}))

(rf/reg-event-fx :switch-program switch-program-handler)


(defn complete-handler [{:keys [db]} [_ selector repsets]]
  (let [slug (get-in db [:current :slug])
        program (-> db :programs (get slug))
        [completed-slugs data] (program/complete-with-slugs repsets program
                                                            (-> db :current :data)
                                                            selector)
        db (-> db
               (update :exercises
                       #(reduce exercise/increment-exercise % completed-slugs))
               (update :current #(assoc % :data data)))]
    {:db db
     :grmble.lyakf.frontend.storage.local/store
     {:kvs (cond-> {:current
                    (foreign/current->js (:current db))}
             (seq completed-slugs) {"exercises" (foreign/exercises->js (:exercises db))})
      :db db}}))

(rf/reg-event-fx :complete complete-handler)

(comment
  (require '[grmble.lyakf.frontend.model])

  (def db grmble.lyakf.frontend.model/default-db)

  (let [slug (get-in db [:current :slug])
        program (-> db :programs (get slug))
        data (-> db :current :data)]
    (program/complete-with-slugs true program data {:path [0 0]}))



  (complete-handler db [:complete {:path [0 0]} "asdf"]))
