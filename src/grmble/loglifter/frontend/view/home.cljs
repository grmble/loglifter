(ns grmble.loglifter.frontend.view.home
  (:require
   [grmble.loglifter.frontend.util :refer [<sub >evt]]
   [grmble.loglifter.frontend.model.parser :as parser]
   [reagent.core :as r]))

(def x-mark "\u2718")
(def check-mark "\u2713")
(def circle-widdershins "\u21ba") ;; nanny ogg would approve

(defn reset-exercise [exercise show? toggle]
  (r/with-let [value (r/atom (:weight exercise))]
    (let [reset-exercise! (fn [evt]
                            (>evt [:reset-exercise (:slug exercise) @value])
                            (toggle evt))]
      [:form.modal {:class (when @show? :is-active)
                    :on-submit reset-exercise!}
       [:div.modal-background]
       [:div.modal-card
        [:header.modal-card-head
         [:p.modal-card-title (str "Reset " (:name exercise))] ; without the str a very strange error ...
         [:button.delete {:aria-label "close" :type "button" :on-click toggle}]]
        [:section.modal-card-body
         [:input.input {:on-change #(reset! value (-> % .-target .-value))
                        :value @value}]]
        [:footer.modal-card-foot
         [:button.button.is-success
          {:type "submit"
           :on-click reset-exercise!}
          "Save"]
         [:button.button {:on-click toggle} "Cancel"]]]])))

(defn- exercise-wizard [_ _ _ suggestion repsets]
  (let [prop-value    (r/atom (or repsets suggestion))
        changed-value (r/atom nil)
        reset?        (r/atom false)]
    (fn exercise-wizard-fn [{name :name :as exercise}
                            selector focus suggestion repsets]
      (reset! prop-value (or repsets suggestion))
      (when (= @prop-value @changed-value)
        (reset! changed-value nil))

      (let [id       (pr-str (:path selector))
            value    (or @changed-value @prop-value)
            invalid? (parser/repsets-invalid? value)
            swap-controlled-value #(reset! changed-value (-> % .-target .-value))
            toggle-reset          (fn
                                    ([] (swap! reset? not))
                                    ([evt]
                                     (swap! reset? not)
                                     (.preventDefault evt)))]

        [:div
         [:form {:on-submit (fn [evt]
                              (>evt [:complete selector value])
                              (.preventDefault evt))}
          [:div.field.is-horizontal
           [:div.field-label.is-normal
            [:label.label {:for id} name]]
           [:div.field-body
            [:div.field.has-addons
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
                repsets [:button.button.is-primary {:type "submit" :title "Complete" :disabled true} check-mark]
                invalid?   [:button.button.is-danger {:type "submit" :title "Complete" :disabled true} x-mark]
                :else      [:button.button.is-primary {:type "submit" :title "Complete"} check-mark])]
             [:div.control
              [:button.button.is-info {:title "Reset Exercise"
                                       :on-click toggle-reset} circle-widdershins]]]]]]

         [reset-exercise exercise reset? toggle-reset]]))))

(defn home-tab []
  (let [workout-info (<sub [:current-workout-info])]
    [:section.section
     [:h1.title "Next Workout"]
     [:ul
      (for [{:keys [exercise selector focus suggestion repsets]} workout-info]
        [:li {:key (pr-str (:path selector))}
         [exercise-wizard exercise selector focus suggestion repsets]])]]))
