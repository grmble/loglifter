(ns grmble.lyakf.frontend.event
  (:require
   [grmble.lyakf.frontend.model.exercise :as exercise]
   [grmble.lyakf.frontend.model.program :as program]
   [re-frame.core :as rf]))

(defn set-testing-weights [db [_ squat bench overhead deadlift]]
  (-> db
      (assoc-in [:exercises "squat" :weight] squat)
      (assoc-in [:exercises "bench" :weight] bench)
      (assoc-in [:exercises "overhead" :weight] overhead)
      (assoc-in [:exercises "deadlift" :weight] deadlift)))

(rf/reg-event-db :set-testing-weights set-testing-weights)


(defn switch-program-handler [db [_ slug]]
  (update db
          :current
          #(assoc % :slug slug :data nil)))

(rf/reg-event-db :switch-program switch-program-handler)


(defn complete-handler [db [_ selector repsets]]
  (let [slug (get-in db [:current :slug])
        program (-> db :programs (get slug))
        [completed-slugs data] (program/complete-with-slugs repsets program
                                                            (-> db :current :data)
                                                            selector)]
    (-> db
        (update :exercises
                #(reduce exercise/increment-exercise % completed-slugs))
        (update :current #(assoc % :data data)))))

(rf/reg-event-db :complete complete-handler)

(comment
  (require '[grmble.lyakf.frontend.model])

  (def db grmble.lyakf.frontend.model/default-db)

  (let [slug (get-in db [:current :slug])
        program (-> db :programs (get slug))
        data (-> db :current :data)]
    (program/complete-with-slugs true program data {:path [0 0]}))



  (complete-handler db [:complete {:path [0 0]} "asdf"]))
