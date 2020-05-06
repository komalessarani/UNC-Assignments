(ns write-wtree
  (:require [clojure.java.io :as io])
  (:import (java.security MessageDigest)
           (java.io ByteArrayInputStream ByteArrayOutputStream File)
           (java.util.zip DeflaterOutputStream)))

(defn sha-bytes [bytes]
  (.digest (MessageDigest/getInstance "sha1") bytes))

(defn to-hex-string
  "Convert the given byte array into a hex string, 2 characters per byte."
  [bytes]
  (letfn [(to-hex [byte]
            (format "%02x" (bit-and 0xff byte)))]
    (->> bytes (map to-hex) (apply str))))

(defn zip-str
  "Zip the given data with zlib. Return a ByteArrayInputStream of the zipped
  content."
  [data]
  (let [out (ByteArrayOutputStream.)
        zipper (DeflaterOutputStream. out)]
    (io/copy data zipper)
    (.close zipper)
    (ByteArrayInputStream. (.toByteArray out))))

(defn recursive-tree [path directory database]
  (let [files (.listFiles (io/file path))]
    (doall (for [f (sort files) :when (not= (.getName f) database)]
             (cond
               (and (not= 0 (.listFiles f)) (.isDirectory f))
               (let [content (recursive-tree (.getPath f) directory database)
                     header (str "tree " (reduce + 0 (map count content)) "\000")
                     header+tree (concat (.getBytes header) (apply concat content))
                     tree-content (byte-array header+tree)
                     tree-ad (to-hex-string (sha-bytes tree-content))
                     tree-path (str directory "/" database "/objects/" (subs tree-ad 0 2) "/" (subs tree-ad 2 (count tree-ad)))]
                 (if (not (nil? (seq content)))
                   (do (.mkdir (File. (str directory "/" database "/objects/" (subs tree-ad 0 2))))
                       (io/copy (zip-str tree-content) (io/file tree-path))
                       (concat (.getBytes (str "40000 " (.getName f) "\000")) (sha-bytes tree-content))) nil))
               (.isFile f)
               (let [header+blob (str "blob " (count (slurp f)) "\000" (slurp f))
                     byte (sha-bytes (.getBytes header+blob))
                     file-ad (to-hex-string byte)
                     file-path (str directory "/" database "/objects/" (subs file-ad 0 2) "/" (subs file-ad 2))]
                 (cond (not (.exists (io/file file-path)))
                       (do (.mkdir (File. (str directory "/" database "/objects/" (subs file-ad 0 2))))
                           (io/copy (zip-str header+blob) (io/file file-path))))
                 (concat (.getBytes (str "100644 " (.getName f) "\000")) byte)))))))

(defn write-wtree [directory database args]
  (cond
    (or (seq args) (seq (first args))) (println "Error: write-wtree accepts no arguments")
    (not (.exists (io/file (str directory "/" database)))) (println "Error: could not find database. (Did you run `idiot init`?)")
    (or (and (.exists (io/file (str directory "/" database))) (= 1 (count (.listFiles (io/file directory))))) (= 0 (count (.listFiles (io/file directory))))) (println "The directory was empty, so nothing was saved.")
    :else
    (let [content (keep seq (recursive-tree directory directory database))]
      (if (not (nil? (seq content)))
        (let [header (str "tree " (reduce + 0 (map count content)) "\000")
              header+tree (concat (.getBytes header) (apply concat content))
              tree-content (byte-array header+tree)
              tree-ad (to-hex-string (sha-bytes tree-content))
              tree-path (str directory "/" database "/objects/" (subs tree-ad 0 2) "/" (subs tree-ad 2 (count tree-ad)))]
          (cond (not (.exists (io/file tree-path)))
                (do (.mkdir (File. (str directory "/" database "/objects/" (subs tree-ad 0 2))))
                    (io/copy (zip-str tree-content) (io/file tree-path))))
          (println tree-ad)) ()))))