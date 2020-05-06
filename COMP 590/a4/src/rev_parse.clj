(ns rev-parse
  (:require [clojure.java.io :as io]
            [clojure.string :as str]))

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