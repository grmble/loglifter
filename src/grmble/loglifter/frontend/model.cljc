(ns grmble.loglifter.frontend.model
  (:require
   #?(:cljs [cljs.spec.alpha :as s]
      :clj [clojure.spec.alpha :as s])
   [grmble.loglifter.frontend.model.exercise :as exercise]
   [grmble.loglifter.frontend.model.program :as program]
   [grmble.loglifter.frontend.model.util :as util]))


(s/def ::db-spec (s/keys :req-un [::transient
                                  ::config ; app config via config.json
                                  ::programs ; hardcoded for now
                                  ::exercises ; hardcoded but contains data
                                  ::current ; current program slug and data - local storage
                                  ]))

(s/def ::transient (s/keys :req-un [::initialized?]
                           :opt-un [::history]))
(s/def ::initialized? boolean?)
(s/def ::history string?)

(s/def ::config (s/keys :req-un [::show-dev-tab?]))
(s/def ::show-dev-tab? boolean?)

(s/def ::current (s/keys :req-un [:grmble.loglifter.frontend.model.program/slug ::weights]
                         :opt-un [:grmble.loglifter.frontend.model.program/data]))

(s/def ::programs
  (s/map-of util/slug? :grmble.loglifter.frontend.model.program/program))
(s/def ::exercises
  (s/map-of util/slug? :grmble.loglifter.frontend.model.exercise/exercise))
(s/def ::weights
  (s/map-of util/slug? :grmble.loglifter.frontend.model.exercise/weight))

(def default-db
  {:transient {:initialized? false}
   :config {:show-dev-tab? false}
   :programs program/default-programs
   :exercises exercise/default-exercises
   :current {:slug :glp-upper-split
             :data {}
             :weights {}}})

(comment

  (s/explain ::exercises exercise/default-exercises)
  (s/explain ::db-spec default-db))
