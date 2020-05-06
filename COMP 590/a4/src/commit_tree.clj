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

(defn make-parents [parent]
  (str/join (map #(str "parent " % "\n") parent)))

(defn commit-object [addr parent msg]
  (let [author-str "Linus Torvalds <torvalds@transmeta.com> 1581997446 -0500"
        tree-str addr
        parent-str (make-parents parent)
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
  (let [contents (if (seq (first args)) (if (.exists (io/file (str directory "/" database "/objects/" (subs (first args) 0 2) "/" (subs (first args) 2)))) (as-> (first args) x (str directory "/" database "/objects/" (subs (first args) 0 2) "/" (subs (first args) 2)) (slurp (unzip x))) ()) ())
        formatted-contents (if (seq (first args)) (if (.exists (io/file (str directory "/" database "/objects/" (subs (first args) 0 2) "/" (subs (first args) 2)))) (first (str/split (first (str/split contents #"\000")) #" ")) ()) ())]
    (cond
      (not (.exists (io/file (str directory "/" database)))) (println "Error: could not find database. (Did you run `idiot init`?)")
      (or (empty? args) (empty? (first args))) (println "Error: you must specify a tree address.")
      (not (.exists (io/file (str directory "/" database "/objects/" (subs (first args) 0 2) "/" (subs (first args) 2)))))
      (println "Error: no tree object exists at that address.")
      (and (not= "tree" formatted-contents) (.exists (io/file (str directory "/" database "/objects/" (subs (first args) 0 2) "/" (subs (first args) 2)))))
      (println "Error: an object exists at that address, but it isn't a tree.")
      (and (not= (second args) "-p") (not= (second args) "-m")) (println "Error: you must specify a message.")
      (and (empty? (rest (rest args))) (= (second args) "-m")) (println "Error: you must specify a message with the -m switch.")
      (and (= "-p" (first (rest (rest (rest args))))) (empty? (rest (rest (rest (rest args))))))
      (println "Error: you must specify a commit object with the -p switch.")
      (and (= "-p" (first (rest (rest (rest args))))) (not (.exists (io/file (str directory "/" database "/objects/" (subs (first (rest (rest (rest (rest args))))) 0 2) "/" (subs (first (rest (rest (rest (rest args))))) 2))))))
      (println (str "Error: no commit object exists at address " (first (rest (rest (rest (rest args))))) "."))
      (and (= "-p" (first (rest (rest (rest args))))) (.exists (io/file (str directory "/" database "/objects/" (subs (first (rest (rest (rest (rest args))))) 0 2) "/" (subs (first (rest (rest (rest (rest args))))) 2)))) (not (commit? directory database (first (rest (rest (rest (rest args))))))))
      (println (str "Error: an object exists at address " (first (rest (rest (rest (rest args))))) ", " "but it isn't a commit."))
      :else
      (do (cond
            (= "ref: refs" (first (str/split (str/trim-newline (slurp (io/file (str directory "/" database "/HEAD")))) #"/")))
            (do (spit (io/file (str directory "/" database "/refs/heads/" (last (str/split (str/trim-newline (slurp (io/file (str directory "/" database "/HEAD")))) #"/"))))
                      (str (commit-addy (commit-object (first args) (drop 1 (str/split (apply str args) #"-p")) (first (rest (rest args))))) "\n"))
                (if (.exists (io/file (str directory "/" database "/refs/heads/" (last (str/split (str/trim-newline (slurp (io/file (str directory "/" database "/HEAD")))) #"/")))))
                  (println (str "Commit created.\nUpdated branch " (last (str/split (str/trim-newline (slurp (io/file (str directory "/" database "/HEAD")))) #"/")) "."))
                  (println (str "Commit created.")))))
          (let [commit-obj (commit-object (first args) (drop 1 (str/split (apply str args) #"-p")) (first (rest (rest args))))
                commit-ad (commit-addy commit-obj)
                header+commit (str "commit " (count commit-obj) "\000" commit-obj)
                commit-path (str directory "/" database "/objects/" (subs commit-ad 0 2))]
            (.mkdir (File. commit-path))
            (io/copy (zip-str header+commit) (io/file (str commit-path "/" (subs commit-ad 2)))))))))

(defn commit-tree [directory database args]
  (let [contents (if (seq (first args)) (if (.exists (io/file (str directory "/" database "/objects/" (subs (first args) 0 2) "/" (subs (first args) 2)))) (as-> (first args) x (str directory "/" database "/objects/" (subs (first args) 0 2) "/" (subs (first args) 2)) (slurp (unzip x))) ()) ())
        formatted-contents (if (seq (first args)) (if (.exists (io/file (str directory "/" database "/objects/" (subs (first args) 0 2) "/" (subs (first args) 2)))) (first (str/split (first (str/split contents #"\000")) #" ")) ()) ())]
    (cond
      (not (.exists (io/file (str directory "/" database)))) (println "Error: could not find database. (Did you run `idiot init`?)")
      (or (empty? args) (empty? (first args))) (println "Error: you must specify a tree address.")
      (not (.exists (io/file (str directory "/" database "/objects/" (subs (first args) 0 2) "/" (subs (first args) 2)))))
      (println "Error: no tree object exists at that address.")
      (and (not= "tree" formatted-contents) (.exists (io/file (str directory "/" database "/objects/" (subs (first args) 0 2) "/" (subs (first args) 2)))))
      (println "Error: an object exists at that address, but it isn't a tree.")
      (and (not= (second args) "-p") (not= (second args) "-m")) (println "Error: you must specify a message.")
      (and (empty? (rest (rest args))) (= (second args) "-m")) (println "Error: you must specify a message with the -m switch.")
      (and (= "-p" (first (rest (rest (rest args))))) (empty? (rest (rest (rest (rest args))))))
      (println "Error: you must specify a commit object with the -p switch.")
      (and (= "-p" (first (rest (rest (rest args))))) (not (.exists (io/file (str directory "/" database "/objects/" (subs (first (rest (rest (rest (rest args))))) 0 2) "/" (subs (first (rest (rest (rest (rest args))))) 2))))))
      (println (str "Error: no commit object exists at address " (first (rest (rest (rest (rest args))))) "."))
      (and (= "-p" (first (rest (rest (rest args))))) (.exists (io/file (str directory "/" database "/objects/" (subs (first (rest (rest (rest (rest args))))) 0 2) "/" (subs (first (rest (rest (rest (rest args))))) 2)))) (not (commit? directory database (first (rest (rest (rest (rest args))))))))
      (println (str "Error: an object exists at address " (first (rest (rest (rest (rest args))))) ", " "but it isn't a commit."))
      :else
      (let [commit-obj (commit-object (first args) (drop 1 (str/split (apply str args) #"-p")) (first (rest (rest args))))
            commit-ad (commit-addy commit-obj)
            header+commit (str "commit " (count commit-obj) "\000" commit-obj)
            commit-path (str directory "/" database "/objects/" (subs commit-ad 0 2))]
        (println commit-ad)
        (.mkdir (File. commit-path))
        (io/copy (zip-str header+commit) (io/file (str commit-path "/" (subs commit-ad 2))))))))