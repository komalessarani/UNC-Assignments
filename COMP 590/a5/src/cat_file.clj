(ns cat-file
  (:require [clojure.java.io :as io]
            [clojure.string :as str])
  (:import (java.util.zip InflaterInputStream)
           (java.io ByteArrayOutputStream)))

(defn unzip
  "Unzip the given file's contents with zlib."
  [path]
  (with-open [input (-> path io/file io/input-stream)
              unzipper (InflaterInputStream. input)
              out (ByteArrayOutputStream.)]
    (io/copy unzipper out)
    (.toByteArray out)))

(defn bytes->str [bytes]
  (->> bytes (map char) (apply str)))

(defn to-hex-string
  "Convert the given byte array into a hex string, 2 characters per byte."
  [bytes]
  (letfn [(to-hex [byte]
            (format "%02x" (bit-and 0xff byte)))]
    (->> bytes (map to-hex) (apply str))))

(defn split-at-byte [b bytes]
  (let [part1 (take-while (partial not= b) bytes)
        part2 (nthrest bytes (-> part1 count inc))]
    [part1 part2]))

(defn format-object [directory database addy]
  (as-> addy x (str directory "/" database "/objects/" (subs addy 0 2) "/" (subs addy 2))
        (str/split (slurp (unzip x)) #"\000")))

;CAT_FILE PART


(defn cat-file [directory database args]
  (if (and (or (= (first args) "-t") (= (first args) "-p")) (empty? (rest args)))
    (println "Error: you must specify an address")
    (let [address (if-not (empty? (first (rest args))) (first (rest args)) ())
          first-two (subs address 0 2)
          rest-address (subs address 2)
          path (str directory "/" database "/objects/" first-two "/")
          files (.listFiles (io/file path))]
      (cond
        (and (not (.exists (io/file (str directory "/" database))))) (println "Error: could not find database. (Did you run `idiot init`?)")
        (< (count address) 4) (println (str "Error: too few characters specified for address '" address "'"))
        (nil? files) (println "Error: that address doesn't exist")
        (and (> (count (.listFiles (io/file path))) 1) (every? true? (distinct (for [f files] (str/starts-with? (.getName f) rest-address)))))
        (println (str "Error: ambiguous match for address '" address "'"))
        :else
        (doseq [f files]
          (cond
            (and (.exists (io/file (str directory "/" database "/objects/" first-two "/")))
                 (str/includes? (str first-two "/" (.getName f)) (str first-two "/" rest-address)))
            (let [new-path (str directory "/" database "/objects/" first-two "/" (.getName f))
                  new-address (str first-two (.getName f))
                  contents (format-object directory database new-address)]
              (cond
                (and (= (first args) "-t") (= (first (rest args)) (str address)) (.exists (io/file new-path)))
                (println (first (str/split (get contents 0) #"\s+")))
                (and (= (first args) "-p") (= (first (rest args)) (str address)) (.exists (io/file new-path)))
                (cond
                  (= "blob" (first (str/split (get contents 0) #"\s+")))
                  (println (str/trim-newline (first (rest contents))))
                  (= "tree" (first (str/split (get contents 0) #"\s+")))
                  (let [tree-obj (unzip new-path)
                        start (str/split (first (drop 1 (get (split-at-byte 0 contents) 0))) #"\s+") first-part (if (= "40000" (first start)) "040000" (first start))
                        last-part (first (rest start)) byte-stuff (first (rest (seq (split-at-byte 0 tree-obj))))
                        header (first (str/split (bytes->str (first (seq (split-at-byte 0 tree-obj)))) #"\s+"))
                        tree-ad (to-hex-string (drop 25 (drop-last 32 byte-stuff)))
                        file-obj (unzip (str directory "/" database "/objects/" (subs tree-ad 0 2) "/" (subs tree-ad 2)))
                        f-contents (format-object directory database tree-ad)
                        f-header (if (= "class [B" (str (type file-obj))) "blob" ())
                        f-start (str/split (first (drop 1 (get (split-at-byte 0 f-contents) 0))) #"\s+")
                        fi-part (if (= "40000" (first f-start)) "040000" (first f-start))
                        file-l-part (first (rest f-start))]
                    (println (str first-part " " header " " tree-ad "\t" last-part
                                  "\n" fi-part " " f-header " " (to-hex-string (take-last 20 tree-obj)) "\t" (subs file-l-part 0 (dec (count file-l-part))))))
                  :else (println (str/trim-newline (first (rest contents)))))))))))))