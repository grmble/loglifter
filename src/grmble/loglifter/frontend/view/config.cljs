(ns grmble.loglifter.frontend.view.config
  (:require
   [grmble.loglifter.frontend.util :refer [<sub >evt]]))

(defn config-tab []
  (let [programs (<sub [:sorted-programs :name])]
    [:section.section
     [:h1.title "Configuration"]
     [:h2.subtitle "Training Programs"]
     [:ul (for [{:keys [slug name current?]} programs]
            [:li.control {:key slug}
             [:label.radio
              [:input {:type "radio"
                       :name "current-program"
                       :checked current?
                       :on-change #(>evt [:switch-program slug])}]
              name]])]]))
