(defproject io.replikativ/datahike-jdbc "0.1.1"
  :description "Datahike with JDBC data storage backend"
  :license {:name "Eclipse"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :url "http://github.com/replikativ/datahike-jdbc"
  :dependencies [[org.clojure/clojure "1.10.1" :scope "provided"]
                 [environ "1.2.0"]
                 [com.taoensso/timbre "5.0.1"]
                 [io.replikativ/datahike "0.3.2" :exclusions [io.replikativ/superv.async]]
                 [alekcz/konserve-jdbc "0.1.0-SNAPSHOT"]]
  :plugins [[lein-cljfmt "0.7.0"]]
  :deploy-repositories
  [["clojars"
    {:url           "https://clojars.org/repo"
     :username      :env
     :password      :env
     :sign-releases false}]])
