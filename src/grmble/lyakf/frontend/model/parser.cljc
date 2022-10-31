(ns grmble.lyakf.frontend.model.parser
  "Parser for log entries / rep sets"
  (:require
   #?(:cljs [instaparse.core :as insta :refer-macros [defparser]]
      :clj [instaparse.core :as insta :refer [defparser]])))

;; for defparser to work, the parser needs to be specified
;; as a string literal
;; so for our 2 parsers we have to use the same string twice
;; or write another macro
;;
;; but it is worth it: 
;; 200ms vs 3ms in firefox, 110ms vs 2ms in chrome (for 1 parser)
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

(defn parse-repsets [s]
  (insta/parse repsets-parser s))

(defn repsets-invalid? [s]
  (-> (insta/parse repsets-parser s)
      (insta/failure?)))

