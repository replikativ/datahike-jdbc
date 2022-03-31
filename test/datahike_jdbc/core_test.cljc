(ns datahike-jdbc.core-test
  (:require
   #?(:cljs [cljs.test    :as t :refer-macros [is deftest]]
      :clj  [clojure.test :as t :refer        [is deftest]])
   [datahike.api :as d]))

(defn delete-db [config]
  (d/delete-database config)
  (is (not (d/database-exists? config))))

(defn setup [config]
  (let [_ (delete-db config)
        _ (d/create-database config)
        conn (d/connect config)]
    (d/transact conn [{:db/ident :name
                       :db/valueType :db.type/string
                       :db/cardinality :db.cardinality/one}
                      {:db/ident :age
                       :db/valueType :db.type/long
                       :db/cardinality :db.cardinality/one}])
    (d/transact conn [{:name  "Alice", :age   20}
                      {:name  "Bob", :age   30}
                      {:name  "Charlie", :age   40}
                      {:age 15}])
    (d/release conn)))

(defn integration-test
  [config]
  (let [conn (d/connect config)]
    (is (= #{[3 "Alice" 20] [4 "Bob" 30] [5 "Charlie" 40]}
           (d/q '[:find ?e ?n ?a
                  :where
                  [?e :name ?n]
                  [?e :age ?a]]
                @conn)))
    (d/transact conn {:tx-data [{:db/id 3 :age 25}]})
    (is (= #{[5 "Charlie" 40] [4 "Bob" 30] [3 "Alice" 25]}
           (d/q {:query '{:find [?e ?n ?a]
                          :where [[?e :name ?n]
                                  [?e :age ?a]]}
                 :args [@conn]})))
    (is (= #{[20] [25]}
           (d/q '[:find ?a
                  :where
                  [?e :name "Alice"]
                  [?e :age ?a]]
                (d/history @conn))))
    (is (= nil (d/release conn)))
    (is (d/database-exists? config))
    (delete-db config)))

(def jdbc-base-config {:store {:backend :jdbc}
                       :schema-flexibility :write})

(deftest ^:integration test-postgresql []
  (let [config (merge jdbc-base-config
                      {:store {:dbtype "postgresql"
                               :jdbcUrl "jdbc:postgresql://localhost/config-test?user=alice"
                               :password "foo"}})]
    (setup config)
    (integration-test config)))

(deftest ^:integration test-mysql []
  (let [config (merge jdbc-base-config
                      {:store {:dbtype "mysql"
                               :user "alice"
                               :password "foo"
                               :dbname "config-test"}})]
    (setup config)
    (integration-test config)))

(deftest ^:integration test-h2 []
  (let [config (merge jdbc-base-config
                      {:store {:dbtype "h2"
                               :dbname "./temp/db"}})]
    (setup config)
    (integration-test config)))

(deftest ^:integration test-env []
  (let [config {:name "test-env"}]
    (setup config)
    (integration-test config)))

(comment
  (require '[clojure.test :refer [run-tests test-vars]])
  (run-tests))
