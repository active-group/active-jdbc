(ns active.jdbc.query
  (:require [clojure.core :as clj])
  (:import [java.sql Statement DriverManager Driver Connection DatabaseMetaData]
           [java.lang StringBuilder])
  (:refer-clojure :exclude [resolve concat empty]))

(defrecord ^:private AbstractQuery [f])

(defn- abstract? [v]
  (instance? AbstractQuery v))

(defn- abstract [f]
  (AbstractQuery. f))

(defn resolve [query connection]
  (loop [query query]
    (if (abstract? query)
      (recur ((:f query) connection))
      query)))

(def empty [""])

(declare concat0)

(defn- concat0-d [before f after]
  (abstract
   (fn [connection]
     (concat0 before (resolve f connection) after))))

(defn- concat0-s [^String sql params & more]
  (loop [result-sb (new StringBuilder sql)
         result-ps params
         rem more]
    (if (empty? rem)
      (apply vector (.toString result-sb) result-ps)
      (let [n (first rem)]
        (if-not (abstract? n)
          (recur (.append result-sb (first n))
                 (clj/concat result-ps (rest n))
                 (rest rem))
          ;; next is function, so delay final concatenation:
          (let [before (apply vector (.toString result-sb) result-ps)
                after (apply concat0 (rest rem))]
            (concat0-d before n after)))))))

(defn concat0 [& queries]
  (if (empty? queries)
    empty
    (let [n (first queries)]
      (if-not (abstract? n)
        (apply concat0-s (first n) (rest n)
               (rest queries))
        (concat0-d empty n (apply concat0 (rest queries)))))))

(def ^:private space [" "])

(defn concat [& queries]
  (apply concat0 (interpose space queries)))

(defn by-connection [f]
  (abstract f))

(defn by-meta-data [f]
  (abstract
   (fn [^Connection connection]
     (f (.getMetaData connection)))))

(defn by-driver-name [f]
  (by-meta-data (fn [^DatabaseMetaData meta]
                  (let [dn (.getDriverName meta)
                        r (f dn)]
                    (assert (some? r) (str "Query not defined for driver name: " dn))
                    r))))

(defn- find-driver-for [url]
  ;; TODO: possible to memoize with a certain driver list?
  (reduce (fn [res ^Driver d]
            (if (.acceptsURL d url)
              (reduced d)
              res))
          nil
          (enumeration-seq (DriverManager/getDrivers))))

(defn by-driver [f]
  (by-meta-data (fn [^DatabaseMetaData meta]
                  (let [url (.getURL meta)]
                    (if-let [^Driver d (find-driver-for url)]
                      (f d)
                      (throw (ex-info (str "Could not find JDBC driver of connection.") {:meta meta
                                                                                         :driver-name (.getDriverName meta)})))))))

(defn by-driver-class-name [f]
  (by-driver (fn [^Driver driver]
               (f (.getName (.getClass driver))))))
