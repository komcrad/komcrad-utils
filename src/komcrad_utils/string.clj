(ns komcrad-utils.string
  (:gen-class))

(defn literal [s]
  (.replace s "\"" "\\\""))

(defn replace-several [content & replacements]
  (let [replacement-list (partition 2 replacements)]
    (reduce #(apply clojure.string/replace %1 %2) content replacement-list)))

(defn between-pattern [prefix suffix]
  (re-pattern (str (literal prefix) "([\\s\\S]*?)" (literal suffix))))

(defn between [s prefix suffix]
  (second (re-find (between-pattern prefix suffix) s)))

(defn between-seq [s prefix suffix]
  (map #(second %) (re-seq (between-pattern prefix suffix) s)))

(defn find-previous-space
  [s index]
  (if (= \space (nth s index))
    index
    (recur s (dec index))))

(defn retain-words-split
  [s index]
  (let [max-index (find-previous-space s index)]
    [(subs s 0 max-index)
     (subs s (inc max-index))]))

(defn line-wrap
  [s max-length]
  (loop [res [] s s]
    (if (< (count s) max-length)
      (conj res s)
      (let [thing (retain-words-split s max-length)]
        (recur (conj res (first thing))
               (second thing))))))

(defn rand-word  [len]
  (clojure.string/join ""  (take len  (repeatedly #(char  (+ 97  (rand-int 26)))))))

(defn rand-csv  [columns rows]
  (let  [rows  (take rows  (repeatedly  (fn  []  (take columns  (repeatedly  (fn  []  (rand-word 5)))))))]
    (map  (fn  [x]  (clojure.string/join "," x)) rows)))

(comment
  ; generates a 1.7Gb file. It's quite slow
  (run! #(spit "/tmp/test.csv" (str % "\n") :append true) (rand-csv 100 3000000))
  )
