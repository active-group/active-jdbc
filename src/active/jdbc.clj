(ns active.jdbc
  (:require [next.jdbc :as next]
            [active.jdbc.query :as q])
  (:import [java.sql Statement]
           [clojure.lang IReduceInit]))

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

(defn execute-batch!
  ([connectable sql param-groups]
   (execute-batch! connectable sql param-groups nil))
  ([connectable sql param-groups opts]
   ;; Note: the sql should probably have no parameters set yet; not sure if next could handle that.
   (next/on-connection [connection connectable]
                       (with-open [stmt (prepare connection sql opts)]
                         (next/execute-batch! stmt param-groups opts)))))

(defn execute-one!
  ([connectable sql-params]
   (execute-one! connectable sql-params nil))
  ([connectable sql-params opts]
   (wrap next/execute-one! connectable sql-params opts)))

(defn- delayed-reduction [g]
  (reify IReduceInit
    (reduce [this f init]
      (g (fn [coll]
           (reduce f init coll))))))

(defn plan
  ([connectable sql-params]
   (plan connectable sql-params nil))
  ([connectable sql-params opts]
   (delayed-reduction
    (fn [red]
      (wrap (comp red next/plan)
            connectable sql-params opts)))))
