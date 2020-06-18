(ns datahike-backend-template.core-test
 (:require
    #?(:cljs [cljs.test    :as t :refer-macros [is are deftest testing]]
       :clj  [clojure.test :as t :refer        [is are deftest testing]])
    [datahike.api :as d]
    [datahike-backend-template.core]))

(deftest test-backend-store-config
  (let [config {:store {:backend :backendname
                        :user "datahike"
                        :password "datahike"
                        :host "localhost"
                        :port 12345
                        :dbname "datahike"}
                :schema-flexibility :read
                :keep-history? false}
        _ (d/delete-database config)]
    (is (not (d/database-exists? config)))
    (let [_ (d/create-database config)
          conn (d/connect config)]

      (d/transact conn [{ :db/id 1, :name  "Ivan", :age   15}
                        { :db/id 2, :name  "Petr", :age   37}
                        { :db/id 3, :name  "Ivan", :age   37}
                        { :db/id 4, :age 15}])
      (is (= (d/q '[:find ?e :where [?e :name]] @conn)
             #{[3] [2] [1]}))

      (d/release conn)
      (is (d/database-exists? config))
      (d/delete-database config))))
