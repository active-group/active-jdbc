(ns active.jdbc.sql
  (:require [active.jdbc.query :as q])
  (:refer-clojure :exclude [and or in list]))

(let [open  ["("]
      close [")"]]
  (defn parens [q]
    (q/concat0 open q close)))

(let [_or_ ["OR"]]
  (defn or [q & qs]
    (if (empty? qs)
      q
      (apply q/concat (interpose _or_ (map parens (cons q qs)))))))

(let [_and_ ["AND"]]
  (defn and [q & qs]
    (if (empty? qs)
      q
      (apply q/concat (interpose _and_ (map parens (cons q qs)))))))

(let [_comma_ [","]]
  (defn list [& things]
    (apply q/concat (interpose _comma_ things))))

(defn value [v]
  ["?" v])

(defn values [& vs]
  (apply list (map value values)))

(let [_in_ ["IN"]
      _not_in_ ["NOT IN"]
      with (fn [x e vs]
             (q/concat e x (parens (apply values vs))))]
  (defn in [e vs]
    (with _in_ e values))

  (defn not-in [e values]
    (with _not_in_ e values)))

(let [_WHERE_ ["WHERE"]]
  (defn where [e]
    (if (some? e)
      (q/concat _WHERE_ e)
      q/empty)))
