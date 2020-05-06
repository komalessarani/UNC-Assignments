(ns idiot-spec-utils
  (:require [clojure.java.io :as io]
            [clojure.string :as str])
  (:import (java.io InputStream
                    File
                    ByteArrayInputStream
                    ByteArrayOutputStream)
           (java.security MessageDigest)
           (java.util.zip DeflaterOutputStream
                          InflaterInputStream)))

(declare rm-rf)

(defn rm-rf-one
  [file-or-dir]
  ;; Thanks to
  ;; https://github.com/hozumi/clj-rm-rf/blob/master/src/hozumi/rm_rf.clj for
  ;; the approach, which I modified slightly. (EPL licensed so OK to use.)
  (when (.isDirectory file-or-dir)
    (apply rm-rf (.listFiles file-or-dir)))
  (io/delete-file file-or-dir true))

(defn rm-rf
  "Remove the given files or directories. Tolerate missing files/dirs
  gracefully."
  [& files-or-dirs]
  (dorun (map rm-rf-one files-or-dirs)))

(comment
  (let [filename "test-file"]
    (spit filename "hello")
    (rm-rf-one (io/file filename)))
  (let [dirname "test-dir"
        filename (str dirname "/file")]
    (.mkdirs (io/file filename))
    (spit filename "hello")
    (rm-rf-one (io/file dirname)))
  (let [filename1 "test-file"
        dirname "test-dir"
        filename2 (str dirname "/file")]
    (spit filename1 "hello")
    (.mkdirs (io/file filename2))
    (spit filename2 "hello")
    (rm-rf (io/file dirname) (io/file filename1)))
  )

(defn data->blob
  [string]
  (format "blob %d\000%s"
          (count string)
          string))

(defn zip-str
  "Zip the given data with zlib. Return a byte-array of the zipped content."
  [data]
  (let [out (ByteArrayOutputStream.)
        zipper (DeflaterOutputStream. out)]
    (io/copy data zipper)
    (.close zipper)
    (ByteArrayInputStream. (.toByteArray out))))

(defn unzip
  "Unzip the given file's contents with zlib."
  [path]
  (with-open [input (-> path io/file io/input-stream)
              unzipper (InflaterInputStream. input)
              out (ByteArrayOutputStream.)]
    (io/copy unzipper out)
    (->> (.toByteArray out)
         (map char)
         (apply str))))

(comment
  (.readAllBytes (zip-str "something\n"))
  (count *1)
  (io/copy (zip-str (str "blob 10\000something\n")) (io/file "clj-zipped"))
  (-> "something\n" data->blob zip-str (io/copy (io/file "clj-zipped")))
  )

(defn sha1-hash-bytes [data]
  (.digest (MessageDigest/getInstance "sha1")
           (.getBytes data)))

(defn byte->hex-digits [byte]
  (format "%02x"
          (bit-and 0xff byte)))

(defn bytes->hex-string [bytes]
  (->> bytes
       (map byte->hex-digits)
       (apply str)))

(defn sha1-sum [data]
  (bytes->hex-string (sha1-hash-bytes data)))

(comment
  (sha1-hash-bytes (data->blob "something\n"))
  (sha1-sum (data->blob "something\n"))
  )

(def git-address (comp sha1-sum data->blob))
