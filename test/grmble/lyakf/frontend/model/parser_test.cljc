(ns grmble.lyakf.frontend.model.parser-test
  (:require
   #?(:clj [clojure.test :refer [deftest testing is]]
      :cljs [cljs.test :refer-macros [deftest testing is]])
   [grmble.lyakf.frontend.model.parser :as parser]))

(deftest parse-history
  (testing "collection of errors and history entries by date"
    (let [result (parser/parse-history "xxx\n2022-10-10 squat 80x5x3\n2022-10-12 squat 85x5x3\nyyy\n2022-10-12 bench 60x5x3")]
      (is (= [1 4] (sort-by identity (-> result :errs keys))))
      (is (= ["2022-10-10" "2022-10-12"] (sort-by identity (-> result :by-date keys))))
      (is (= 1 (count (-> result :by-date (get "2022-10-10")))))
      (is (= 2 (count (-> result :by-date (get "2022-10-12")))))))
  (testing "weights with decimal points"
    (let [result (parser/parse-history "2022-10-12 squat 66.6x5")]
      (is (not (:errs result))))))
