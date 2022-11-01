(ns grmble.lyakf.frontend.model
  (:require
   #?(:cljs [cljs.spec.alpha :as s]
      :clj [clojure.spec.alpha :as s])
   [grmble.lyakf.frontend.model.exercise :as exercise]
   [grmble.lyakf.frontend.model.program :as program]
   [grmble.lyakf.frontend.model.util :as util]))


(s/def ::db-spec (s/keys :req-un [::ui ; transient
                                  ::config ; app config via config.json
                                  ::programs ; hardcoded for now
                                  ::exercises ; hardcoded but contains data
                                  ::current ; current program slug and data - local storage
                                  ]))

(s/def ::ui (s/keys :req-un [::initialized?]))
(s/def ::initialized? boolean?)

(s/def ::config (s/keys :req-un [::show-dev-tab?]))
(s/def ::show-dev-tab? boolean?)

(s/def ::current (s/keys :req-un [:grmble.lyakf.frontend.model.program/slug]
                         :opt-un [:grmble.lyakf.frontend.model.program/data]))

(s/def ::programs
  (s/map-of util/slug? :grmble.lyakf.frontend.model.program/program))
(s/def ::exercises
  (s/map-of util/slug? :grmble.lyakf.frontend.model.exercise/exercise))

(def default-db
  {:ui {:initialized? false}
   :config {:show-dev-tab? false}
   :programs program/default-programs
   :exercises exercise/default-exercises
   :current {:slug :glp-upper-split :data {}}})

(comment

  (s/explain ::exercises exercise/default-exercises)
  (s/explain ::db-spec default-db))
