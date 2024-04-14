(ns datahike-jdbc.core
  (:require [datahike.store :refer [empty-store delete-store connect-store default-config config-spec release-store store-identity]]
            [datahike.config :refer [map-from-env]]
            [konserve-jdbc.core :as k]
            [next.jdbc.connection :as connection]
            [clojure.spec.alpha :as s]
            [clojure.string :as str]))

(defn generate-signature [cfg]
  (if (contains? cfg :jdbcUrl)
    (merge
     (dissoc cfg :jdbcUrl)
     (-> cfg :jdbcUrl connection/uri->db-spec
         (update :dbtype #(str/replace % #"postgres$" "postgresql"))
         (update :port #(if (pos? %) % (-> connection/dbtypes (get (:dbtype %)) :port)))))
    cfg))

(defmethod store-identity :jdbc [store-config]
  ;; the store signature is made up of the dbtype, dbname, and table
  (let [{:keys [dbtype _host _port dbname table]} (generate-signature store-config)]
    [:jdbc dbtype dbname table]))

(defmethod empty-store :jdbc [store-config]
  (k/connect-store store-config))

(defmethod delete-store :jdbc [store-config]
  (k/delete-store store-config))

(defmethod connect-store :jdbc [store-config]
  (k/connect-store store-config))

(defmethod default-config :jdbc [config]
  ;; with the introduction of the store-identity config data should derived from inputs and not set to default values
  (let [env-config (map-from-env :datahike-store-config {})
        passed-config config]
    (merge env-config passed-config)))

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
(s/def :datahike.store.jdbc/table string?)
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
                               :datahike.store.jdbc/password
                               :datahike.store.jdbc/table]))

(defmethod config-spec :jdbc [_] ::jdbc)

(defmethod release-store :jdbc [_ store]
  (k/release store {:sync? true}))
