(ns grmble.lyakf.frontend.model.parser
  "Parser for log entries / rep sets"
  (:require
   #?(:cljs [instaparse.core :as insta :refer-macros [defparser]]
      :clj [instaparse.core :as insta :refer [defparser]])))

;; insta-parse recommends defparser because the work is done at
;; compile time.  but if we want to use the locales decimal separator
;; we can not do this.  so far it seems that it costs around 20 ms
;; per parser (we need 2).  that is ok.
;;
;; but i would  not want to trade this for multiple seconds of load time

(defn parser-text
  "EBNF for parser using decimal-sep in weights."
  [decimal-sep]
  (str "entry = (annotation <ws>)* date <ws> slug <ws> repsets <ws?>;
        date = #'\\d{4}-\\d{2}-\\d{2}';
        slug = #'[-\\w]+';
        repsets = repset (<ws> repset)*;
        repset = (annotation <ws>)* weight [<ws?> <('x'|'*')> reps [<ws?> <('x'|'*')> sets]];
        ws = #'\\s+';
        weight = #'(\\d+)(\\" decimal-sep "\\d*)?';
        reps = #'\\d+';
        sets = #'\\d+';
        annotation = <'@'> #'\\w+';
        "))

(defparser field-parser (parser-text ".") :start :repsets)


(defn parse-field [s]
  (insta/parse field-parser s))

(defn field-invalid? [s]
  (-> (insta/parse field-parser s)
      (insta/failure?)))

(comment
  (def p1 (time (insta/parser (parser-text ".") :start :entry)))

  (parse-field "20x5")
  (field-invalid? "20x5")

  (-> (insta/parse p1 "@bday 2022-10-12 squat @pr 17x5x2 18x5 10")
      identity))

