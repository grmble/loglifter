(ns grmble.lyakf.frontend.view.page
  "Common page layout"
  (:require
   [grmble.lyakf.frontend.view.data :as data]
   [grmble.lyakf.frontend.view.dev :as dev]
   [grmble.lyakf.frontend.view.home :as home]
   [grmble.lyakf.frontend.view.config :as config]
   [grmble.lyakf.frontend.util :refer [<sub]]
   [reagent.core :as r]
   [kee-frame.core :as k]))

(defn nav-link [current-tab title tab]
  [:a.navbar-item
   {:href  (k/path-for [tab])
    :class (when (= tab current-tab) "is-active")}
   title])

(defn navbar [tab]
  (r/with-let [expanded? (r/atom false)]
    [:nav.navbar.is-info>div.container
     [:div.navbar-brand
      [:a.navbar-item {:href  (k/path-for [:home])
                       :style {:font-weight :bold}} "Learn You A Kee-Frame"]
      [:span.navbar-burger.burger
       {:data-target :nav-menu
        :on-click #(swap! expanded? not)
        :class (when @expanded? :is-active)}
       [:span] [:span] [:span]]]
     [:div#nav-menu.navbar-menu
      {:class (when @expanded? :is-active)}
      [:div.navbar-start
       [nav-link tab "Home" :home]
       [nav-link tab "Data" :data]
       [nav-link tab "Config" :config]
       (when (<sub [:show-dev-tab?])
         [nav-link tab "Dev" :dev])]]]))

(defn footer []
  [:footer.footer
   [:div.content.has-text-centered
    [:p "Read the source, Luke! "
     [:a {:href "https://github.com/grmble/learn-you-a-keeframe/"}
      "https://github.com/grmble/learn-you-a-keeframe/"]
     " - "
     [:a {:href "https://opensource.org/licenses/EPL-2.0"} "EPL 2.0"]]]])

(defn show-tab [tab component]
  [:<>
   [navbar tab]
   component])

(defn loading-tab []
  [:button.button.is-loading.is-warning])



(defn current-page []
  [:<>
   (k/case-route (comp :name :data)
                 :home [show-tab :home [home/home-tab]]
                 :data [show-tab :data [data/data-tab]]
                 :config [show-tab :config [config/config-tab]]
                 :dev [show-tab :dev [dev/dev-tab]]
                 [loading-tab])])

(defn loading-page []
  [:<>
   [navbar]
   [loading-tab]])
