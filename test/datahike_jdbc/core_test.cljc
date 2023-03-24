(ns datahike-jdbc.core-test
  (:require
   #?(:cljs [cljs.test    :as t :refer-macros [deftest]]
      :clj  [clojure.test :as t :refer        [deftest]])
   [datahike.integration-test :as dt]
   [datahike-jdbc.core]))

(def jdbc-base-config {:store {:backend :jdbc}
                       :schema-flexibility :write})

(deftest ^:integration test-postgresql []
  (let [config (update jdbc-base-config :store
                       merge {:dbtype "postgresql"
                              :jdbcUrl "jdbc:postgresql://localhost/config-test?user=alice"
                              :password "foo"})]
    (dt/integration-test config)))

(deftest ^:integration test-mysql []
  (let [config (update jdbc-base-config :store
                       merge {:dbtype "mysql"
                              :user "alice"
                              :password "foo"
                              :dbname "config-test"})] 
    (dt/integration-test config)))

(deftest ^:integration test-h2 []
  (let [config (update jdbc-base-config :store
                       merge {:dbtype "h2"
                              :dbname "./temp/db"})]
    (dt/integration-test config)))

(deftest ^:integration test-env []
  (let [config {:name "test-env"}]
    (dt/integration-test config)))
