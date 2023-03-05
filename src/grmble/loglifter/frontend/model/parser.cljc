(ns grmble.loglifter.frontend.model.parser
  "Parser for log entries / rep sets"
  #?(:cljs (:require-macros [grmble.loglifter.frontend.model.parser]))
  (:require
   [clojure.string :as str]
   #?(:cljs [instaparse.core :as insta :refer-macros [defparser]]
      :clj [instaparse.core :as insta :refer [defparser]])))

;; defparser is a must - it takes reduces loading time be 100-200ms PER PARSER
;; we need 2, but we have to specify them as string literals
(defparser repsets-parser
  "entry = (annotation <ws>)* date <ws> slug <ws> repsets <ws?>;
        date = #'\\d{4}-\\d{2}-\\d{2}';
        slug = #'[-\\w]+';
        repsets = repset (<ws> repset)*;
        repset = (annotation <ws>)* weight [<ws?> <('x'|'*')> reps [<ws?> <('x'|'*')> sets]];
        ws = #'\\s+';
        weight = #'(\\d+)([\\.,]\\d*)?';
        reps = #'\\d+';
        sets = #'\\d+';
        annotation = <'@'> #'\\w+';
        "
  :start :repsets)
(defparser entry-parser
  "entry = (annotation <ws>)* date <ws> slug <ws> repsets <ws?>;
        date = #'\\d{4}-\\d{2}-\\d{2}';
        slug = #'[-\\w]+';
        repsets = repset (<ws> repset)*;
        repset = (annotation <ws>)* weight [<ws?> <('x'|'*')> reps [<ws?> <('x'|'*')> sets]];
        ws = #'\\s+';
        weight = #'(\\d+)([\\.,]\\d*)?';
        reps = #'\\d+';
        sets = #'\\d+';
        annotation = <'@'> #'\\w+';
        "
  :start :entry)

(defn repsets-invalid? [s]
  (-> (insta/parse repsets-parser s)
      (insta/failure?)))

(defn entry-date
  "Get the date out of a successful entry parse"
  ([result] (entry-date result 1 (count result)))
  ([result idx size]
   (let [[k v] (nth result idx)]
     (cond
       (= k :date)    v
       (>= idx size)  nil
       :else          (recur result (inc idx) size)))))

(defn- vmap [f]
  (map (fn [[i v]] [i (f v)])))
(defn- vremove [f]
  (remove (fn [[_ v]] (f v))))

(defn- acc-results
  "Collect entry parser results for [index entry] pairs."
  [acc [index entry]]
  (let [result   (insta/parse entry-parser entry)]
    (if (insta/failure? result)
      ;; inc zero based index -> 1 based line number
      (assoc-in acc [:errs (inc index)] result)
      (update-in acc [:by-date (entry-date result)] (fn [x] (conj (or x []) entry))))))

(defn parse-history
  "Parse history, either one big string or a collection of lines"
  [history]
  (->> (if (string? history)
         (str/split history #"\n")
         history)
       (map-indexed vector)
       (eduction (comp
                  (vmap str/trim)
                  (vremove str/blank?)))
       (reduce acc-results {})))

