(ns komcrad-utils.chug
  (:require [taoensso.nippy :as nip]
            [clojure.java.io :as io]
            [clojure.walk :as walk]))

(defn keep-serializable [coll]
  (walk/prewalk #(if (instance? java.io.Serializable %) % nil) coll))

(defn freeze-result [working-directory f x]
  (let [res (try {:result (f x)}
                 (catch Exception e
                   {:err (str e)}))
        file (str working-directory (hash x))]
    (io/make-parents file)
    (nip/freeze-to-file file (keep-serializable res))
    (or (:result res) (:err res))))

(defn working-dir [label]
  (str (System/getProperty "java.io.tmpdir") "/" label "/"))

(defn read-frozen-result [label x]
  (try
    (nip/thaw-from-file (str (working-dir label) (hash x)))
    (catch Exception e nil)))

(defn freeze-or-read [label f x]
  (let [res (read-frozen-result (working-dir label) x)]
    (cond
      (or (nil? res) (:err res)) (freeze-result (working-dir label) f x)
      :else (freeze-result (working-dir label) f x))))

(defn read-results [label]
  (map
    #(let [res (nip/thaw-from-file %)]
       (or (:err res) (:result res)))
    (rest (file-seq (io/file (working-dir label))))))

(defn run-this [label f coll]
  (run! #(freeze-or-read label f %) coll))

(comment
  (do
    (run-this "thing" inc (range 10000))
    (sort (read-results "thing"))))


