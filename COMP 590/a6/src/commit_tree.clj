(ns commit-tree
  (:require [clojure.java.io :as io]
            [clojure.string :as str])
  (:import (java.security MessageDigest)
           (java.util.zip InflaterInputStream DeflaterOutputStream)
           (java.io ByteArrayOutputStream File ByteArrayInputStream)))

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

(defn unzip
  "Unzip the given file's contents with zlib."
  [path]
  (with-open [input (-> path io/file io/input-stream)
              unzipper (InflaterInputStream. input)
              out (ByteArrayOutputStream.)]
    (io/copy unzipper out)
    (.toByteArray out)))

(defn ambig? [addy dir db]
  (let [first-two (subs addy 0 2)
        rest-address (subs addy 2)
        path (str dir "/" db "/objects/" first-two "/")
        files (.listFiles (io/file path))]
    (if (and (> (count (.listFiles (io/file path))) 1) (every? true? (distinct (for [f files] (str/starts-with? (.getName f) rest-address))))) true false)))

(defn abbrev [addy dir db]
  (let [first-two (subs addy 0 2)
        rest-address (subs addy 2)
        path (str dir "/" db "/objects/" first-two)
        files (.listFiles (io/file path))]
    (cond
      (< (count addy) 4) (println (str "Error: too few characters specified for address '" addy "'"))
      (ambig? addy dir db)
      (println (str "Error: ambiguous match for address '" addy "'"))
      :else
      (for [f files]
        (cond (and (.exists (io/file (str dir "/" db "/objects/" first-two "/")))
                   (str/includes? (str first-two "/" (.getName f)) (str first-two "/" rest-address)))
              (str first-two (.getName f)))))))

(defn make-parents [parent dir db]
  (for [p parent]
    (cond (not (nil? (first (abbrev p dir db))))
          (str "parent " (first (abbrev p dir db))  "\n"))))

(defn commit-object [addr parent msg dir db]
  (let [author-str "Linus Torvalds <torvalds@transmeta.com> 1581997446 -0500"
        tree-str addr
        parent-str (str/join (make-parents parent dir db))
        message msg
        commit-format (str "tree %s\n"
                           "%s"
                           "author %s\n"
                           "committer %s\n"
                           "\n"
                           "%s\n")
        commit-str (format commit-format
                           tree-str
                           parent-str
                           author-str
                           author-str
                           message)]
    (format "%s"
            commit-str)))

(defn commit-addy [commit]
  (to-hex-string (sha-bytes (.getBytes (str "commit " (count commit) "\000" commit)))))

(defn commit? [directory database addy]
  (if (as-> addy x (str directory "/" database "/objects/" (subs addy 0 2) "/" (subs addy 2))
            (str/split (slurp (unzip x)) #"\000")
            (first (str/split (first x) #" ")) (= "commit" x)) true false))

(defn commit [directory database args]
  (let [address (if-not (empty? args) (first (abbrev (first args) directory database)) ())
        contents (if (seq address)
                   (if (.exists (io/file (str directory "/" database "/objects/" (subs address 0 2) "/" (subs address 2))))
                     (as-> address x (str directory "/" database "/objects/" (subs address 0 2) "/" (subs address 2)) (slurp (unzip x))) ()) ())
        formatted-contents (if (seq address)
                             (if (.exists (io/file (str directory "/" database "/objects/" (subs address 0 2) "/" (subs address 2))))
                               (first (str/split (first (str/split contents #"\000")) #" ")) ()) ())]
    (cond
      (not (.exists (io/file (str directory "/" database)))) (println "Error: could not find database. (Did you run `idiot init`?)")
      (empty? args) (println "Error: you must specify a tree address.")
      (and (> (count (first args)) 3) (nil? address) (not (ambig? (first args) directory database)))
      (println "Error: no tree object exists at that address.")
      (and (not (nil? address)) (not= "tree" formatted-contents) (.exists (io/file (str directory "/" database "/objects/" (subs address 0 2) "/" (subs address 2)))))
      (println "Error: an object exists at that address, but it isn't a tree.")
      (and (not= (second args) "-p") (not= (second args) "-m")) (println "Error: you must specify a message.")
      (and (empty? (rest (rest args))) (= (second args) "-m")) (println "Error: you must specify a message with the -m switch.")
      (and (= "-p" (first (rest (rest (rest args))))) (empty? (rest (rest (rest (rest args))))))
      (println "Error: you must specify a commit object with the -p switch.")
      (and (> (count (first (rest (rest (rest (rest args)))))) 3) (not (ambig? (first (rest (rest (rest (rest args))))) directory database))
           (= "-p" (first (rest (rest (rest args))))) (nil? (first (abbrev (first (rest (rest (rest (rest args))))) directory database))))
      (println (str "Error: no commit object exists at address " (first (rest (rest (rest (rest args))))) "."))
      (and (> (count (first (rest (rest (rest (rest args)))))) 3) (= "-p" (first (rest (rest (rest args))))) (.exists (io/file (str directory "/" database "/objects/" (subs (first (rest (rest (rest (rest args))))) 0 2) "/" (subs (first (rest (rest (rest (rest args))))) 2)))) (not (commit? directory database (first (rest (rest (rest (rest args))))))))
      (println (str "Error: an object exists at address " (first (rest (rest (rest (rest args))))) ", " "but it isn't a commit."))
      (not (nil? (seq (filter #(< % 4) (map #(count %) (drop 1 (str/split (apply str args) #"-p")))))))
      (println (format (str "Error: too few characters specified for address '%s'") (first (filter #(< (count %) 4) (drop 1 (str/split (apply str args) #"-p"))))))
      (not (every? false? (map #(ambig? % directory database) (drop 1 (str/split (apply str args) #"-p")))))
      (println (format (str "Error: ambiguous match for address '%s'") (first (filter #(true? (ambig? % directory database)) (drop 1 (str/split (apply str args) #"-p"))))))
      :else
      (do (cond
            (not (nil? address))
            (do (if (.exists (io/file (str directory "/" database "/refs/heads/" (last (str/split (str/trim-newline (slurp (io/file (str directory "/" database "/HEAD")))) #"/")))))
                  (println (str "Commit created.\nUpdated branch " (last (str/split (str/trim-newline (slurp (io/file (str directory "/" database "/HEAD")))) #"/")) "."))
                  (println (str "Commit created.")))
                (spit (io/file (str directory "/" database "/refs/heads/" (last (str/split (str/trim-newline (slurp (io/file (str directory "/" database "/HEAD")))) #"/"))))
                      (str (commit-addy (commit-object address (drop 1 (str/split (apply str args) #"-p")) (first (rest (rest args))) directory database)) "\n"))))
          (let [commit-obj (commit-object address (drop 1 (str/split (apply str args) #"-p")) (first (rest (rest args))) directory database)
                commit-ad (commit-addy commit-obj)
                header+commit (str "commit " (count commit-obj) "\000" commit-obj)
                commit-path (str directory "/" database "/objects/" (subs commit-ad 0 2))]
            (.mkdir (File. commit-path))
            (io/copy (zip-str header+commit) (io/file (str commit-path "/" (subs commit-ad 2)))))))))

(defn commit-tree [directory database args]
  (let [address (if-not (empty? args) (first (abbrev (first args) directory database)) ())
        contents (if (seq address)
                   (if (.exists (io/file (str directory "/" database "/objects/" (subs address 0 2) "/" (subs address 2))))
                     (as-> address x (str directory "/" database "/objects/" (subs address 0 2) "/" (subs address 2)) (slurp (unzip x))) ()) ())
        formatted-contents (if (seq address)
                             (if (.exists (io/file (str directory "/" database "/objects/" (subs address 0 2) "/" (subs address 2))))
                               (first (str/split (first (str/split contents #"\000")) #" ")) ()) ())]
    (cond
      (not (.exists (io/file (str directory "/" database)))) (println "Error: could not find database. (Did you run `idiot init`?)")
      (empty? args) (println "Error: you must specify a tree address.")
      (and (> (count (first args)) 3) (nil? address) (not (ambig? (first args) directory database)))
      (println "Error: no tree object exists at that address.")
      (and (not (nil? address)) (not= "tree" formatted-contents) (.exists (io/file (str directory "/" database "/objects/" (subs address 0 2) "/" (subs address 2)))))
      (println "Error: an object exists at that address, but it isn't a tree.")
      (and (not= (second args) "-p") (not= (second args) "-m")) (println "Error: you must specify a message.")
      (and (empty? (rest (rest args))) (= (second args) "-m")) (println "Error: you must specify a message with the -m switch.")
      (and (= "-p" (first (rest (rest (rest args))))) (empty? (rest (rest (rest (rest args))))))
      (println "Error: you must specify a commit object with the -p switch.")
      (and (> (count (first (rest (rest (rest (rest args)))))) 3) (not (ambig? (first (rest (rest (rest (rest args))))) directory database))
           (= "-p" (first (rest (rest (rest args))))) (nil? (first (abbrev (first (rest (rest (rest (rest args))))) directory database))))
      (println (str "Error: no commit object exists at address " (first (rest (rest (rest (rest args))))) "."))
      (and (> (count (first (rest (rest (rest (rest args)))))) 3) (= "-p" (first (rest (rest (rest args))))) (.exists (io/file (str directory "/" database "/objects/" (subs (first (rest (rest (rest (rest args))))) 0 2) "/" (subs (first (rest (rest (rest (rest args))))) 2)))) (not (commit? directory database (first (rest (rest (rest (rest args))))))))
      (println (str "Error: an object exists at address " (first (rest (rest (rest (rest args))))) ", " "but it isn't a commit."))
      (not (nil? (seq (filter #(< % 4) (map #(count %) (drop 1 (str/split (apply str args) #"-p")))))))
      (println (format (str "Error: too few characters specified for address '%s'") (first (filter #(< (count %) 4) (drop 1 (str/split (apply str args) #"-p"))))))
      (not (every? false? (map #(ambig? % directory database) (drop 1 (str/split (apply str args) #"-p")))))
      (println (format (str "Error: ambiguous match for address '%s'") (first (filter #(true? (ambig? % directory database)) (drop 1 (str/split (apply str args) #"-p"))))))
      :else
      (cond (not (nil? address))
            (let [commit-obj (commit-object address (drop 1 (str/split (apply str args) #"-p")) (first (rest (rest args))) directory database)
                  commit-ad (commit-addy commit-obj)
                  header+commit (str "commit " (count commit-obj) "\000" commit-obj)
                  commit-path (str directory "/" database "/objects/" (subs commit-ad 0 2))]
              (println commit-ad)
              (.mkdir (File. commit-path))
              (io/copy (zip-str header+commit) (io/file (str commit-path "/" (subs commit-ad 2)))))))))