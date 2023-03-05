#! /usr/bin/env bb
(ns generate-sw
  (:require
   [selmer.parser :as sp]
   [clojure.java.io :as io]
   [clojure.string :as string]))

(defn copy-template [src dst ctx]
  (println "generating " (str dst))
  (with-open [w (io/writer dst)]
    (.write w (sp/render-file src ctx))))

(defn generate-sw-files [{:keys [debug?] :as opts}]
  (println "generate-sw-files" opts)
  (copy-template "index.html" "public/index.html" opts)

  (when-not debug?
    (.mkdir (java.io.File. "public/js"))
    (copy-template "manifest.json" "public/manifest.json" opts)
    (copy-template "register_sw.js" "public/js/register_sw.js" opts)
    (copy-template "sw.js" "public/sw.js" opts)))

;; empty main because a babaskha task needs a function
;; but will execute the guard too
(defn task [& _])

;; task runner exec runs this too
(when *file* (System/getProperty "babashka.file")
      (let [part (first *command-line-args*)
            base-path (cond
                        (not part) ""
                        (string/starts-with? part "part") (str "/learn-you-a-keeframe/" part)
                        :else "/loglifter")
            debug?    (not part)]
        (generate-sw-files
         {:base-path base-path
          :debug?    debug?
          :part      part})))
