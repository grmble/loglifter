(ns grmble.loglifter.frontend.sub
  (:require
   [grmble.loglifter.frontend.model.program :as program]
   [re-frame.core :as rf]))

;; (fn [db [& args]] (get-in db args))
;; (fn [db args] (get-in db args))
(rf/reg-sub :transient get-in)
(rf/reg-sub :config get-in)
(rf/reg-sub :current get-in)

;; safer variant of :exercises - (fn [db _] (:exercises db))
;; (:exercises {:typo 42} "BOOM") ==> "BOOM"
(rf/reg-sub :exercises :-> :exercises)
(rf/reg-sub :programs :-> :programs)

(rf/reg-sub :flash
            (fn [_]
              [(rf/subscribe [:transient :flash])])
            (fn [[flash] [_]]
              (for [[id {:keys [class msg]}] flash]
                {:id id :class class :msg msg})))

;; (<sub [:sorted-programs :name])
(rf/reg-sub :sorted-programs
            (fn [_qv]
              [(rf/subscribe [:programs])
               (rf/subscribe [:current :slug])])
            (fn [[programs slug] [_ key]]
              (->> programs
                   (vals)
                   (map (fn [{pslug :slug :as p}] (assoc p :current? (= pslug slug))))
                   (sort-by key))))

(rf/reg-sub :current-program
            (fn [_qv]
              [(rf/subscribe [:programs])
               (rf/subscribe [:current :slug])])
            (fn [[programs slug] _]
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
               (rf/subscribe [:current :data])])
            (fn [[program data] _]
              (program/current-selectors program data)))

(rf/reg-sub :current-workout-info
            (fn [_qv]
              [(rf/subscribe [:current-program])
               (rf/subscribe [:exercises])
               (rf/subscribe [:workout-selectors])
               (rf/subscribe [:current :data])
               (rf/subscribe [:current :weights])])
            (fn [[program exercises selectors data weights] _]
              (let [completed?     (program/mk-completed? data)
                    uncompleted    (remove completed? selectors)]
                (mapv (fn [sel]
                        (let [completed (completed? sel)
                              xref      (program/exercise-ref program sel)
                              xn        (:slug xref)
                              weight    (weights xn)
                              exercise  (cond-> (exercises xn)
                                          weight (assoc :weight weight))]
                          {:exercise exercise
                           :selector sel
                           :repsets completed
                           ;; focus seems to go to the LAST element with auto-focus
                           :focus (first uncompleted)
                           :suggestion (program/wizard-suggestion xref exercise)}))
                      selectors))))
