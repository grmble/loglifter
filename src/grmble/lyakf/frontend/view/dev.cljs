(ns grmble.lyakf.frontend.view.dev)

(defn link-entry
  ([url] (link-entry url url))
  ([link-text url] [:li [:a {:href url :target "_blank"} link-text]]))

(defn dev-tab []
  [:section.section
   [:h1.title "Development Tools"]
   [:h2.subtitle "Useful Links"]
   [:ul
    [link-entry "Bulma Docs" "https://bulma.io/documentation/"]
    [link-entry "Clojure Spec Guide" "https://clojure.org/guides/spec"]
    [link-entry "ClojureScript Cheatsheet" "https://cljs.info/cheatsheet/"]
    [link-entry "Glimt Github" "https://github.com/ingesolvoll/glimt"]
    [link-entry "Inge Solvoll's Blog" "http://ingesolvoll.github.io/archives/"]
    [link-entry "Kee-Frame Github" "https://github.com/ingesolvoll/kee-frame"]
    [link-entry "Kee-Frame Sample" "https://github.com/ingesolvoll/kee-frame-sample/"]
    [link-entry "Re-Frame Docs" "https://day8.github.io/re-frame/re-frame/"]
    [link-entry "Re-Frame Github" "https://github.com/day8/re-frame"]
    [link-entry "Re-Frame HTTP Fx" "https://github.com/day8/re-frame-http-fx"]
    [link-entry "Re-Frame Testring" "https://github.com/day8/re-frame/blob/master/docs/Testing.md"]
    [link-entry "Reagent Documentation" "https://cljdoc.org/d/reagent/reagent/1.1.1/doc/documentation-index"]
    [link-entry "Shadow-cljs User Guide" "https://shadow-cljs.github.io/docs/UsersGuide.html"]
    [link-entry "Specced Def" "https://github.com/nedap/speced.def"]
    ;; sort me! ^^^
    ]])
