(ns grmble.loglifter.frontend.model.util
  (:require [clojure.string :as str]))

(defn slug?
  "Is it a keyword, does it start with a-z and only contains a-z,0-9 and -?"
  [k]
  (and (keyword? k) (re-matches #"^[a-z][-a-z0-9]*$" (name k))))

(defn live-string?
  "Is it a non-blank string?" [s]
  (and (string? s) (not (str/blank? s))))

(defn map-by [f coll]
  (into {} (map (fn [x] [(f x) x])) coll))

(comment
  (slug? nil)
  (slug? "1 2 3")
  (slug? "xxx")

  (live-string? nil)
  (live-string? "")
  (live-string? "   ")
  (live-string? "blubb")

  (map-by :slug [{:slug "x" :foo 1}]))
