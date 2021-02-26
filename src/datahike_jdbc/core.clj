(ns datahike-jdbc.core
  (:require [datahike.store :refer [empty-store delete-store connect-store scheme->index default-config config-spec]]
            [datahike.config :refer [map-from-env]]
            [hitchhiker.tree.bootstrap.konserve :as kons]
            [konserve-jdbc.core :as k]
            [clojure.spec.alpha :as s]
            [superv.async :refer [<?? S]]))

(defmethod empty-store :jdbc [store-config]
  (kons/add-hitchhiker-tree-handlers
   (<?? S (k/new-jdbc-store store-config))))

(defmethod delete-store :jdbc [store-config]
  (let [conn (<?? S (k/new-jdbc-store store-config))]
    (<?? S (k/delete-store conn))))

(defmethod connect-store :jdbc [store-config]
  (<?? S (k/new-jdbc-store store-config)))

(defmethod scheme->index :jdbc [_]
  :datahike.index/hitchhiker-tree)

(defmethod default-config :jdbc [config]
  (merge
   (map-from-env :datahike-store-config {:dbtype "h2:mem"
                                         :dbname "datahike"})
   config))

(s/def :datahike.store.jdbc/backend #{:jdbc})
(s/def :datahike.store.jdbc/dbtype #{"h2" "h2:mem" "hsqldb" "jtds:sqlserver" "mysql" "oracle:oci" "oracle:thin" "postgresql" "redshift" "sqlite" "sqlserver"})
(s/def :datahike.store.jdbc/jdbcUrl string?)
(s/def :datahike.store.jdbc/dbname string?)
(s/def :datahike.store.jdbc/dbname-separator string?)
(s/def :datahike.store.jdbc/host string?)
(s/def :datahike.store.jdbc/host-prefix string?)
(s/def :datahike.store.jdbc/port int?)
(s/def :datahike.store.jdbc/classname string?)
(s/def :datahike.store.jdbc/user string?)
(s/def :datahike.store.jdbc/password string?)
(s/def ::jdbc (s/keys :req-un [:datahike.store.jdbc/backend]
                      :opt-un [:datahike.store.jdbc/dbtype
                               :datahike.store.jdbc/jdbcUrl
                               :datahike.store.jdbc/dbname
                               :datahike.store.jdbc/dbname-separator
                               :datahike.store.jdbc/host
                               :datahike.store.jdbc/host-prefix
                               :datahike.store.jdbc/port
                               :datahike.store.jdbc/classname
                               :datahike.store.jdbc/user
                               :datahike.store.jdbc/password]))

(defmethod config-spec :jdbc [_] ::jdbc)
