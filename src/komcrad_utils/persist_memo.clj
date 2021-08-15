(ns komcrad-utils.persist-memo
  (:require [clojure.core.memoize :as memo]
            [taoensso.nippy :as nippy]
            [clojure.java.io :as io]))

(defn persist-memo 
  "Creates a 'persistent' memoized function. We'll persist the cache in a naive way.
   This shouldn't be thread safe. Existence is to see if this will be a useful development tool."
  [f ttl-ms file]
  (let [memoed (memo/ttl f :ttl/threshold ttl-ms)]
    (when (.exists (io/file file))
      (memo/memo-reset! memoed (nippy/thaw-from-file file)))
    (add-watch 
      (#'memo/cache-id memoed)
      :watcher
      (fn [key atom old-state new-state]
        (nippy/freeze-to-file file (memo/snapshot memoed))))
    memoed))
