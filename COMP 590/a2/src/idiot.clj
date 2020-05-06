(ns idiot
  (:require [clojure.java.io :as io]
            hash-object
            cat-file)
  (:import (java.io File)))

; creates a new git directory with objects if it does not exist, if it does exist, then prints an error message


(defn init []
  (if (not (.exists (io/file ".git"))) (do
                                         (.mkdir (File. ".git")) (.mkdir (File. ".git/objects"))
                                         (println "Initialized empty Idiot repository in .git directory"))
      (println "Error: .git directory already exists")))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

; MAIN FUNCTION


(defn -main [& args]

  ; if there is no second argument, if it is just an empty call or --help, then print standard message or error messages
  (if (empty? (rest args))
    (cond
      (or (= (first args) "-h") (= (first args) "--help") (= (first args) "help") (empty? args))
      (println "idiot: the other stupid content tracker\n\nUsage: idiot <command> [<args>]\n\nCommands:\n   help\n   init\n   hash-object [-w] <file>\n   cat-file -p <address>")
      (= (first args) "init") (init)
      (= (first args) "hash-object") (println "Error: you must specify a file.")
      (and (empty? (first (rest args))) (= (first args) "cat-file")) (println "Error: the -p switch is required")
      :else (println "Error: invalid command"))

    ; else if there are more than one arguments, call those functions and check their arguments
    (cond
      (and (= (first args) "hash-object") (>= (count (rest args)) 1) (not (or (= (first (rest args)) "--help") (= (first (rest args)) "-h")))) (hash-object/hash-object (rest args))
      (and (= (first args) "init") (>= (count (rest args)) 1) (not (or (= (first (rest args)) "--help") (= (first (rest args)) "-h")))) (println "Error: init accepts no arguments")
      (and (= (first args) "cat-file")  (>= (count (rest args)) 1) (not (or (= (first (rest args)) "--help") (= (first (rest args)) "-h")))) (cat-file/cat-file (rest args))))

  ; if the commands are called with -h or --help, print specific statements
  (cond
    (or (and (= (first args) "init") (or (= (first (rest args)) "-h") (= (first (rest args)) "--help"))) (and (= (first args) "help") (= (first (rest args)) "init")))
    (println "idiot init: initialize a new database\n\nUsage: idiot init\n\nArguments:\n   -h   print this message")
    (or (and (= (first args) "hash-object") (or (= (first (rest args)) "-h") (= (first (rest args)) "--help"))) (and (= (first args) "help") (= (first (rest args)) "hash-object")))
    (println "idiot hash-object: compute address and maybe create blob from file\n\nUsage: idiot hash-object [-w] <file>\n\nArguments:\n   -h       print this message\n   -w       write the file to database as a blob object\n   <file>   the file")
    (or (and (= (first args) "cat-file") (or (= (first (rest args)) "-h") (= (first (rest args)) "--help"))) (and (= (first args) "help") (= (first (rest args)) "cat-file")))
    (println "idiot cat-file: print information about an object\n\nUsage: idiot cat-file -p <address>\n\nArguments:\n   -h          print this message\n   -p          pretty-print contents based on object type\n   <address>   the SHA1-based address of the object")
    (or (and (= (first args) "help") (or (= (first (rest args)) "-h") (= (first (rest args)) "--help"))) (and (= (first args) "help") (= (first (rest args)) "help")))
    (println "idiot help: print help for a command\n\nUsage: idiot help <command>\n\nArguments:\n   <command>   the command to print help for\n\nCommands:\n   help\n   init\n   hash-object [-w] <file>\n   cat-file -p <address>")
    (and (= (first args) "help") (seq (first (rest args))) (or (not (= (first (rest args)) "init")) (not (= (first (rest args)) "hash-object")) (not (= (first (rest args)) "cat-file")) (not (= (first (rest args)) "help")))) (println "Error: invalid command")))