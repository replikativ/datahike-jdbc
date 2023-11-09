(ns datahike-jdbc.core-test
  (:require
   #?(:cljs [cljs.test    :as t :refer-macros [is are deftest testing]]
      :clj  [clojure.test :as t :refer        [is are deftest testing]])
   [datahike.api :as d]
   [datahike-jdbc.core]))

(deftest ^:integration test-postgresql
  (let [config {:store {:backend :jdbc
                        :dbtype "postgresql"
                        :host "localhost"
                        :dbname "config-test"
                        :user "alice"
                        :password "foo"}
                :schema-flexibility :read
                :keep-history? false}
        _ (d/delete-database config)]
    (is (not (d/database-exists? config)))
    (let [_ (d/create-database config)
          conn (d/connect config)]

      (d/transact conn [{:db/id 1, :name  "Ivan", :age   15}
                        {:db/id 2, :name  "Petr", :age   37}
                        {:db/id 3, :name  "Ivan", :age   37}
                        {:db/id 4, :age 15}])
      (is (= (d/q '[:find ?e :where [?e :name]] @conn)
             #{[3] [2] [1]}))

      (d/release conn)
      (is (d/database-exists? config))
      (d/delete-database config)
      (is (not (d/database-exists? config))))))

(deftest ^:integration test-mysql
  (let [config {:store {:backend :jdbc
                        :dbtype "mysql"
                        :user "alice"
                        :password "foo"
                        :dbname "config-test"}
                :schema-flexibility :write
                :keep-history? false}
        _ (d/delete-database config)]
    (is (not (d/database-exists? config)))
    (let [_      (d/create-database config)
          conn   (d/connect config)]
      (d/transact conn [{:db/ident :name
                         :db/valueType :db.type/string
                         :db/cardinality :db.cardinality/one}
                        {:db/ident :age
                         :db/valueType :db.type/long
                         :db/cardinality :db.cardinality/one}])
      (d/transact conn [{:db/id 1, :name  "Ivan", :age   15}
                        {:db/id 2, :name  "Petr", :age   37}
                        {:db/id 3, :name  "Ivan", :age   37}
                        {:db/id 4, :age 15}])
      (is (= (d/q '[:find ?e :where [?e :name]] @conn)
             #{[3] [2] [1]}))

      (d/release conn)
      (is (d/database-exists? config))
      (d/delete-database config)
      (is (not (d/database-exists? config))))))

(deftest ^:integration test-mssql
  (let [config {:store {:backend :jdbc
                        :dbtype "sqlserver"
                        :user "sa"
                        :password "passwordA1!"
                        :dbname "tempdb"}
                :schema-flexibility :write
                :keep-history? false}
        _ (d/delete-database config)]
    (is (not (d/database-exists? config)))
    (let [_      (d/create-database config)
          conn   (d/connect config)]
      (d/transact conn [{:db/ident :name
                         :db/valueType :db.type/string
                         :db/cardinality :db.cardinality/one}
                        {:db/ident :age
                         :db/valueType :db.type/long
                         :db/cardinality :db.cardinality/one}])
      (d/transact conn [{:db/id 1, :name  "Ivan", :age   15}
                        {:db/id 2, :name  "Petr", :age   37}
                        {:db/id 3, :name  "Ivan", :age   37}
                        {:db/id 4, :age 15}])
      (is (= (d/q '[:find ?e :where [?e :name]] @conn)
             #{[3] [2] [1]}))

      (d/release conn)
      (is (d/database-exists? config))
      (d/delete-database config)
      (is (not (d/database-exists? config))))))

(deftest ^:integration test-h2
  (let [config {:store {:backend :jdbc
                        :dbtype "h2"
                        :dbname "./temp/db"}
                :schema-flexibility :write
                :keep-history? false}
        _ (d/delete-database config)]
    (is (not (d/database-exists? config)))
    (let [_ (d/create-database config)
          conn (d/connect config)]

      (d/transact conn [{:db/ident :name
                         :db/valueType :db.type/string
                         :db/cardinality :db.cardinality/one}
                        {:db/ident :age
                         :db/valueType :db.type/long
                         :db/cardinality :db.cardinality/one}])
      (d/transact conn [{:db/id 1, :name  "Ivan", :age   15}
                        {:db/id 2, :name  "Petr", :age   37}
                        {:db/id 3, :name  "Ivan", :age   37}
                        {:db/id 4, :age 15}])
      (is (= (d/q '[:find ?e :where [?e :name]] @conn)
             #{[3] [2] [1]}))

      (d/release conn)
      (is (d/database-exists? config))
      (d/delete-database config)
      (is (not (d/database-exists? config))))))

(deftest ^:integration test-env
  (let [_ (d/delete-database)]
    (is (not (d/database-exists?)))
    (let [_ (d/create-database)
          conn (d/connect)]

      (d/transact conn [{:db/ident :name
                         :db/valueType :db.type/string
                         :db/cardinality :db.cardinality/one}
                        {:db/ident :age
                         :db/valueType :db.type/long
                         :db/cardinality :db.cardinality/one}])
      (d/transact conn [{:db/id 1, :name  "Ivan", :age   15}
                        {:db/id 2, :name  "Petr", :age   37}
                        {:db/id 3, :name  "Ivan", :age   37}
                        {:db/id 4, :age 15}])
      (is (= (d/q '[:find ?e :where [?e :name]] @conn)
             #{[3] [2] [1]}))

      (d/release conn)
      (is (d/database-exists?))
      (d/delete-database)
      (is (not (d/database-exists?))))))

(deftest ^:integration test-table
  (let [config {:store {:backend :jdbc
                        :dbtype "mysql"
                        :user "alice"
                        :password "foo"
                        :dbname "config-test"
                        :table "wonderland"}
                :schema-flexibility :write
                :keep-history? false}
        config2 (assoc-in config [:store :table] "tea_party")
        _ (d/delete-database config)
        _ (d/delete-database config2)]
    (is (not (d/database-exists? config)))
    (is (not (d/database-exists? config2)))
    (let [_      (d/create-database config)
          _      (d/create-database config2)
          conn   (d/connect config)
          conn2   (d/connect config2)]
      (d/transact conn [{:db/ident :name
                         :db/valueType :db.type/string
                         :db/cardinality :db.cardinality/one}
                        {:db/ident :age
                         :db/valueType :db.type/long
                         :db/cardinality :db.cardinality/one}])
      (d/transact conn [{:db/id 1, :name  "Ivan", :age   15}
                        {:db/id 2, :name  "Petr", :age   37}
                        {:db/id 3, :name  "Ivan", :age   37}
                        {:db/id 4, :age 15}])
      (is (= (d/q '[:find ?e :where [?e :name]] @conn)
             #{[3] [2] [1]}))
      (is (empty? (d/q '[:find ?e :where [?e :name]] @conn2)))
      (d/release conn)
      (d/release conn2)
      (is (d/database-exists? config))
      (d/delete-database config)
      (is (d/database-exists? config2))
      (d/delete-database config2)
      (is (not (d/database-exists? config)))
      (is (not (d/database-exists? config2))))))

(deftest ^:integration test-signature
  (let [config {:store {:backend :jdbc
                        :dbtype "postgresql"
                        :host "localhost"
                        :dbname "config-test"
                        :user "alice"
                        :password "foo"
                        :table "samezies"}
                :schema-flexibility :read
                :keep-history? false}
        config2 {:store {:backend :jdbc
                         :jdbcUrl "postgresql://alice:foo@localhost/config-test"
                         :table "samezies"}
                 :schema-flexibility :read
                 :keep-history? false}
        config3 {:store {:backend :jdbc
                         :jdbcUrl "postgresql://alice:foo@127.0.0.1/config-test"
                         :table "samezies"}
                 :schema-flexibility :read
                 :keep-history? false}
        config4 {:store {:backend :jdbc
                         :jdbcUrl "postgresql://alice:foo@127.0.0.1/config-test"
                         :table "different"}
                 :schema-flexibility :read
                 :keep-history? false}
        _ (d/delete-database config)]
    (is (not (d/database-exists? config)))
    (let [_ (d/create-database config)
          conn (d/connect config)
          conn2 (d/connect config2)
          conn3 (d/connect config2)]
      (is (not (d/database-exists? config4)))

      (d/transact conn [{:db/id 1, :name  "Ivan", :age   15}
                        {:db/id 2, :name  "Petr", :age   37}
                        {:db/id 3, :name  "Ivan", :age   37}
                        {:db/id 4, :age 15}])
      (is (= (d/q '[:find ?e :where [?e :name]] @conn2)
             #{[3] [2] [1]}))

      (is (= (d/q '[:find ?e :where [?e :name]] @conn3)
             #{[3] [2] [1]}))

      (d/release conn)
      (is (d/database-exists? config))
      (is (d/database-exists? config2))
      (is (d/database-exists? config3))
      (d/delete-database config)
      (is (not (d/database-exists? config)))
      (is (not (d/database-exists? config2)))
      (is (not (d/database-exists? config3))))))