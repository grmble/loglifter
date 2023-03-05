(ns grmble.loglifter.frontend.storage.foreign-test
  (:require
   [cljs.test :refer [deftest testing is]]
   [cljs.spec.alpha :as s]
   [grmble.loglifter.frontend.model.exercise :as exercise]
   [grmble.loglifter.frontend.model.program :as program]
   [grmble.loglifter.frontend.model]
   [grmble.loglifter.frontend.model.util]
   [grmble.loglifter.frontend.storage.foreign :as foreign]))


(defn valid-or-explain [schema v]
  (if (s/valid? schema v)
    true
    (s/explain schema v)))

(deftest exercises
  (testing "roundtrip"
    (let [obj  (foreign/exercises->js exercise/default-exercises)
          v    (foreign/js->exercises obj)]
      (is (valid-or-explain :grmble.loglifter.frontend.model/exercises v))
      (is (= exercise/default-exercises v)))))

(deftest programs
  (testing "roundtrip"
    (let [obj  (foreign/programs->js program/default-programs)
          v    (foreign/js->programs obj)]
      (is (valid-or-explain :grmble.loglifter.frontend.model/programs v))
      (is (= program/default-programs v)))))

(defn- current-roundtrip [data]
  (let [obj  (foreign/current->js data)
        v    (foreign/js->current obj)]
    (is (valid-or-explain :grmble.loglifter.frontend.model/current v))
    (is (= data v))))

(deftest current
  (testing "roundtrip"
    (current-roundtrip {:slug :xxx
                        :weights {}
                        :data {}})
    (current-roundtrip
     {:slug :xxx
      :weights {:squat 66.6}
      :data {0 {0 {:completed "xxx"}}}})))
