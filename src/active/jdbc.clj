(ns active.jdbc
  (:require [next.jdbc :as next]
            [active.jdbc.query :as q])
  (:import [java.sql Statement]))

(defn prepare
  (^Statement [connection sql-params]
   (prepare connection sql-params nil))
  (^Statement [connection sql-params opts]
   (next/prepare connection (q/resolve sql-params connection) opts)))

(defn- wrap [f connectable sql-params opts]
  (next/on-connection [connection connectable]
                      (with-open [stmt (prepare connection sql-params opts)]
                        (f stmt [] opts))))

(defn execute!
  ([connectable sql-params]
   (execute! connectable sql-params nil))
  ([connectable sql-params opts]
   (wrap next/execute! connectable sql-params opts)))

#_(defn execute-batch!)

(defn execute-one!
  ([connectable sql-params]
   (execute-one! connectable sql-params nil))
  ([connectable sql-params opts]
   (wrap next/execute-one! connectable sql-params opts)))

(defn plan
  ([connectable sql-params]
   (plan connectable sql-params nil))
  ([connectable sql-params opts]
   (wrap next/plan connectable sql-params opts)))
