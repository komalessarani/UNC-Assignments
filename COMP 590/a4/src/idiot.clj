(ns idiot
  (:require hash-object cat-file write-wtree commit-tree
            help rdswitch switch-branch rev-parse init
            [clojure.string :as str]))

(defn -main [& args]
  ; if there is no second argument, if it is just an empty call or --help, then print standard message or error messages
  (if (empty? (rest args))
    (cond
      (or (= (first args) "-h") (= (first args) "--help") (= (first args) "help") (empty? args)) ()
      (= (first args) "init") (init/init "." ".idiot")
      (= (first args) "rev-parse") (println "Error: you must specify a branch name.")
      (= (first args) "switch") (println "Error: you must specify a branch name.")
      (= (first args) "write-wtree") (write-wtree/write-wtree "." ".idiot" (first (rest args)))
      (= (first args) "hash-object") (println "Error: you must specify a file.")
      (= "-r" (first args)) (println "Error: the -r switch needs an argument")
      (= "-d" (first args)) (println "Error: the -d switch needs an argument")
      (and (empty? (first (rest args))) (= (first args) "cat-file")) (println "Error: the -p switch is required")
      :else (println "Error: invalid command")) ())

  (if (or (empty? args) (str/includes? args "-r") (str/includes? args "-d") (str/includes? args "-h") (str/includes? args "--help") (str/includes? args "help"))
    (cond
      (or (= (first args) "-h") (= (first args) "--help") (and (empty? (rest args)) (= (first args) "help")) (empty? args))
      (println "idiot: the other stupid content tracker\n\nUsage: idiot [<top-args>] <command> [<args>]\n\nTop-level arguments:\n   -r <dir>   run from the given directory instead of the current one\n   -d <dir>   store the database in <dir> (default: .idiot)\n\nCommands:\n   branch [-d <branch>]\n   cat-file {-p|-t} <address>\n   commit <tree> -m \"message\" [(-p parent)...]\n   commit-tree <tree> -m \"message\" [(-p parent)...]\n   hash-object [-w] <file>\n   help\n   init\n   rev-parse <ref>\n   switch [-c] <branch>\n   write-wtree")
      (or (str/includes? args "--help") (str/includes? args "-h") (str/includes? args "help")) (help/help args)
      (and (seq (rest args)) (or (= (first args) "-d") (= (first args) "-r"))) (rdswitch/switches args)
      (and (seq (rest args)) (or (and (= (nth args 3) "-r") (= (first args) "-d")) (and (= (nth args 3) "-d") (= (first args) "-r")))) (rdswitch/switches args)
      :else "Error: invalid command")

    ; if there are more than one arguments and there are no switches used (-h, --help, help, -r, or -d), call the functions needed and check their arguments
    (if (seq (rest args))
      (cond
        (= (first args) "hash-object") (hash-object/hash-object "." ".idiot" (rest args))
        (= (first args) "init") (println "Error: init accepts no arguments")
        (= (first args) "cat-file") (cat-file/cat-file "." ".idiot" (rest args))
        (= (first args) "write-wtree") (write-wtree/write-wtree  "." ".idiot" (first (rest args)))
        (= (first args) "commit-tree") (commit-tree/commit-tree "." ".idiot" (first (rest args)))
        (= (first args) "rev-parse") (rev-parse/rev-parse "." ".idiot" (rest args))
        (= (first args) "switch") (switch-branch/switch "." ".idiot" (rest args))
        (= (first args) "branch") (switch-branch/branch "." ".idiot" (rest args))
        (= (first args) "commit") (commit-tree/commit "." ".idiot" (first (rest args)))) ())))