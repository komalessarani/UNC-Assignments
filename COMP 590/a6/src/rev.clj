(ns rev
  (:require [clojure.java.io :as io]
            [clojure.string :as str])
  (:import (java.io ByteArrayOutputStream)
           (java.util.zip InflaterInputStream)))

(defn rev-parse [dir db args]
  (cond
    (empty? args) (println "Error: you must specify a branch name.")
    (> (count args) 1) (println "Error: you must specify a branch name and nothing else.")
    (not (.exists (io/file (str dir "/" db)))) (println "Error: could not find database. (Did you run `idiot init`?)")
    (and (or (= (first args) "@") (= (first args) "HEAD")) (.exists (io/file (str dir "/" db "/refs/heads/" (last (str/split (str/trim-newline (slurp (io/file (str dir "/" db "/HEAD")))) #"/"))))))
    (println (str/trim-newline (slurp (io/file (str dir "/" db "/refs/heads/" (last (str/split (str/trim-newline (slurp (io/file (str dir "/" db "/HEAD")))) #"/")))))))
    (.exists (io/file (str dir "/" db "/refs/heads/" (first args))))
    (println (str/trim-newline (slurp (io/file (str dir "/" db "/refs/heads/" (first args))))))
    (and (not= (first args) "HEAD") (not (.exists (io/file (str dir "/" db "/refs/heads/" (first args))))) (not= (first args) "@"))
    (println (str "Error: could not find ref named " (first args) "."))
    :else (println (str/trim-newline (slurp (io/file (str dir "/" db "/HEAD")))))))

(defn unzip
  "Unzip the given file's contents with zlib."
  [path]
  (with-open [input (-> path io/file io/input-stream)
              unzipper (InflaterInputStream. input)
              out (ByteArrayOutputStream.)]
    (io/copy unzipper out)
    (.toByteArray out)))

(defn format-object [directory database addy]
  (as-> addy x (str directory "/" database "/objects/" (subs addy 0 2) "/" (subs addy 2))
        (str/split (slurp (unzip x)) #"\000")))

(defn recurse-parent [dir db address]
  (let [contents (format-object dir db address)]
    (if (not (.contains (second (str/split (str/trim-newline (first (rest contents))) #"\n")) "parent"))
      (println address)
      (do (println address)
          (recurse-parent dir db (second (str/split (second (str/split (str/trim-newline (first (rest contents))) #"\n")) #"\s+")))))))

(defn recurse-log [dir db address]
  (let [contents (format-object dir db address)]
    (if (not (.contains (second (str/split (str/trim-newline (first (rest contents))) #"\n")) "parent"))
      (println (subs address 0 7) (first (str/split (second (str/split (last contents) #"\n\n")) #"\n")))
      (do (println (subs address 0 7) (first (str/split (second (str/split (last contents) #"\n\n")) #"\n")))
          (recurse-log dir db (second (str/split (second (str/split (str/trim-newline (first (rest contents))) #"\n")) #"\s+")))))))

(defn rev-list [dir db args]
  (cond
    (not (.exists (io/file (str dir "/" db)))) (println "Error: could not find database. (Did you run `idiot init`?)")
    (and (empty? (rest args)) (= "-n" (first args))) (println "Error: you must specify a numeric count with '-n'.")
    (and (= "-n" (first args)) (or (nil? (cond (re-find #"^-?\d+\.?\d*$" (first (rest args))) (read-string (first (rest args)))))
                                   (< (Integer/parseInt (first (rest args))) 0)))
    (println "Error: the argument for '-n' must be a non-negative integer.")
    (and (not= (first args) "HEAD") (not= "-n" (first args)) (not (.exists (io/file (str dir "/" db "/refs/heads/" (first args))))) (not= (first args) "@"))
    (println (str "Error: could not find ref named " (first args) "."))
    (and (or (not= (first args) "HEAD") (not= (first args) "@")) (not (nil? (seq args))) (.exists (io/file (str dir "/" db "/refs/heads/" (first args)))))
    (recurse-parent dir db (str/trim-newline (slurp (io/file (str dir "/" db "/refs/heads/" (first args))))))
    :else
    (let [branch (last (str/split (str/trim-newline (slurp (io/file (str dir "/" db "/HEAD")))) #"/"))]
      (recurse-parent dir db (str/trim-newline (slurp (io/file (str dir "/" db "/refs/heads/" branch))))))))

(defn log [dir db args]
  (cond
    (or (not= (first args) "--oneline") (empty? args)) (println "Error: log requires the --oneline switch")
    (not (.exists (io/file (str dir "/" db)))) (println "Error: could not find database. (Did you run `idiot init`?)")
    (and (empty? (rest (rest args))) (= "-n" (first (rest args)))) (println "Error: you must specify a numeric count with '-n'.")
    (and (= "-n" (first (rest args))) (or (nil? (cond (re-find #"^-?\d+\.?\d*$" (first (rest (rest args)))) (read-string (first (rest (rest args))))))
                                          (< (Integer/parseInt (first (rest (rest args)))) 0)))
    (println "Error: the argument for '-n' must be a non-negative integer.")
    (and (not= (first (rest args)) "HEAD") (not= (first (rest args)) "-n") (not (.exists (io/file (str dir "/" db "/refs/heads/" (first (rest args)))))) (not= (first (rest args)) "@"))
    (println (str "Error: could not find ref named " (first (rest args)) "."))
    (and (or (not= (first (rest args)) "HEAD") (not= (first (rest args)) "@")) (not (nil? (seq (rest args)))) (.exists (io/file (str dir "/" db "/refs/heads/" (first (rest args))))))
    (recurse-log dir db (str/trim-newline (slurp (io/file (str dir "/" db "/refs/heads/" (first (rest args)))))))
    :else
    (let [branch (last (str/split (str/trim-newline (slurp (io/file (str dir "/" db "/HEAD")))) #"/"))]
      (recurse-log dir db (str/trim-newline (slurp (io/file (str dir "/" db "/refs/heads/" branch))))))))