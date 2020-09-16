# Datahike JDBC Backend

<p align="center">
<a href="https://clojurians.slack.com/archives/CB7GJAN0L"><img src="https://img.shields.io/badge/clojurians%20slack-join%20channel-blueviolet"/></a>
<a href="https://clojars.org/io.replikativ/datahike-jdbc"> <img src="https://img.shields.io/clojars/v/io.replikativ/datahike-jdbc.svg" /></a>
<a href="https://circleci.com/gh/replikativ/datahike-jdbc"><img src="https://circleci.com/gh/replikativ/datahike-jdbc.svg?style=shield"/></a>
<a href="https://github.com/replikativ/datahike-jdbc/tree/development"><img src="https://img.shields.io/github/last-commit/replikativ/datahike-jdbc/development"/></a>
<a href="https://versions.deps.co/replikativ/datahike-jdbc" title="Dependencies Status"><img src="https://versions.deps.co/replikativ/datahike-jdbc/status.svg" /></a>
</p>

The goal of this backend is to support all JDBC backends in one with no penalty in performance.
Since there are plenty of databases to support and JDBC gives us a sufficient interface to a
lot of them we chose to use JDBC as a backend instead of implementing them one by one. Another
goal is that this backend supersedes [datahike-postgres](https://github.com/replikativ/datahike-postgres/).

## Migrate from datahike-postgres to datahike-jdbc
The migration should be seamless. First you update the dependencies from `datahike-postgres` to
`datahike-jdbc`. Then you adapt the configuration as follows.

## Configuration
Please read the [Datahike configuration docs](https://github.com/replikativ/datahike/blob/master/doc/config.md) on how to configure your backend. A sample configuration for PostgreSQL for passing as parameter to e.g.
`create-database`, `connect` and `delete-database`:
```clojure
{:store {:backend :jdbc
         :dbtype "postgresql"
         :user "datahike"
         :password "datahike"
         :dbname "datahike"}}
```
This same configuration can be achieved by setting one environment variable for the jdbc backend
and one environment variable for the configuration of the jdbc backend:
```bash
DATAHIKE_STORE_BACKEND=jdbc
DATAHIKE_STORE_CONFIG='{:dbtype "postgresql" :user "datahike" :password "datahike" :dbname "datahike"}'
```

It is also possible to pass a configuration url via `:jdbcUrl` like it is mentioned in the underlying library [next.jdbc](https://cljdoc.org/d/seancorfield/next.jdbc/1.0.462/doc/getting-started#the-db-spec-hash-map). The Url can pass additional arguments in the query part or you can combine the url with arguments passed as key-value-pairs.

Arguments not mentioned will be passed downstream to the corresponding jdbc-driver so every configuration option available should be working.

## Prerequisites
For this backend to work you need to choose a database that is supported by JDBC. Please have a
look at the docs for [clojure.java.jdbc](https://github.com/clojure/java.jdbc/). For the sake
of comparability we will choose Postgres here.

For this manual to work you need to have a PostgreSQL server running and be able to connect to
it from your machine. There are several ways to run PostgreSQL but the easiest might be to use
a container. Please find a [manual on how to run PostgreSQL](https://hub.docker.com/_/postgres) in a container on dockerhub.

On your local machine your setup prerequisites could look something like this:
1. [Running Docker locally](https://docs.docker.com/engine/)
2. A running instance of PostgreSQL that you can connect to.
3. [A JDK to run your Clojure on](https://clojure.org/guides/getting_started)
4. [Leiningen, Boot or Clojure CLI to run your code or a REPL](https://leiningen.org/#install)

We will stick with Leiningen for this manual. If you want to use MySQL or H2 for a try, please
have a look into the tests to see how to configure these backend stores.

## Usage
Add to your Leiningen or Boot dependencies:
[![Clojars Project](https://img.shields.io/clojars/v/io.replikativ/datahike-jdbc.svg)](https://clojars.org/io.replikativ/datahike-jdbc)

Now require the Datahike API and the datahike-jdbc namespace in your editor or REPL using the
keyword ~:jdbc~. If you want to use other backends than JDBC please refer to the official
[Datahike docs](https://github.com/replikativ/datahike/blob/master/doc/config.md).

### Run PostgreSQL on Docker
```bash
  docker run --detach --publish 5432:5432 --env POSTGRES_DB=config-test --env POSTGRES_USER=alice --env POSTGRES_PASSWORD=foo postgres:alpine
```

### Run Datahike in your REPL
```clojure
  (ns project.core
    (:require [datahike.api :as d]
              [datahike-jdbc.core])

  ;; This configuration suits the config of the container started above.
  ;; In case you run your PostgreSQL instance with other settings, please
  ;; adjust this configuration
  (def cfg {:store {:backend :jdbc
                    :dbtype "postgresql"
                    :host "localhost"
                    :port 5432
                    :user "alice"
                    :password "foo"
                    :dbname "config-test"}})

  ;; Create a database at this place, by default configuration we have a strict
  ;; schema validation and keep historical data
  (d/create-database cfg)

  (def conn (d/connect cfg))

  ;; The first transaction will be the schema we are using:
  (d/transact conn [{:db/ident :name
                     :db/valueType :db.type/string
                     :db/cardinality :db.cardinality/one }
                    {:db/ident :age
                     :db/valueType :db.type/long
                     :db/cardinality :db.cardinality/one }])

  ;; Let's add some data and wait for the transaction
  (d/transact conn [{:name  "Alice", :age   20 }
                    {:name  "Bob", :age   30 }
                    {:name  "Charlie", :age   40 }
                    {:age 15 }])

  ;; Search the data
  (d/q '[:find ?e ?n ?a
         :where
         [?e :name ?n]
         [?e :age ?a]]
    @conn)
  ;; => #{[3 "Alice" 20] [4 "Bob" 30] [5 "Charlie" 40]}

  ;; Clean up the database if it is not needed any more
  (d/delete-database cfg)
```

## Run Tests

```bash
  bash -x ./bin/run-integration-tests
```

## License

Copyright © 2020 lambdaforge UG (haftungsbeschränkt)

This program and the accompanying materials are made available under the terms of the Eclipse Public License 1.0.
