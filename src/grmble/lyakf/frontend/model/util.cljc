(ns grmble.lyakf.frontend.model.util
  (:require [clojure.string :as string]))

(defn slug?
  "Does it start with a-z and only contain a-z,0-9 and -?"
  [s]
  (and (string? s) (re-matches #"^[a-z][-a-z0-9]*$" s)))

(defn live-string?
  "Is it a non-blank string?" [s]
  (and (string? s) (not (string/blank? s))))

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
