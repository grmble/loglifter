(ns grmble.lyakf.frontend.app-test
  (:require
   [cljs.test :refer [deftest is]]
   [grmble.lyakf.frontend.app :as app]))

(deftest init-test
  (is (nil? (app/init)))
  (is (= 1 1)))
