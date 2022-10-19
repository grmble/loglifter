(ns grmble.lyakf.frontend.app
  (:require
   [reagent.core :as r]
   [reagent.dom :as rdom]))

(defonce session (r/atom nil))

(defn nav-link [uri title page]
  [:a.navbar-item
   {:href   uri
    :class (when (= page (:page @session)) "is-active")}
   title])

(defn navbar []
  (r/with-let [expanded? (r/atom false)]
    [:nav.navbar.is-info>div.container
     [:div.navbar-brand
      [:a.navbar-item {:href "/" :style {:font-weight :bold}} "Learn You A Kee-Frame"]
      [:span.navbar-burger.burger
       {:data-target :nav-menu
        :on-click #(swap! expanded? not)
        :class (when @expanded? :is-active)}
       [:span] [:span] [:span]]]
     [:div#nav-menu.navbar-menu
      {:class (when @expanded? :is-active)}
      [:div.navbar-start
       [nav-link "#/" "Home" :home]
       [nav-link "#/about" "About" :about]]]]))


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

(defn main-page []
  [:<>
   (navbar)
   (counting-component)])

(defn- root-element []
  (.getElementById js/document "root"))

;; init! is called initially by shadlow-cljs (init-fn)
;; after-load! is called after every load
(defn ^:dev/after-load after-load! []
  ;; note the use of the var - #' reader syntax
  (rdom/render [#'main-page] (root-element)))
(defn init! []
  (after-load!)
  (println "init! complete"))
