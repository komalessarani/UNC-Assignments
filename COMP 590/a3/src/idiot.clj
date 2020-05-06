(ns idiot
  (:require [clojure.java.io :as io]
            hash-object
            cat-file
            write-wtree
            commit-tree
            [clojure.string :as str])
  (:import (java.io File)))

; creates a new git directory with objects if it does not exist, if it does exist, then prints an error message

(defn init [directory database]
  (if (not (.exists (io/file (str directory "/" database))))
    (do
      (.mkdir (File. (str directory "/" database))) (.mkdir (File. (str directory "/" database "/objects")))
      (println (str "Initialized empty Idiot repository in " directory " directory")))
    (println (str "Error: " database " directory already exists"))))
(defn switches [& args]
  (let [params (first args)
        switch1 (first params)
        switch2 (if (and (> (count params) 2) (or (= (nth params 2) "-r") (= (nth params 2) "-d"))) (nth params 2) nil)]
    ;(println (rest (rest (rest (rest (rest params))))))
    (if (and (= switch1 "-r") (= nil switch2))
      (let [directory (first (rest params))]
        (if (.exists (io/file directory))
          (cond
            (= (first (rest (rest params))) "init") (if (seq (rest (rest (rest params)))) (println "Error: init accepts no arguments") (init directory ".idiot"))
            (= (first (rest (rest params))) "hash-object") (if (seq (rest (rest (rest params)))) (hash-object/hash-object directory ".idiot" (rest (rest (rest params)))) (println "Error: you must specify a file."))
            (= (first (rest (rest params))) "cat-file") (if (or (.contains (rest (rest (rest params))) "-t") (.contains (rest (rest (rest params))) "-p")) (cat-file/cat-file directory ".idiot" (rest (rest (rest params)))) (println "Error: the -p or -t switch is required"))
            (= (first (rest (rest params))) "write-wtree") (write-wtree/write-wtree directory ".idiot" (rest (rest (rest params))))
            (= (first (rest (rest params))) "commit-tree") (commit-tree/commit-tree directory ".idiot" (rest (rest (rest params)))))
          (println "Error: the directory specified by -r does not exist"))) ())
    (if (and (= switch1 "-d") (= nil switch2))
      (let [database (first (rest params))]
        (cond
          (= (first (rest (rest params))) "init") (if (seq (rest (rest (rest params)))) (println "Error: init accepts no arguments") (init "." (first (rest params))))
          (= (first (rest (rest params))) "hash-object") (if (seq (rest (rest (rest params)))) (hash-object/hash-object "." database (rest (rest (rest params)))) (println "Error: you must specify a file."))
          (= (first (rest (rest params))) "cat-file") (if (or (.contains (rest (rest (rest params))) "-t") (.contains (rest (rest (rest params))) "-p")) (cat-file/cat-file "." database (rest (rest (rest params)))) (println "Error: either the -p or the -t switch is required."))
          (= (first (rest (rest params))) "write-wtree") (write-wtree/write-wtree "." database (rest (rest (rest params))))
          (= (first (rest (rest params))) "commit-tree") (commit-tree/commit-tree "." database (rest (rest (rest params)))))) ())
    (if (or (and (= switch1 "-d") (= switch2 "-r")) (and (= switch1 "-r") (= switch2 "-d")))
      (let [database (if (= switch1 "-d") (first (rest params)) (nth params 3))
            directory (if (= switch1 "-d") (nth params 3) (first (rest params)))]
        (cond
          (= (first (rest (rest (rest (rest params))))) "init") (if (seq (rest (rest (rest (rest (rest params)))))) (println "Error: init accepts no arguments") (init directory database))
          (= (first (rest (rest (rest (rest params))))) "hash-object") (if (seq (rest (rest (rest (rest (rest params)))))) (hash-object/hash-object directory database (rest (rest (rest (rest (rest params)))))) (println "Error: you must specify a file."))
          (= (first (rest (rest (rest (rest params))))) "cat-file") (if (or (.contains (rest (rest (rest (rest (rest params))))) "-t") (.contains (rest (rest (rest (rest (rest params))))) "-p")) (cat-file/cat-file directory database (rest (rest (rest (rest (rest params)))))) (println "Error: either the -p or the -t switch is required."))
          (= (first (rest (rest (rest (rest params))))) "write-wtree") (write-wtree/write-wtree directory database (rest (rest (rest (rest (rest params))))))
          (= (first (rest (rest (rest (rest params))))) "commit-tree") (commit-tree/commit-tree directory database (rest (rest (rest (rest (rest params)))))))) ())))

(defn help [& args]
  (if (or (.contains (first args) "-r") (.contains (first args) "-d"))
    (let [command (if (and (not= (first (first args)) "help") (not= (first (first args)) "-h") (not= (first (first args)) "--help")) (nth (first args) 2) (first (first args)))]
      (case command
        "init" (println "idiot init: initialize a new database\n\nUsage: idiot init\n\nArguments:\n   -h   print this message")
        "hash-object" (println "idiot hash-object: compute address and maybe create blob from file\n\nUsage: idiot hash-object [-w] <file>\n\nArguments:\n   -h       print this message\n   -w       write the file to database as a blob object\n   <file>   the file")
        "cat-file" (println "idiot cat-file: print information about an object\n\nUsage: idiot cat-file {-p|-t} <address>\n\nArguments:\n   -h          print this message\n   -p          pretty-print contents based on object type\n   -t          print the type of the given object\n   <address>   the SHA1-based address of the object")
        (or "-h" "help" "--help") (println "idiot help: print help for a command\n\nUsage: idiot help <command>\n\nArguments:\n   <command>   the command to print help for\n\nCommands:\n   help\n   init\n   hash-object [-w] <file>\n   cat-file {-p|-t} <address>\n   write-wtree\n   commit-tree <tree> -m \"<message>\" [(-p <parent>)...]")
        "write-wtree" (println "idiot write-wtree: write the working tree to the database\n\nUsage: idiot write-wtree\n\nArguments:\n   -h       print this message")
        "commit-tree" (println "idiot commit-tree: write a commit object based on the given tree\n\nUsage: idiot commit-tree <tree> -m \"message\" [(-p parent)...]\n\nArguments:\n   -h               print this message\n   <tree>           the address of the tree object to commit\n   -m \"<message>\"   the commit message\n   -p <parent>      the address of a parent commit")
        (println "Error: invalid command")))

    (let [command (if (= "help" (first (first args))) (first (rest (first args))) (first (first args)))]
      (case command
        "init" (println "idiot init: initialize a new database\n\nUsage: idiot init\n\nArguments:\n   -h   print this message")
        "hash-object" (println "idiot hash-object: compute address and maybe create blob from file\n\nUsage: idiot hash-object [-w] <file>\n\nArguments:\n   -h       print this message\n   -w       write the file to database as a blob object\n   <file>   the file")
        "cat-file" (println "idiot cat-file: print information about an object\n\nUsage: idiot cat-file {-p|-t} <address>\n\nArguments:\n   -h          print this message\n   -p          pretty-print contents based on object type\n   -t          print the type of the given object\n   <address>   the SHA1-based address of the object")
        (or "-h" "help" "--help") (println "idiot help: print help for a command\n\nUsage: idiot help <command>\n\nArguments:\n   <command>   the command to print help for\n\nCommands:\n   help\n   init\n   hash-object [-w] <file>\n   cat-file {-p|-t} <address>\n   write-wtree\n   commit-tree <tree> -m \"<message>\" [(-p <parent>)...]")
        "write-wtree" (println "idiot write-wtree: write the working tree to the database\n\nUsage: idiot write-wtree\n\nArguments:\n   -h       print this message")
        "commit-tree" (println "idiot commit-tree: write a commit object based on the given tree\n\nUsage: idiot commit-tree <tree> -m \"message\" [(-p parent)...]\n\nArguments:\n   -h               print this message\n   <tree>           the address of the tree object to commit\n   -m \"<message>\"   the commit message\n   -p <parent>      the address of a parent commit")
        (println "Error: invalid command")))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

; MAIN FUNCTION

(defn -main [& args]
  ; if there is no second argument, if it is just an empty call or --help, then print standard message or error messages
  (if (empty? (rest args))
    (cond
      (or (= (first args) "-h") (= (first args) "--help") (= (first args) "help") (empty? args)) ()
      (= (first args) "init") (init "." ".idiot")
      (= (first args) "write-wtree") (write-wtree/write-wtree "." ".idiot" (first (rest args)))
      (= (first args) "hash-object") (println "Error: you must specify a file.")
      (= "-r" (first args)) (println "Error: the -r switch needs an argument")
      (= "-d" (first args)) (println "Error: the -d switch needs an argument")
      (and (empty? (first (rest args))) (= (first args) "cat-file")) (println "Error: the -p switch is required")
      :else (println "Error: invalid command")) ())

  ; if there are more than one arguments, call those functions and check their arguments
  (if (seq (rest args))
    (cond
      (and (= (first args) "hash-object") (not (or (= (first (rest args)) "--help") (= (first (rest args)) "-h")))) (hash-object/hash-object "." ".idiot" (rest args))
      (and (= (first args) "init") (not (or (= (first (rest args)) "--help") (= (first (rest args)) "-h")))) (println "Error: init accepts no arguments")
      (and (= (first args) "cat-file") (not (or (= (first (rest args)) "--help") (= (first (rest args)) "-h")))) (cat-file/cat-file "." ".idiot" (rest args))
      (= (first args) "write-wtree") (write-wtree/write-wtree  "." ".idiot" (first (rest args)))
      (= (first args) "commit-tree") (commit-tree/commit-tree "." ".idiot" (first (rest args)))) ())

  (cond
    (or (= (first args) "-h") (= (first args) "--help") (and (empty? (rest args)) (= (first args) "help")) (empty? args))
    (println "idiot: the other stupid content tracker\n\nUsage: idiot [<top-args>] <command> [<args>]\n\nTop-level arguments:\n   -r <dir>   run from the given directory instead of the current one\n   -d <dir>   store the database in <dir> (default: .idiot)\n\nCommands:\n   help\n   init\n   hash-object [-w] <file>\n   cat-file {-p|-t} <address>\n   write-wtree\n   commit-tree <tree> -m \"<message>\" [(-p <parent>)...]")
    (or (= (first (rest args)) "-h") (= (first (rest args)) "help") (= (first (rest args)) "--help") (and (or (str/includes? args "--help") (str/includes? args "-h")) (= (first args) "help")) (or (str/includes? args "--help") (str/includes? args "-h") (str/includes? args "help"))) (help args)
    (and (seq (rest args)) (or (= (first args) "-d") (= (first args) "-r"))) (switches args)
    (and (seq (rest args)) (or (and (= (nth args 3) "-r") (= (first args) "-d")) (and (= (nth args 3) "-d") (= (first args) "-r")))) (switches args)
    :else "Error: invalid command"))