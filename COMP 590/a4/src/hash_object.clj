(ns hash-object
  (:require [clojure.java.io :as io])
  (:import (java.security MessageDigest)
           (java.io ByteArrayOutputStream ByteArrayInputStream File)
           (java.util.zip DeflaterOutputStream)))

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

(defn sha1-sum [header+blob]
  (bytes->hex-string (sha1-hash-bytes header+blob)))

(defn zip-str
  "Zip the given data with zlib. Return a ByteArrayInputStream of the zipped
  content."
  [data]
  (let [out (ByteArrayOutputStream.)
        zipper (DeflaterOutputStream. out)]
    (io/copy data zipper)
    (.close zipper)
    (ByteArrayInputStream. (.toByteArray out))))

(defn hash-object [directory database args]
  (let [file_name  (if (and (not= nil (first (rest args))) (= (first args) "-w")) (first (rest args))   (first args))
        address (if (.exists (io/file (str directory "/" file_name))) (sha1-sum (str "blob " (count (slurp (str directory "/" file_name))) "\000" (slurp (str directory "/" file_name)))) ())]
    (cond
      (and (= (first args) "-w") (empty? (rest args))) (println "Error: you must specify a file.")
      (not (.exists (io/file (str directory "/" database)))) (println "Error: could not find database. (Did you run `idiot init`?)")
      (not (.exists (io/file (str directory "/" file_name)))) (println "Error: that file isn't readable")
      (= (first args) (str file_name)) (println address)
      (and (= (first args) "-w") (= (first (rest args)) (str file_name)) (.exists (io/file (str directory "/" file_name)))) (do
                                                                                                                              (.mkdir (File. (str directory "/" database "/objects/") (subs address 0 2)))
                                                                                                                              (io/copy (zip-str (str "blob " (count (slurp (str directory "/" file_name))) "\000" (slurp (str directory "/" file_name)))) (io/file (str directory "/" database "/objects/" (subs address 0 2) "/" (subs address 2 (count address)))))
                                                                                                                              (println address)))))