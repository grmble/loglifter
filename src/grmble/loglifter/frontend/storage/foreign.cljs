(ns grmble.loglifter.frontend.storage.foreign
  "Convert app db parts into js objects.

   This is very ugly.  I have plans, but for now we will live
   with the ugly.
   "
  (:require
   [grmble.loglifter.frontend.model.exercise :as exercise]
   [grmble.loglifter.frontend.model.program :as program]
   [medley.core :as medley]))


(defn- js->any
  "Convert a JSON object to a clojure type"
  [obj]
  (js->clj obj {:keywordize-keys true}))

(def exercises->js clj->js)
(def programs->js clj->js)

(defn js->exercises [obj]
  (->> obj
       js->any
       (medley/map-vals #(exercise/map->Exercise
                          (update % :slug keyword)))))

(defn- process-workouts [ws]
  (mapv (fn [w]
          (mapv (fn [x]
                  (if (map? x)
                    (program/json->xref x)
                    (mapv program/json->xref x)))
                w))
        ws))

(defn js->programs [obj]
  (->> obj
       js->any
       (medley/map-vals #(program/map->Program
                          (-> %
                              (update :slug keyword)
                              (update :workouts process-workouts))))))

(defn- encode-data [data]
  (for [[w xs] data]
    [w (for [[x m] xs]
         [x m])]))

(defn- decode-data [data]
  (into {} (for [[w xs] data]
             [w
              (into {} (for [[x m] xs]
                         [x m]))])))


(defn current->js [{:keys [data slug] :as current}]
  (-> current
      (assoc :data (encode-data data)
             :slug (name slug))
      (clj->js)))

(defn js->current [obj]
  (let [{:keys [data slug] :as current} (js->any obj)]
    (-> current
        (assoc :data (decode-data data)
               :slug (keyword slug)))))
