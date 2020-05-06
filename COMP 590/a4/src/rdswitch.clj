(ns rdswitch
  (:require [clojure.java.io :as io]
            hash-object cat-file write-wtree commit-tree
            help init rev-parse switch-branch))

(defn switches [& args]
  (let [params (first args)
        switch1 (first params)
        switch2 (if (and (> (count params) 2) (or (= (nth params 2) "-r") (= (nth params 2) "-d"))) (nth params 2) nil)]
    (if (and (= switch1 "-r") (= nil switch2))
      (let [directory (first (rest params))]
        (if (.exists (io/file directory))
          (cond
            (= (first (rest (rest params))) "init") (if (seq (rest (rest (rest params)))) (println "Error: init accepts no arguments") (init/init directory ".idiot"))
            (= (first (rest (rest params))) "hash-object") (if (seq (rest (rest (rest params)))) (hash-object/hash-object directory ".idiot" (rest (rest (rest params)))) (println "Error: you must specify a file."))
            (= (first (rest (rest params))) "cat-file") (if (or (.contains (rest (rest (rest params))) "-t") (.contains (rest (rest (rest params))) "-p")) (cat-file/cat-file directory ".idiot" (rest (rest (rest params)))) (println "Error: the -p or -t switch is required"))
            (= (first (rest (rest params))) "write-wtree") (write-wtree/write-wtree directory ".idiot" (rest (rest (rest params))))
            (= (first (rest (rest params))) "commit-tree") (commit-tree/commit-tree directory ".idiot" (rest (rest (rest params))))
            (= (first (rest (rest params))) "rev-parse") (rev-parse/rev-parse directory ".idiot" (rest (rest (rest params))))
            (= (first (rest (rest params))) "switch") (switch-branch/switch directory ".idiot" (rest (rest (rest params))))
            (= (first (rest (rest params))) "branch") (switch-branch/branch directory ".idiot" (rest (rest (rest params))))
            (= (first (rest (rest params))) "commit") (commit-tree/commit directory ".idiot" (rest (rest (rest params)))))
          (println "Error: the directory specified by -r does not exist"))) ())
    (if (and (= switch1 "-d") (= nil switch2))
      (let [database (first (rest params))]
        (cond
          (= (first (rest (rest params))) "init") (if (seq (rest (rest (rest params)))) (println "Error: init accepts no arguments") (init/init "." (first (rest params))))
          (= (first (rest (rest params))) "hash-object") (if (seq (rest (rest (rest params)))) (hash-object/hash-object "." database (rest (rest (rest params)))) (println "Error: you must specify a file."))
          (= (first (rest (rest params))) "cat-file") (if (or (.contains (rest (rest (rest params))) "-t") (.contains (rest (rest (rest params))) "-p")) (cat-file/cat-file "." database (rest (rest (rest params)))) (println "Error: either the -p or the -t switch is required."))
          (= (first (rest (rest params))) "write-wtree") (write-wtree/write-wtree "." database (rest (rest (rest params))))
          (= (first (rest (rest params))) "commit-tree") (commit-tree/commit-tree "." database (rest (rest (rest params))))
          (= (first (rest (rest params))) "rev-parse") (rev-parse/rev-parse "." database (rest (rest (rest params))))
          (= (first (rest (rest params))) "switch") (switch-branch/switch "." database (rest (rest (rest params))))
          (= (first (rest (rest params))) "branch") (switch-branch/branch "." database (rest (rest (rest params))))
          (= (first (rest (rest params))) "commit") (commit-tree/commit "." database (rest (rest (rest params)))))) ())
    (if (or (and (= switch1 "-d") (= switch2 "-r")) (and (= switch1 "-r") (= switch2 "-d")))
      (let [database (if (= switch1 "-d") (first (rest params)) (nth params 3))
            directory (if (= switch1 "-d") (nth params 3) (first (rest params)))]
        (cond
          (= (first (rest (rest (rest (rest params))))) "init") (if (seq (rest (rest (rest (rest (rest params)))))) (println "Error: init accepts no arguments") (init/init directory database))
          (= (first (rest (rest (rest (rest params))))) "hash-object") (if (seq (rest (rest (rest (rest (rest params)))))) (hash-object/hash-object directory database (rest (rest (rest (rest (rest params)))))) (println "Error: you must specify a file."))
          (= (first (rest (rest (rest (rest params))))) "cat-file") (if (or (.contains (rest (rest (rest (rest (rest params))))) "-t") (.contains (rest (rest (rest (rest (rest params))))) "-p")) (cat-file/cat-file directory database (rest (rest (rest (rest (rest params)))))) (println "Error: either the -p or the -t switch is required."))
          (= (first (rest (rest (rest (rest params))))) "write-wtree") (write-wtree/write-wtree directory database (rest (rest (rest (rest (rest params))))))
          (= (first (rest (rest (rest (rest params))))) "commit-tree") (commit-tree/commit-tree directory database (rest (rest (rest (rest (rest params))))))
          (= (first (rest (rest (rest (rest params))))) "rev-parse") (rev-parse/rev-parse directory database (rest (rest (rest (rest (rest params))))))
          (= (first (rest (rest (rest (rest params))))) "switch") (switch-branch/switch directory database (rest (rest (rest (rest (rest params))))))
          (= (first (rest (rest (rest (rest params))))) "branch") (switch-branch/branch directory database (rest (rest (rest (rest (rest params))))))
          (= (first (rest (rest (rest (rest params))))) "commit") (commit-tree/commit directory database (rest (rest (rest (rest (rest params)))))))) ())))
