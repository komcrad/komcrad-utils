(defproject komcrad-utils "0.13.0-SNAPSHOT"
  :description "komcrad's utilities"
  :url "https://github.com/komcrad/komcrad-utils"
  :license {:name "GNU Lesser General Public License"
            :url "https://www.gnu.org/licenses/lgpl-3.0.en.html"}
  :dependencies [[org.clojure/clojure "1.10.1"]
                 [org.clojure/data.xml "0.0.8"]
                 [nrepl "0.7.0"]
                 [com.taoensso/nippy "3.1.1"]
                 [org.clojure/data.csv "1.0.0"]
                 [org.exupero/clipboard "0.1.0"]
                 [org.clojure/core.memoize "1.0.250"]]
  :profiles {:dev {:dependencies [[circleci/bond "0.6.0"]]}})
