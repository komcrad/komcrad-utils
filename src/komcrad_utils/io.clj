(ns komcrad-utils.io
  (:gen-class)
  (:require [clojure.java.io :refer [output-stream]]
            [komcrad-utils.wait :refer [wait-for]]
            [clojure.string :as s]))

(defn file [s]
  (clojure.java.io/file s))

(defn existing-file
  "returns a file object based on path s.
   returns nil if file doesn't exist"
  [s]
  (let [file (file s)]
    (when (.exists file) file)))

(defn tmp-file
  "returns a tmp file File object"
  []
  (. java.io.File createTempFile "komcrad-utils-" ".tmp"))

(defn tmp-folder
  "returns a tmp folder File object"
  []
  (let [file (tmp-file)]
    (.delete file)
    (.mkdir file)
    file))

(defn resource-as-file
  "returns a temp file containing resource"
  [resource]
  (let [res (clojure.java.io/resource resource)
        tmp (tmp-file)]
    (with-open [in (clojure.java.io/input-stream res)]
      (clojure.java.io/copy in tmp) tmp)))

(defn tempify
  "returns a file object inside a tmp folder.
   retains original name of file"
  [filename]
  (file (str (.getCanonicalPath (tmp-folder)) "/"
             (.getName (file filename)))))

(defn as-tmp [filename]
  (let [in-file (file filename)
        tmp (tempify filename)]
    (when (.exists in-file)
      (with-open [in (clojure.java.io/input-stream in-file)]
        (clojure.java.io/copy in tmp)))
      tmp))

(defn resource-as-folder-child
  "returns a file (in a tmp folder) with the same name as resource"
  [resource]
  (let [res (clojure.java.io/resource resource)
        tmp (tmp-folder)
        new-file (file (str (.getCanonicalPath tmp) (. java.io.File separator)
                            (last (clojure.string/split (.getPath res) #"/"))))]
    (with-open [in (clojure.java.io/input-stream res)]
      (clojure.java.io/copy in new-file)) new-file))

(defn file-list
  "returns a vector of files found in file (a folder)"
  [file]
  (vec (.listFiles (clojure.java.io/file file))))

(defn files-partial-match
  "given a vector of files, returns a vector of files whos names match filename"
  [files filename]
  (filter #(.contains (.getName %) filename) files))

(defn wait-file
  "waits for a file described by path s. returns file or nil"
  ([s timeout]
   (wait-for #(existing-file s) timeout 50))
  ([s]
   (wait-file s 5000)))

(defn delete-file
  "deletes file recursively"
  [file]
  (loop [files (vec [(clojure.java.io/file file)])]
    (when (not (empty? files))
      (let [f (first files)]
        (if (and (.isDirectory f) (> (count (.listFiles f)) 0))
            (recur (concat (vec (.listFiles f)) files))
            (do (.delete f)
                (recur (rest files))))))))

(defn parent
  "return parent of file"
  [file]
  (.getParent file))

(defn delete-file-parent
  "calls delete-file on parent of file"
  [file]
  (delete-file (parent file)))

(defn smart-delete
  "calls delete-file on parent of file if it matches ^komcrad-utils"
  [file]
  (if (re-find #"^komcrad-utils" (.getName (.getParentFile file)))
    (delete-file-parent file)
    (delete-file file)))

(defn file-move
  [input-file output-file]
  (clojure.java.io/copy input-file output-file)
  (if (.exists output-file)
    (do (delete-file (.getPath input-file)) true)))

(defn file-move-tmp [input-file]
  (let [tmp-dir (tmp-folder)
        tmp-file (file (str (.getPath tmp-dir) (. java.io.File separator)
                            (.getName input-file)))]
    (file-move input-file tmp-file) tmp-file))

(defmacro with-tmp-file
  "executes body and insures the file is deleted"
  [file & body]
  `(let [file# ~file]
     (try
       ~@body
       (finally (delete-file file#)))))

(defmacro with-tmp-files
  "executes body and insures the files are deleted"
  [files & body]
  `(let [files# ~files]
     (try
       ~@body
       (finally (doseq [x# files#] (delete-file x#))))))

(defmacro with-tmps
  "Executes body and insures the files are deleted.
   Syntax similar to let:
   (with-tmps [file1 (tmp-file) file2 (resource-as-file \"resource\")]
     (println file1 file2))"
  [bindings & body]
  `((fn [~@(take-nth 2 bindings)]
      (try
        ~@body
        (finally
          (doseq [x# [~@(take-nth 2 bindings)]]
            (komcrad-utils.io/delete-file x#)))))
    ~@(take-nth 2 (rest bindings))))

(defmacro with-tmp-folder-children
  "executes body and insures the files are deleted
   along with their parents"
  [files & body]
  `(let [files# ~files]
     (try
       ~@body
       (finally (doseq [x# files#] (delete-file (parent x#)))))))

(defmacro with-tf
  "binds the given symbol to a tmpfile, executes body with access
   to the tmpfile and insures the file gets deleted
   Example: (with-tf [my-file]
              (spit my-file \"hello\")
              (slurp my-file))"
  [[file] & body]
  `(let [~file (komcrad-utils.io/tmp-file)]
     (try
       ~@body
       (finally (delete-file ~file)))))

(defn host-port-listening?
  "returns true if a host at ip is listing on port n"
  [ip n]
  (try
    (let [s (new java.net.Socket)]
      (.connect s (new java.net.InetSocketAddress ip n) 5000)
      (.close s) true)
    (catch Exception e false)))

(defn available-port? [n]
  (try
    (let [socket (java.net.ServerSocket. n)]
      (.close socket) true)
    (catch Exception e false)))

(defn available-port []
  (let [socket (java.net.ServerSocket. 0)]
    (.close socket)
    (.getLocalPort socket)))

(defn local-ip []
  (.getHostAddress (java.net.InetAddress/getLocalHost)))

(defn get-local-interfaces []
  (let [interfaces (. java.net.NetworkInterface getNetworkInterfaces)]
    (if (.hasMoreElements interfaces)
      (loop [interface (.nextElement interfaces) result []]
        (if (.hasMoreElements interfaces)
          (recur (.nextElement interfaces) (conj result interface))
          (conj result interface))))))

(defn filter-interfaces [s]
  (vec (filter (fn [x] (.contains (.getName x) s)) (get-local-interfaces))))

(defn ipv4-from-interface
  [^java.net.NetworkInterface interface]
  (.getHostAddress (.getAddress (first (filter #(= 32 (.getNetworkPrefixLength %))
                                               (.getInterfaceAddresses interface))))))

(defn touch-tmp
  "creates a file named name in a tmp dir"
  [name]
  (let [parent (tmp-folder)]
    (file (str (.getCanonicalPath parent) (. java.io.File separator) name))))

(defn zero-fill
  "adds n bytes to file"
  [file n]
  (with-open [out (output-stream file)]
    (.write out (byte-array n))) file)

(defn read-stream [is]
  (loop [result []]
    (if (< 0 (.available is))
      (recur (conj result (char (.read is))))
      (do
        (Thread/sleep 20)
        (if (< 0 (.available is))
          (recur result) result)))))

(defn stream-as-atom [is]
  (let [a (atom {:continue true :val ""})
        fu (future
             (while (:continue @a)
               (swap! a #(merge-with str %1 %2)
                      {:val (s/join "" (read-stream is))})
               (Thread/sleep 500))
             (.close is))] a))

(defmacro with-streams-as-atoms
  [bindings & body]
  `(apply (fn [~@(take-nth 2 bindings)]
     ~@body
     (doseq [m# [~@(take-nth 2 bindings)]]
       (swap! m# merge {:continue false})))
    (map #(stream-as-atom %) [~@(take-nth 2 (rest bindings))])))
