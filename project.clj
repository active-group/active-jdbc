(defproject de.active-group/active-jdbc "0.1.0"
  :description "Functions to work with JDBC building upon next.jdbc."
  :url "http://github.com/active-group/active-jdbc"
  
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :dependencies [[org.clojure/clojure "1.10.1"]
                 [com.github.seancorfield/next.jdbc "1.2.780"]]

  :codox {:language :clojure
          :metadata {:doc/format :markdown}
          :themes [:rdash]
          :src-dir-uri "http://github.com/active-group/active-jdbc/blob/master/"
          :src-linenum-anchor-prefix "L"}
  )
