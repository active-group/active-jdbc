(ns active.jdbc.query-test
  (:require [clojure.test :refer (deftest testing is)]
            [active.jdbc.query :as q]))

(deftest concat-test
  (is (= q/empty (q/concat)))

  (is (= ["foo ?" 42] (q/concat ["foo ?" 42])))
  
  (is (= ["foo ? bar ?" 42 0]
         (q/concat ["foo ?" 42]
                   ["bar ?" 0])))

  (is (= ["foo ? bar ? baz" 42 0]
         (q/concat ["foo ?" 42]
                   ["bar ?" 0]
                   ["baz"])))
  )

(deftest resolve-abstract-test
  (is (= ["foo ?" 42]
         (q/resolve (q/by-connection (fn [x] ["foo ?" x]))
                    42)))

  (is (= ["foo ? bar ? baz" 42 0]
         (q/resolve (q/concat ["foo ?" 42]
                              (q/by-connection (fn [x] ["bar ?" x]))
                              ["baz"])
                    0)))
  )
