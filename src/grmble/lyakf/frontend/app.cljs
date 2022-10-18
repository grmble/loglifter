(ns grmble.lyakf.frontend.app
  (:require
   [reagent.core :as r]
   [reagent.dom :as rdom]))

(def click-count (r/atom 0))

(defn counting-component []
  [:div
   "The atom " [:code "click-count"] " has value: "
   @click-count ". "
   [:br]
   [:input {:type "button" :value "Inc"
            :on-click #(swap! click-count inc)}]
   [:br]
   [:input {:type "button" :value "Dec"
            :on-click #(swap! click-count dec)}]])


(defn- root-element []
  (.getElementById js/document "root"))

;; init! is called initially by shadlow-cljs (init-fn)
;; after-load! is called after every load
(defn ^:dev/after-load after-load! []
  ;; note the use of the var - #' reader syntax
  (rdom/render [#'counting-component] (root-element)))
(defn init! []
  (after-load!)
  (println "init! complete"))
