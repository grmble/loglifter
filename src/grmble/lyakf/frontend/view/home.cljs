(ns grmble.lyakf.frontend.view.home
  (:require
   [grmble.lyakf.frontend.util :refer [<sub >evt]]
   [grmble.lyakf.frontend.model.parser :as parser]
   [reagent.core :as r]))

(def x-mark "\u2718")
(def check-mark "\u2713")

(defn- exercise-wizard [_ _ _ suggestion repsets]
  (let [prop-value (r/atom (or repsets suggestion))
        changed-value (r/atom nil)]
    (fn exercise-wizard-fn [{name :name :as _exercise}
                            selector focus suggestion repsets]
      (reset! prop-value (or repsets suggestion))
      (when (= @prop-value @changed-value)
        (reset! changed-value nil))

      (let [id       (pr-str (:path selector))
            value    (or @changed-value @prop-value)
            invalid? (parser/field-invalid? value)
            swap-controlled-value #(reset! changed-value (-> % .-target .-value))]

        [:form {:on-submit (fn [evt]
                             (>evt [:complete selector value])
                             (.preventDefault evt))}
         [:div.field.is-horizontal
          [:div.field-label.is-normal
           [:label.label {:for id} name]]
          [:div.field-body.has-addons
           [:div.control
            [:input.input
             (-> (if repsets
                   {:disabled true}
                   {:auto-focus (identical? selector focus)
                    :class [(when invalid? "is-danger")]})
                 (assoc :id id
                        :type "text"
                        :value value
                        :on-change swap-controlled-value))]]
           [:div.control
            (cond
              repsets [:button.button.is-primary {:disabled true} check-mark]
              invalid?   [:button.button.is-danger {:disabled true} x-mark]
              :else      [:button.button.is-primary check-mark])]]]]))))

(defn home-tab []
  (let [workout-info (<sub [:current-workout-info])]
    [:section.section
     [:h1.title "Next Workout"]
     [:ul
      (for [{:keys [exercise selector focus suggestion repsets]} workout-info]
        [:li {:key (pr-str (:path selector))}
         [exercise-wizard exercise selector focus suggestion repsets]])]]))
