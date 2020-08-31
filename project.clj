(defproject io.replikativ/datahike-jdbc "0.1.1-SNAPSHOT"
  :description "Datahike with JDBC data storage backend"
  :license {:name "Eclipse"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :url "http://github.com/replikativ/datahike-jdbc"
  :dependencies [[org.clojure/clojure "1.10.1" :scope "provided"]
                 [environ "1.2.0"]
                 [com.taoensso/timbre "4.10.0"]
                 [io.replikativ/datahike "0.3.2-SNAPSHOT" :exclusions [io.replikativ/superv.async]]
                 [alekcz/konserve-jdbc "0.1.0-SNAPSHOT"]]
  :deploy-repositories
  [["clojars"
    {:url           "https://clojars.org/repo"
     :username      :env
     :password      :env
     :sign-releases false}]])
