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

(defn ->vec [x]
  (if (vector? x) x (vector x)))

(defn add-parents [parent m]
  (reduce-kv
   (fn [m k v]
     (assoc m (into [] (flatten [parent k])) v))
   {} m))

(defn remove-top-level [m]
  (reduce-kv
   (fn [m k v]
     (if (map? v)
       (merge m (add-parents k v))
       (assoc m (->vec k) v)))
   {} m))

(defn map->flat-paths [m]
  (clojure.walk/postwalk #(if (map? %) (remove-top-level %) %) m))

(defn expand-paths [m]
  (reduce-kv
   (fn [m k v]
     (assoc-in m k v))
   {} m))

(defn flat-paths->map [m]
  (clojure.walk/postwalk #(if (map? %) (expand-paths %) %) m))

(comment
  (flat-paths->map
   (map->flat-paths
    {:person {:name "bob"
              :age 34
              :children [{:person {:name "james" :age 12}}
                         {:person {:name "john" :age 15}}]}
     :name "bob"})))
