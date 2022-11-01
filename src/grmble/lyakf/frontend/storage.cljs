(ns grmble.lyakf.frontend.storage
  "Storage layer, basically json codecs.
   
   JSON encoding/decoding is sufficently differnt in clj/cljs
   that I am only doing it for cljs.  With a weeping eye,
   because I prefer cljc in java mode for a development repl.

   This is because I don't want to tie myself to transit on the
   storage layer.  I want general purpose JSON on disk/localstorage/whatever.
   "
  (:require
   [grmble.lyakf.frontend.model.exercise :as exercise]
   [grmble.lyakf.frontend.model.program :as program]
   [medley.core :as medley]))

(defn any->json
  "Convert any (map ?) type to a JSON string"
  [x]
  (->> x
       clj->js
       (.stringify js/JSON)))

(defn json->any
  "Convert a JSON string to a clojure type"
  [json]
  (let [obj (.parse js/JSON json)]
    (js->clj obj {:keywordize-keys true})))

(def exercises->json any->json)
(def programs->json any->json)

(defn json->exercises [^String json]
  (->> json
       json->any
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

(defn json->programs [^String json]
  (->> json
       json->any
       (medley/map-vals #(program/map->Program
                          (-> %
                              (update :slug keyword)
                              (update :workouts process-workouts))))))

(defn current-slug->json [slug]
  (name slug))

(defn json->current-slug [json] (keyword json))

(defn current-data->json [data]
  (any->json (for [[w xs] data]
               [w (for [[x m] xs]
                    [x m])])))

(defn json->current-data [json]
  (into {} (for [[w xs] (json->any json)]
             [w
              (into {} (for [[x m] xs]
                         [x m]))])))

(comment

  (current-data->json {0 {0 {:completed "xxx"}}})
  (json->current-data *1))
