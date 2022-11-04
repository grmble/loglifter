(ns grmble.lyakf.frontend.view.data
  (:require
   ["codemirror" :refer [EditorView basicSetup]]
   [grmble.lyakf.frontend.util :refer [<sub]]
   [reagent.core :as r]))

;; the inner / outer pattern comes straigt from the docs
;; https://day8.github.io/re-frame/Using-Stateful-JS-Components/
(defn codemirror-inner []
  (let [view     (atom nil)
        init!    (fn [comp]
                   (let [history     (:history (r/props comp))
                         state       (.-state @view)
                         length      (or (some-> state .-doc .-length)
                                         0)
                         transaction (.update state #js {:changes #js {:from 0 :to length :insert history}})]
                     (.dispatch @view transaction)))]

    (r/create-class
     {:reagent-render         (fn []
                                [:div#codemirror])

      :component-did-mount    (fn [comp]
                                (let [elem  (js/document.getElementById "codemirror")
                                      cm    (EditorView.
                                             #js {:extensions #js [basicSetup]
                                                  :parent elem})]
                                  (reset! view cm)
                                  (init! comp)))
      :component-did-update    init!
      :display-name            "codemirror-inner"})))


(defn codemirror-outer []
  (let [transient (<sub [:transient])]
    (fn []
      ;; it is a map so it can be accessed as (r/props cmop)
      [codemirror-inner transient])))


(defn data-tab []
  [:section.section
   [:h1.title "Data"]
   [codemirror-outer]])
