(ns grmble.lyakf.frontend.storage-test
  (:require
   [cljs.test :refer [deftest testing is]]
   [cljs.spec.alpha :as s]
   [grmble.lyakf.frontend.model.exercise :as exercise]
   [grmble.lyakf.frontend.model.program :as program]
   [grmble.lyakf.frontend.model]
   [grmble.lyakf.frontend.model.util]
   [grmble.lyakf.frontend.storage :as storage]))


(defn valid-or-explain [schema v]
  (if (s/valid? schema v)
    true
    (s/explain schema v)))

(deftest exercises
  (testing "roundtrip"
    (let [json (storage/exercises->json exercise/default-exercises)
          v    (storage/json->exercises json)]
      (is (valid-or-explain :grmble.lyakf.frontend.model/exercises v))
      (is (= exercise/default-exercises v)))))

(deftest programs
  (testing "roundtrip"
    (let [json (storage/programs->json program/default-programs)
          v    (storage/json->programs json)]
      (is (valid-or-explain :grmble.lyakf.frontend.model/programs v))
      (is (= program/default-programs v)))))

(deftest current-slug
  (testing "roundtrip"
    (let [json (storage/current-slug->json :glp-upper-split)
          v    (storage/json->current-slug json)]
      (is (valid-or-explain :grmble.lyakf.frontend.model.program/slug v))
      (is (= :glp-upper-split v)))))

(defn- data-roundtrip [data]
  (let [json (storage/current-data->json data)
        v    (storage/json->current-data json)]
    (is (valid-or-explain :grmble.lyakf.frontend.model.program/data v))
    (is (= data v))))

(deftest current-data
  (testing "roundtrip"
    (data-roundtrip {})
    (data-roundtrip {0 {0 {:completed "xxx"}}})))


(comment
  (-> exercise/default-exercises
      storage/exercises->json
      storage/json->exercises))
