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

      (let [value    (or @changed-value @prop-value)
            invalid? (parser/field-invalid? value)
            swap-controlled-value #(reset! changed-value (-> % .-target .-value))]

        [:form {:on-submit (fn [evt]
                             (>evt [:complete selector value])
                             (.preventDefault evt))}
         [:div.field.is-horizontal
          [:div.field-label.is-normal
           [:label.label name]]
          [:div.field-body.has-addons
           [:div.control
            [:input.input
             (if repsets
               ;; react complain about changing from controlled to uncontrolled
               ;; so we control the disabled component too ...
               {:type "text" :value value :disabled true
                :on-change swap-controlled-value}
               {:type "text"
                ;; this seems to go to the last element with auto-focus
                ;; so we only set it for the one we want to win
                :auto-focus (identical? selector focus)
                :value value
                :class [(when invalid? "is-danger")]
                :on-change swap-controlled-value})]]
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
