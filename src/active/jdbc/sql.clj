(ns active.jdbc.sql
  (:require [active.jdbc.query :as q])
  (:refer-clojure :exclude [and or]))

(let [_or_ ["OR"]]
  (defn or [q & qs]
    (apply q/concat (interpose _or_ (cons q qs)))))

(let [_and_ ["AND"]]
  (defn and [q & qs]
    (apply q/concat (interpose _and_ (cons q qs)))))
