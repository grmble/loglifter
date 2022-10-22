(ns grmble.lyakf.frontend.spec
  (:require [cljs.spec.alpha :as s]))

(s/def ::db-spec (s/keys :req-un [::ui
                                  ::config
                                  ::training-programs]))

(s/def ::ui (s/keys :req-un [::initialized?]))
(s/def ::initialized? boolean?)

(s/def ::config (s/keys :req-un [::show-dev-tab?]))
(s/def ::show-dev-tab? boolean?)


(s/def ::training-programs (s/map-of ::program-name ::training-program))
(s/def ::training-program (s/keys :req-un [::program-name
                                           ::exercises]))
(s/def ::program-name string?)

(s/def ::exercises (s/coll-of ::exercise))
(s/def ::exercise (s/or ::progression ::progression
                        ::alternating ::alternating))

(defmulti progression-type :type)
(defmethod progression-type :linear [_]
  (s/keys :req-un [::exercise-slug
                   ::weight
                   ::increment
                   ::round-to-multiples-of]))
(s/def ::progression (s/multi-spec progression-type :type))
(s/def ::alternating (s/coll-of ::progression))


(s/def ::exercise-slug string?)
(s/def ::weight number?)
(s/def ::increment number?)
(s/def ::round-to-multiples-of number?)




(comment

  (s/explain ::db-spec {:ui {:initialized? false
                             :current-tab :home}
                        :config {:show-dev-tab? false}
                        :training-programs {}})

  (s/valid? ::exercises [{:type :linear
                          :exercise-slug "asdf"
                          :weight 100
                          :increment 10
                          :round-to-multiples-of 5}]))
