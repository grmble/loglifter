(ns grmble.lyakf.frontend.sub
  (:require
   [grmble.lyakf.frontend.model.program :as program]
   [re-frame.core :as rf]))

(rf/reg-sub :show-dev-tab?
            (comp :show-dev-tab? :config))

(rf/reg-sub :ui :ui)

(rf/reg-sub :exercises :exercises)

(rf/reg-sub :programs :programs)

(rf/reg-sub :current :current)

(rf/reg-sub :initialized?
            (fn [_qv] (rf/subscribe [:ui]))
            (fn [ui _]
              (:initialized? ui)))

;; (<sub [:sorted-programs :name])
(rf/reg-sub :sorted-programs
            (fn [_qv]
              [(rf/subscribe [:programs])
               (rf/subscribe [:current])])
            (fn [[programs {:keys [slug]}] [_ key]]
              (->> programs
                   (vals)
                   (map (fn [{pslug :slug :as p}] (assoc p :current? (= pslug slug))))
                   (sort-by key))))

(rf/reg-sub :current-program
            (fn [_qv]
              [(rf/subscribe [:programs])
               (rf/subscribe [:current])])
            (fn [[programs {:keys [slug]}] _]
              (programs slug)))

;; selectors for the current workout
;; GOD I LOVE RE-FRAME
;; i put this is the transient part of app db,
;; but it was janky because of all the update logic,
;; and initalisation would be hell once local storage is in place.
;; but it does not need to be in the model at all!
;; it is a function of the current program and the current
;; program data ...
(rf/reg-sub :workout-selectors
            (fn [_qv]
              [(rf/subscribe [:current-program])
               (rf/subscribe [:current])])
            (fn [[program {:keys [data]}] _]
              (program/current-selectors program data)))

(rf/reg-sub :current-workout-info
            (fn [_qv]
              [(rf/subscribe [:current-program])
               (rf/subscribe [:exercises])
               (rf/subscribe [:workout-selectors])
               (rf/subscribe [:current])])
            (fn [[program exercises selectors {:keys [data]}] _]
              (let [completed?     (program/mk-completed? data)
                    uncompleted    (remove completed? selectors)]
                (mapv (fn [sel]
                        (let [completed (completed? sel)
                              xref      (program/exercise-ref program sel)
                              exercise  (->> xref
                                             :slug
                                             exercises)]
                          (println "sel:" sel "completed:" completed)
                          {:exercise exercise
                           :selector sel
                           :repsets completed
                           ;; focus seems to go to the LAST element with auto-focus
                           :focus (first uncompleted)
                           :suggestion (program/wizard-suggestion xref exercises)}))
                      selectors))))
