(ns datahike-jdbc.core-test
  (:require
   #?(:cljs [cljs.test    :as t :refer-macros [deftest]]
      :clj  [clojure.test :as t :refer        [deftest]])
   [datahike.integration-test :as dt]
   [datahike-jdbc.core]))

(deftest ^:integration test-postgresql []
  (let [config {:store {:backend :jdbc
                        :dbtype "postgresql"
                        :host "localhost"
                        :dbname "config-test"
                        :user "alice"
                        :password "foo"}
                :schema-flexibility :read
                :keep-history? false}]
    (dt/integration-test config)))

(deftest ^:integration test-mysql []
  (let [config {:store {:backend :jdbc
                        :dbtype "mysql"
                        :user "alice"
                        :password "foo"
                        :dbname "config-test"}
                :schema-flexibility :write
                :keep-history? false}]
    (dt/integration-test config)))

(deftest ^:integration test-mssql []
  (let [config {:store {:backend :jdbc
                        :dbtype "sqlserver"
                        :user "sa"
                        :password "passwordA1!"
                        :dbname "tempdb"}
                :schema-flexibility :write
                :keep-history? false}]
    (dt/integration-test config)))

(deftest ^:integration test-h2 []
  (let [config {:store {:backend :jdbc
                        :dbtype "h2"
                        :dbname "./temp/db"}
                :schema-flexibility :write
                :keep-history? false}]
    (dt/integration-test config)))

(deftest ^:integration test-env []
  (let [config {:name "test-env"}]
    (dt/integration-test config)))
