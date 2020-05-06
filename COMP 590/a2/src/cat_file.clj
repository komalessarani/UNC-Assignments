(ns cat-file
  (:require [clojure.java.io :as io]
            [clojure.string :as str])
  (:import (java.util.zip InflaterInputStream)
           (java.io ByteArrayOutputStream)))

(defn unzip
  "Unzip the given data with zlib. Pass an opened input stream as the arg. The
  caller should close the stream afterwards."
  [input-stream]
  (with-open [unzipper (InflaterInputStream. input-stream)
              out (ByteArrayOutputStream.)]
    (io/copy unzipper out)
    (->> (.toByteArray out)

         (map char)
         (apply str))))

;CAT_FILE PART

(defn cat-file [args]
  (let [address (if-not (empty? (first (rest args))) (first (rest args)))
        path (if-not (empty? (first (rest args))) (str ".git/objects/" (subs address 0 2) "/" (subs address 2 (count address))))]
    (cond
      (and (= (first args) "-p") (empty? (rest args))) (println "Error: you must specify an address")
      (and (not (.exists (io/file ".git"))) (not (= (first args) "-p"))) (println "Error: could not find database. (Did you run `idiot init`?)")
      (and (= (first args) "-p") (not (.exists (io/file path)))) (println "Error: that address doesn't exist")
      (and (= (first args) "-p") (= (first (rest args)) (str address)) (.exists (io/file path)))
      (let [contents (with-open [input (-> path io/file io/input-stream)] (unzip input))
            new_string (get (str/split contents #"\000") 1)]
        (println (str/trim-newline new_string))))))