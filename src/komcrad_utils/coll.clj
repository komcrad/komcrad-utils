(ns komcrad-utils.coll
  (:require [clojure.data.xml :as xml])
  (:gen-class))

(defn query-key [m s]
  (filter #(clojure.string/includes? % s) (map str (keys m))))

(defn pprint-xml [s]
  (-> s xml/parse-str xml/indent-str print))

(defn get-key [m coll]
  (some #(% m) coll))

(defn order-symbols []
  (-> (clojure.string/replace (slurp "/tmp/file.file") #"\s+" ",")
      (clojure.string/split #",")
      sort
      ((fn [coll] (map keyword coll)))))

(defn sim-keys [m s]
  (->> (filter #(.contains (clojure.string/lower-case (name (key %))) s) m)
       (into (sorted-map))))

(defn swap [coll i1 i2]
  (assoc coll i1 (coll i2) i2 (coll i1)))

(defn permutate [coll i]
  (map #(swap coll i %) (range i (count coll))))

(defn permutations [coll]
  (loop [result [coll] i (range (dec (count coll)))]
    (if (empty? i)
      result
      (recur (mapcat #(permutate % (first i)) result) (rest i)))))

(defn strpermutations [s]
  (->> (into [] s)
      permutations
      (map clojure.string/join)))
