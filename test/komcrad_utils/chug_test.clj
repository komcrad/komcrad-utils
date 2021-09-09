(ns komcrad-utils.chug-test
  (:require [clojure.test :refer :all]
            [komcrad-utils.chug :refer :all]
            [bond.james :as bond]
            [clojure.java.io :as io]))

(deftest run-this-test
  (run-this "chug-test" inc (range 10000))
  (bond/with-spy [inc] 
    (run-this "chug-test" inc (range 10000))
    (is (= 0 (-> inc bond/calls count))))
 
  (= (map inc (range 10000)) 
     (map #(:result (read-frozen-result "chug-test" %)) (range 10000)))
  (= (map inc (range 10000)))
  (sort (read-results "chug-test")) 
  ; cleanup 
  (doseq [x (reverse (file-seq (io/file (working-dir "chug-test"))))] 
    (io/delete-file x)))
