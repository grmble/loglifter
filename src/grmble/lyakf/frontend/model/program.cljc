(ns grmble.lyakf.frontend.model.program
  (:require
   #?(:cljs [cljs.spec.alpha :as s]
      :clj [clojure.spec.alpha :as s])
   #?(:cljs [cljs.pprint :refer [pprint]]
      :clj [clojure.pprint :refer [pprint]])
   [grmble.lyakf.frontend.model.util :as util]
   [medley.core :as medley]
   [cuerdas.core :as cuerdas]))

(def ^{:doc "Supported progressions"}
  progression? #{:linear :five-three-one})

(s/def ::program (s/keys :req-un [::slug
                                  ::name
                                  ::min-days
                                  ::workouts]))
(s/def ::slug util/slug?)
(s/def ::name util/live-string?)
(s/def ::workouts (s/and vector? (s/coll-of ::workout)))
(s/def ::min-days number?)
(s/def ::workout (s/and vector? (s/coll-of ::exercise)))
(s/def ::exercise (s/or :exercise (s/keys :req-un [::slug ::progression]
                                          :opt-un [::opts])
                        :alternating (s/and vector? (s/coll-of (s/keys :req-un [::slug ::progression])))))
(s/def ::progression progression?)
;; opts are progression specific, in other words, the can be anything


(defrecord Program [slug name workouts min-days])
(defrecord ExerciseRef [slug progression opts])


;; hardcoded programs
(def repout-suggestion "%(weight)sx5x2 %(weight)sx5x5+")
(def deadlift-suggestion "%(weight)sx5")

(def glp-upper-split
  (map->Program
   {:slug "glp-upper-split"
    :name "Greyskull LP Upper Body Split"
    :min-days 2
    :workouts
    [[(->ExerciseRef "squat" :linear repout-suggestion)
      [(->ExerciseRef "bench" :linear repout-suggestion)
       (->ExerciseRef "overhead" :linear repout-suggestion)]
      (->ExerciseRef "deadlift" :linear deadlift-suggestion)]]}))

(def greyskull-lp
  (map->Program
   {:slug "greyskull-lp"
    :name "Greyskull LP"
    :min-days 2
    :workouts
    [[(->ExerciseRef "squat" :linear repout-suggestion)
      (->ExerciseRef "bench" :linear repout-suggestion)
      (->ExerciseRef "overhead" :linear repout-suggestion)
      (->ExerciseRef "deadlift" :linear deadlift-suggestion)]]}))

(def five-three-one
  (map->Program
   {:slug "five-three-one"
    :name "Five Three One"
    :min-days 2
    :workouts
    [[{:slug "squat" :progression :five-three-one :opts :five}
      {:slug "bench" :progression :five-three-one :opts :five}]
     [{:slug "deadlift" :progression :five-three-one :opts :five}
      {:slug "overhead" :progression :five-three-one :opts :five}]
     [{:slug "squat" :progression :five-three-one :opts :three}
      {:slug "bench" :progression :five-three-one :opts :three}]
     [{:slug "deadlift" :progression :five-three-one :opts :three}
      {:slug "overhead" :progression :five-three-one :opts :three}]
     [{:slug "squat" :progression :five-three-one :opts :one}
      {:slug "bench" :progression :five-three-one :opts :one}]
     [{:slug "deadlift" :progression :five-three-one :opts :one}
      {:slug "overhead" :progression :five-three-one :opts :one}]
     [{:slug "squat" :progression :five-three-one :opts :rec}
      {:slug "bench" :progression :five-three-one :opts :rec}]
     [{:slug "deadlift" :progression :five-three-one :opts :rec}
      {:slug "overhead" :progression :five-three-one :opts :rec}]]}))

(def default-programs
  (util/map-by :slug [greyskull-lp glp-upper-split five-three-one]))


(s/def ::exercise-data (s/keys :opt-un [::completed
                                        ::alternative]))
(s/def ::alternative integer?)
(s/def ::completed (s/or :nil nil?
                         :string string?))

(s/def ::data
  (s/or :nil nil?
        :map
        (s/map-of integer?
                  (s/map-of integer? ::exercise-data))))

;; a selector is vector of indices that points to an exercise-ref
;; it is either [w x] as in Workout and eXercise
;; or  `{:path [w x a] :number-of-alternatives na} 
;; as in Workout, eXercise, Alternative, Number of Alternatives
(defrecord Selector [path number-alternatives])


(defn selectors
  "All selectors of a program."
  [program-or-workouts]
  (if-let [workouts (:workouts program-or-workouts)]
    (selectors workouts)
    (flatten
     (for [[wi w] (map-indexed vector program-or-workouts)
           [xi x] (map-indexed vector w)]
       (if (map? x)
         (->Selector [wi xi] 0)
         (let [na (count x)]
           (map-indexed (fn [ai _] (->Selector [wi xi ai] na)) x)))))))

(defn- mk-current-alternative? [program-data]
  (fn [{[w x a] :path}]
    (if a
      (let [current-alternative (get-in program-data [w x :alternative] 0)]
        (= a current-alternative))
      true)))

(defn mk-completed? [program-data]
  (fn [{[w x] :path}]
    (get-in program-data [w x :completed])))

(defn current-selectors
  "All selectors of the current workout."
  [program data]
  (let [all-selectors (selectors program)
        not-completed (sequence
                       (comp (filter (mk-current-alternative? data))
                             (remove (mk-completed? data)))
                       all-selectors)]
    (when (first not-completed)
      (let [{[workout-id] :path} (first not-completed)]
        (sequence
         (comp (filter (mk-current-alternative? data))
               (filter (fn [{[w] :path}] (== workout-id w))))
         all-selectors)))))

(defn complete-exercise [{[w x] :path} repsets data]
  (assoc-in data [w x :completed] repsets))

(defn exercise-completed?
  "Has the exercise identified by `sel` been completed?
   
   Prefer `mk-completed` in a loop."
  [sel data]
  ((mk-completed? data) sel))


(defn- complete-program-data
  "Remove all completed fields from all exercises."
  [completed-selectors data]
  (reduce (fn [data {[w x a] :path n :number-alternatives}]
            (cond-> data
              a     (update-in [w x :alternative] #(mod (inc (or % 0)) n))
              :else (medley/dissoc-in [w x :completed])))
          data completed-selectors))

(defn exercise-ref
  "Get the exercise ref for the selector"
  [program {path :path :as sel}]
  (if-let [workouts (:workouts program)]
    (exercise-ref workouts sel)
    (get-in program path)))

(defn- completed-selectors [program data]
  (sequence
   (comp (filter (mk-current-alternative? data))
         (filter (mk-completed? data)))
   (selectors program)))

(defn complete-with-slugs
  "Complete the exercise identified by `selector`.
   
   It this completes the whole program, program data
   is cleaned out and alternatives are incremented
   for the next iteration.

   IF this completes the program, 
   returns a pair `[ seq-of-slugs new-data]`,
   otherwise `[nil new-data]`.  This
   is for `real` usage where those exercises
   have their weight increased.

   For use in reductions (tests) see `complete`
   "
  [repsets program data selector]
  (let [data (complete-exercise selector repsets data)
        workout (current-selectors program data)]
    (if (empty? workout)
      (let [completed (completed-selectors program data)
            xrefs (map #(exercise-ref program %) completed)
            slugs (set (map :slug xrefs))]
        [slugs
         (complete-program-data completed data)])
      [nil data])))

(defn complete
  "Reducer version of `complete-with-slugs`.
   
   If you need to increase the exercise weights,
   use `complete-with-slugs`"
  [repsets program data selector]
  (nth (complete-with-slugs  repsets program data selector) 1))


(comment

  (def data nil)
  (def sels (current-selectors glp-upper-split data))
  (pprint sels)
  (def data (complete true glp-upper-split data (first sels)))
  (def data (complete true glp-upper-split data (second sels)))
  (pprint data)
  (pprint sels)
  (def sels (current-selectors glp-upper-split data))
  (pprint sels)
  (pprint (completed-selectors glp-upper-split data))


  (def data (complete true glp-upper-split data (nth sels 2)))
  (pprint data)
  (def sels (current-selectors glp-upper-split data))
  (pprint sels)

  (pprint (selectors glp-upper-split))
  (pprint
   (filter (mk-current-alternative? data) (selectors glp-upper-split)))

  (pprint
   (filter (mk-completed? data) (selectors glp-upper-split)))


  (pprint (completed-selectors glp-upper-split data)))


(defmulti wizard-suggestion
  "Data entry suggestion for the wizard.
   
   
   Depending on the progression (:linear/:five-three-one)
   a method is called that has to produce a
   suggestion for the exercise."

  {:arglists '([exercise-ref exercises])}
  :progression)

(defmethod wizard-suggestion :linear
  [xref exercises]
  (let [ex (-> xref :slug exercises)]
    (cuerdas/format (:opts xref) ex)))

(def ^:private fto-table
  {:five  {:weight1 0.65 :reps1 5
           :weight2 0.75 :reps2 5
           :weight3 0.85 :reps3 "5+"}
   :three {:weight1 0.7 :reps1 3
           :weight2 0.8 :reps2 3
           :weight3 0.9 :reps3 "3+"}
   :one   {:weight1 0.75 :reps1 5
           :weight2 0.85 :reps2 3
           :weight3 0.95 :reps3 "1+"}
   :rec   {:weight1 0.4 :reps1 5
           :weight2 0.5 :reps2 5
           :weight3 0.6 :reps3 5}})

(defn- multiply-rounding [^double v1 ^double v2 ^double round-to]
  (-> v1
      (* v2)
      (/ round-to)
      (Math/round)
      (* round-to)))


(defn- multiply-weights [{:keys [weight1 weight2 weight3] :as table}
                         {:keys [weight round-to]}]
  (assoc table
         :weight1 (multiply-rounding weight1 weight round-to)
         :weight2 (multiply-rounding weight2 weight round-to)
         :weight3 (multiply-rounding weight3 weight round-to)))

(comment
  (multiply-rounding 20.0 0.85 2.5)

  (multiply-weights (fto-table :five) {:weight 65.0 :round-to 2.5}))


(defmethod wizard-suggestion :five-three-one
  [{opts :opts :as xref} exercises]
  (let [exercise (-> xref :slug exercises)
        values (multiply-weights (fto-table opts) exercise)]
    (cuerdas/format
     "%(weight1)sx%(reps1)s %(weight2)sx%(reps2)s %(weight3)sx%(reps3)s"
     values)))

(comment
  (multiply-weights (fto-table :five) {:weight 20.0 :round-to 2.5}))
