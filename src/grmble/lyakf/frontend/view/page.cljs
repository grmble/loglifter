(ns grmble.lyakf.frontend.view.page
  "Common page layout"
  (:require
   [grmble.lyakf.frontend.view.dev :as dev]
   [grmble.lyakf.frontend.util :refer [<sub]]
   [reagent.core :as r]
   [kee-frame.core :as k]))

(defn nav-link [current-tab uri title tab]
  [:a.navbar-item
   {:href   uri
    :class (when (= tab current-tab) "is-active")}
   title])

(defn navbar [tab]
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
       [nav-link tab "#/" "Home" :home]
       [nav-link tab "#/data" "Data" :data]
       [nav-link tab "#/config" "Config" :config]
       (when (<sub [:show-dev-tab?])
         [nav-link tab "#/dev" "Dev" :dev])]]]))


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

(defn show-tab [tab component]
  [:<>
   [navbar tab]
   component])

(defn home-tab []
  [counting-component])

(defn data-tab []
  [:div "Data"])

(defn config-tab []
  [:div "Config"])


(defn loading-tab []
  [:button.button.is-loading.is-warning])



(defn current-page []
  [:<>
   (k/case-route (comp :name :data)
                 :home [show-tab :home [home-tab]]
                 :data [show-tab :data [data-tab]]
                 :config [show-tab :config [config-tab]]
                 :dev [show-tab :dev [dev/dev-tab]]
                 [loading-tab])])

(defn loading-page []
  [:<>
   [navbar]
   [loading-tab]])
