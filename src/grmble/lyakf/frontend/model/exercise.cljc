(ns grmble.lyakf.frontend.model.exercise
  (:require
   #?(:cljs [cljs.spec.alpha :as s]
      :clj [clojure.spec.alpha :as s])
   [grmble.lyakf.frontend.model.util :as util]))

(s/def ::exercise (s/keys :req-un [::slug
                                   ::name
                                   ::weight
                                   ::increment
                                   ::round-to]))
(s/def ::slug util/slug?)
(s/def ::name util/live-string?)
(s/def ::weight double?)
(s/def ::increment double?)
(s/def ::round-to double?)

;; this breaks cuerdas formating?
(defrecord Exercise [slug name weight increment round-to])

(def default-exercises
  (util/map-by
   :slug
   [(map->Exercise {:slug :squat :name "Squat" :weight 20.0 :increment 5.0 :round-to 5.0})
    (map->Exercise {:slug :bench :name "Bench Press" :weight 20.0 :increment 2.5 :round-to 2.5})
    (map->Exercise {:slug :deadlift :name "Deadlift" :weight 20.0 :increment 5.0 :round-to 5.0})
    (map->Exercise {:slug :overhead :name "Overhead Press" :weight 20.0 :increment 2.5 :round-to 2.5})]))

(defn increment-exercise
  "Increment the exercise"
  ([exercises slug]
   (update exercises slug increment-exercise))
  ([x]
   (update x :weight + (:increment x))))

(comment
  (s/explain ::exercise (:squat default-exercises))

  (increment-exercise (default-exercises :squat))
  (increment-exercise default-exercises :squat))
