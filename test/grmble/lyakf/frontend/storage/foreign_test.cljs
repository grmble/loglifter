(ns grmble.lyakf.frontend.storage.foreign-test
  (:require
   [cljs.test :refer [deftest testing is]]
   [cljs.spec.alpha :as s]
   [grmble.lyakf.frontend.model.exercise :as exercise]
   [grmble.lyakf.frontend.model.program :as program]
   [grmble.lyakf.frontend.model]
   [grmble.lyakf.frontend.model.util]
   [grmble.lyakf.frontend.storage.foreign :as foreign]))


(defn valid-or-explain [schema v]
  (if (s/valid? schema v)
    true
    (s/explain schema v)))

(deftest exercises
  (testing "roundtrip"
    (let [obj  (foreign/exercises->js exercise/default-exercises)
          v    (foreign/js->exercises obj)]
      (is (valid-or-explain :grmble.lyakf.frontend.model/exercises v))
      (is (= exercise/default-exercises v)))))

(deftest programs
  (testing "roundtrip"
    (let [obj  (foreign/programs->js program/default-programs)
          v    (foreign/js->programs obj)]
      (is (valid-or-explain :grmble.lyakf.frontend.model/programs v))
      (is (= program/default-programs v)))))

(defn- current-roundtrip [data]
  (let [obj  (foreign/current->js data)
        v    (foreign/js->current obj)]
    (is (valid-or-explain :grmble.lyakf.frontend.model/current v))
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
