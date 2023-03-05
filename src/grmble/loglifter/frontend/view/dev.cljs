(ns grmble.loglifter.frontend.view.dev
  (:require
   [grmble.loglifter.frontend.util :refer [>evt]]))

(defn link-entry
  ([url] (link-entry url url))
  ([link-text url] [:li [:a {:href url :target "_blank"} link-text]]))

(defn dev-tab []
  [:section.section
   [:h1.title "Development Tools"]

   ;; ??? is-ancestor screws up the alignment
   [:div.tile
    [:div.tile.is-4.is-vertical
     [:div.tile
      [:article
       [:h2.subtitle "Documentation"]
       [:ul
        (link-entry "Babashka Book" "https://book.babashka.org/")
        [link-entry "Bulma Docs" "https://bulma.io/documentation/"]
        [link-entry "Clojure Spec Guide" "https://clojure.org/guides/spec"]
        [link-entry "ClojureScript Cheatsheet" "https://cljs.info/cheatsheet/"]
        (link-entry "Clojure Style Guide" "https://guide.clojure.style/")
        [link-entry "Glimt Github" "https://github.com/ingesolvoll/glimt"]
        [link-entry "Inge Solvoll's Blog" "http://ingesolvoll.github.io/archives/"]
        [link-entry "Instaparse" "https://github.com/Engelberg/instaparse"]
        [link-entry "Kee-Frame Github" "https://github.com/ingesolvoll/kee-frame"]
        [link-entry "Kee-Frame Sample" "https://github.com/ingesolvoll/kee-frame-sample/"]
        [link-entry "Learn PWA" "https://web.dev/learn/pwa/"]
        [link-entry "Medley" "https://weavejester.github.io/medley/medley.core.html"]
        [link-entry "Paredit Visual Guide" "https://calva.io/paredit/#editing"]
        [link-entry "Pouch DB" "https://pouchdb.com/api.html"]
        [link-entry "Re-Frame Docs" "https://day8.github.io/re-frame/re-frame/"]
        [link-entry "Re-Frame Github" "https://github.com/day8/re-frame"]
        [link-entry "Re-Frame HTTP Fx" "https://github.com/day8/re-frame-http-fx"]
        [link-entry "Re-Frame Testring" "https://github.com/day8/re-frame/blob/master/docs/Testing.md"]
        [link-entry "Reagent Documentation" "https://cljdoc.org/d/reagent/reagent/1.1.1/doc/documentation-index"]
        [link-entry "Service Worker" "https://w3c.github.io/ServiceWorker/"]
        [link-entry "Shadow-cljs User Guide" "https://shadow-cljs.github.io/docs/UsersGuide.html"]
        [link-entry "Specced Def" "https://github.com/nedap/speced.def"]
        [link-entry "Tick" "https://juxt.github.io/tick/"]
        [link-entry "Workbox (Service Workers)" "https://developer.chrome.com/docs/workbox/"]
        ;; sort me ^^^
        ]]]


     [:div.tile
      [:article
       [:h2.subtitle.mt-5 "Other Reading"]
       [:ul
        (link-entry "Handling Invalid User Inputs"
                    "https://medium.com/web-dev-survey-from-kyoto/how-to-handle-invalid-user-inputs-in-react-forms-for-ux-design-best-practices-e3108ef8a793")]]]]
    [:div.tile
     [:article
      [:h2.subtitle "Testing"]
      [:div.field
       [:label.label "Snapshot of :current"]
       [:div.field.is-grouped
        [:div.control
         [:button.button.is-primary {:on-click #(>evt [:snapshot-current])}
          "Snapshot"]]
        [:div.control
         [:button.button {:on-click #(>evt [:restore-snapshot])}
          "Restore"]]]]]]]])
