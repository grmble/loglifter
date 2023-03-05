(ns grmble.loglifter.frontend.model.program-test
  (:require
   #?(:clj [clojure.test :refer [deftest testing is]]
      :cljs [cljs.test :refer-macros [deftest testing is]])
   [grmble.loglifter.frontend.model.exercise :as exercise]
   [grmble.loglifter.frontend.model.program :as program]))

(defn- workout-indexes [selectors]
  (set
   (map (fn [{[w] :path}] w) selectors)))


(deftest current-selectors
  (testing "finds first workout for empty data"
    (let [greyskull-workout (program/current-selectors program/glp-upper-split nil)
          five-workout (program/current-selectors program/five-three-one nil)]
      (is (= 3 (count greyskull-workout)))
      (is (= 2 (count five-workout)))
      (is (= #{0} (workout-indexes greyskull-workout)))
      (is (= #{0} (workout-indexes five-workout)))))

  (testing "finds next workout if previous is completed"
    (let [data {0 {0 {:completed true}
                   1 {:completed true}}}
          five-workout (program/current-selectors program/five-three-one data)]
      (is (= 2 (count five-workout)))
      (is (= #{1} (workout-indexes five-workout)))))

  (testing "finds no selectors if all workouts are completed"
    (let [data {0 {0 {:completed true}
                   1 {:completed true}
                   2 {:completed true}}}
          greyskull-workout (program/current-selectors program/glp-upper-split data)]
      (is (empty? greyskull-workout)))))

(deftest complete
  (testing "will complete exercises"
    (let [greyskull-workout (program/current-selectors program/glp-upper-split nil)
          data (program/complete true program/glp-upper-split nil (nth greyskull-workout 0))
          data2 (program/complete true program/glp-upper-split data (nth greyskull-workout 1))
          data3 (program/complete true program/glp-upper-split data2 (nth greyskull-workout 2))]
      (is (program/exercise-completed? (nth greyskull-workout 0) data))
      (is (not (program/exercise-completed? (nth greyskull-workout 1) data)))
      (is (not (program/exercise-completed? (nth greyskull-workout 2) data)))
      (is (program/exercise-completed? (nth greyskull-workout 0) data2))
      (is (program/exercise-completed? (nth greyskull-workout 1) data2))
      (is (not (program/exercise-completed? (nth greyskull-workout 2) data2)))
      (is (not-any? (program/mk-completed? data3) greyskull-workout)) ;; program has been reset
      (is (= 3 (count (program/current-selectors program/glp-upper-split data3))))))

  (testing "will not toggle alternatives before program completion"
    ;; the view gets a new list of selectors after every change
    ;; this means the wrong exercise is displayed after completing an alternative
    (let [workout (program/current-selectors program/glp-upper-split nil)
          data (->> nil
                    (program/complete-exercise (nth workout 0) true)
                    (program/complete-exercise (nth workout 1) true))
          workout (program/current-selectors program/glp-upper-split data)]
      (is (= [0 1 0] (:path (second workout))))))

  (testing "will reset completed program"
    (let [greyskull-workout (program/current-selectors program/glp-upper-split nil)
          data (reduce (partial program/complete true program/glp-upper-split)
                       nil greyskull-workout)]
      (is (= [0 1 0] (:path (second greyskull-workout))))
      (let [greyskull-workout (program/current-selectors program/glp-upper-split data)]
        ;; alternating exercise alternates ...
        (is (= [0 1 1] (:path (second greyskull-workout))))
        (is (not-any? (program/mk-completed? data) greyskull-workout))))))

(deftest wizard-suggestion
  (testing "linear suggestion will use exercise weight"
    (let [gr-sels (program/current-selectors program/glp-upper-split nil)
          xref (program/exercise-ref program/glp-upper-split (first gr-sels))
          suggestion (program/wizard-suggestion xref (exercise/default-exercises (:slug xref)))]
      (is (= "20x5x2 20x5x5+" suggestion))))

  (testing "five-three-one suggestion will use different weight"
    (let [fto-sels (program/current-selectors program/five-three-one nil)
          ;; upper body exercise with lesser round-to ...
          xref (program/exercise-ref program/five-three-one (second fto-sels))]
      (is (= "12.5x5 15x5 17.5x5+" (program/wizard-suggestion xref (exercise/default-exercises (:slug xref))))))))
