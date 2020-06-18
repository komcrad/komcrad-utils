(ns komcrad-utils.csv
  (:require [clojure.data.csv :as csv]))

(defn csv->edn
  [s]
  (let  [records  (clojure.data.csv/read-csv s)]
    (map #(zipmap  (first records) %)  (rest records))))
