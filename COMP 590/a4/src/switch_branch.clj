(ns switch-branch
  (:require [clojure.java.io :as io]
            [clojure.string :as str]))

(defn switch [dir db args]
  (cond
    (empty? args) (println "Error: you must specify a branch name.")
    (or (and (not= "-c" (first args)) (> (count args) 1)) (and (= "-c" (first args)) (> (count (rest args)) 1))) (println "Error: you may only specify one branch name.")
    (not (.exists (io/file (str dir "/" db)))) (println "Error: could not find database. (Did you run `idiot init`?)")
    (and (= "-c" (first args)) (.exists (io/file (str dir "/" db "/refs/heads/" (first (rest args)))))) (println "Error: a ref with that name already exists.")
    (and (not= "-c" (first args)) (not (.exists (io/file (str dir "/" db "/refs/heads/" (first args)))))) (println "Error: no ref with that name exists.")
    (and (not= "-c" (first args)) (.exists (io/file (str dir "/" db "/refs/heads/" (first args)))))
    (do (spit (io/file (str dir "/" db "/HEAD")) (str "ref: refs/heads/" (first args) "\n"))
        (println (str "Switched to branch '" (first args) "'")))
    (and (= "-c" (first args)) (not (.exists (io/file (str dir "/" db "/refs/heads/" (first (rest args)))))))
    (do (spit (io/file (str dir "/" db "/refs/heads/" (first (rest args))))
              (slurp (io/file (str dir "/" db "/refs/heads/" (last (str/split (str/trim-newline (slurp (io/file (str dir "/" db "/HEAD")))) #"/"))))))
        (spit (io/file (str dir "/" db "/HEAD")) (str "ref: refs/heads/" (first (rest args)) "\n"))
        (println (str "Switched to a new branch '" (first (rest args)) "'")))
    :else (println "else " args)))

(defn branch [dir db args]
  (cond
    (and (empty? (rest args)) (= (first args) "-d")) (println "Error: you must specify a branch name.")
    (and (> (count (rest args)) 1) (= (first args) "-d")) (println "Error: invalid arguments.")
    (not (.exists (io/file (str dir "/" db)))) (println "Error: could not find database. (Did you run `idiot init`?)")
    (and (= "-d" (first args)) (not (.exists (io/file (str dir "/" db "/refs/heads/" (first (rest args)))))))
    (println (str "Error: branch '" (first (rest args)) "' not found."))
    (and (= "-d" (first args)) (= (first (rest args)) (last (str/split (str/trim-newline (slurp (io/file (str dir "/" db "/HEAD")))) #"/"))))
    (println (str "Error: cannot delete checked-out branch '" (first (rest args)) "'."))
    (and (= "-d" (first args)) (not= (first (rest args)) (last (str/split (str/trim-newline (slurp (io/file (str dir "/" db "/HEAD")))) #"/"))))
    (do (io/delete-file (io/file (str dir "/" db "/refs/heads/" (first (rest args)))))
        (println (str "Deleted branch " (first (rest args)) ".")))
    (empty? args)
    (let [files (sort (.listFiles (io/file (str dir "/" db "/refs/heads/"))))]
      (doseq [f files]
        (if (= (.getName f) (last (str/split (str/trim-newline (slurp (io/file (str dir "/" db "/HEAD")))) #"/")))
          (println "*" (.getName f)) (println " " (.getName f)))))
    :else (println "Error: invalid arguments.")))
