(ns datahike-backend-template.core
  (:require [datahike.store :refer [empty-store delete-store connect-store scheme->index default-config config-spec]]
            [datahike.config :refer [int-from-env bool-from-env]]
            [hitchhiker.tree.bootstrap.konserve :as kons]
            [your-konserve-store.core :as k]
            [environ.core :refer [env]]
            [clojure.spec.alpha :as s]
            [superv.async :refer [<?? S]]))

(defmethod empty-store :bs [config]
  (kons/add-hitchhiker-tree-handlers
   (<?? S (k/new-your-store config))))

(defmethod delete-store :bs [config]
  (k/delete-store config))

(defmethod connect-store :bs [config]
  (<?? S (k/new-your-store config)))

(defmethod scheme->index :bs [_]
  :datahike.index/hitchhiker-tree)

(defmethod default-config :bs [config]
  (merge
   {:dbtype "your-store"
    :user (:datahike-store-user env)
    :password (:datahike-store-password env)
    :host (:datahike-store-host env)
    :port (int-from-env :datahike-store-port nil)
    :dbname (:datahike-store-dbname env)
    :ssl (bool-from-env (:datahike-store-ssl env) nil)
    :sslfactory (:datahike-store-ssl-factory env)}
   config))

(s/def :datahike.store.bs/backend #{:bs})
(s/def :datahike.store.bs/user string?)
(s/def :datahike.store.bs/password string?)
(s/def :datahike.store.bs/host string?)
(s/def :datahike.store.bs/dbtype string?)
(s/def :datahike.store.bs/port int?)
(s/def :datahike.store.bs/dbname string?)
(s/def :datahike.store.bs/ssl boolean?)
(s/def :datahike.store.bs/sslfactory string?)
(s/def ::bs (s/keys :req-un [:datahike.store.bs/backend
                             :datahike.store.bs/dbname
                             :datahike.store.bs/user
                             :datahike.store.bs/password
                             :datahike.store.bs/dbtype
                             :datahike.store.bs/host
                             :datahike.store.bs/port]
                    :opt-un [:datahike.store.bs/ssl
                             :datahike.store.bs/sslfactory]))

(defmethod config-spec :bs [_] ::bs)

