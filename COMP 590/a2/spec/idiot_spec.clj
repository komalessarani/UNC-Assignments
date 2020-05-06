(ns idiot-spec
  (:require [clojure.java.io :as io]
            [clojure.java.shell :refer [sh]]
            [clojure.string :as str]
            [idiot :as sut]
            [idiot-spec-utils :refer [data->blob git-address rm-rf sha1-sum
                                      unzip zip-str]]
            [speclj.core :refer [after before context describe it should
                                 should=* should-not= with]]))

(defn run-fast
  "Call the idiot program in the current process with the given string of args."
  [cmd]
  (->> (str/split cmd #"\s+")
       (remove str/blank?)
       (apply sut/-main)
       with-out-str))

(defn run-safely
  "Run as a separate process the idiot program with the given string of args."
  [cmd]
  (->> (str/split cmd #"\s+")
       (remove str/blank?)
       (apply sh "clojure" "-m" "idiot")
       :out
       str))

(def run run-fast)

(def usage-str
  (str "idiot: the other stupid content tracker\n\n"
       "Usage: idiot <command> [<args>]\n\n"
       "Commands:\n"
       "   help\n"
       "   init\n"
       "   hash-object [-w] <file>\n"
       "   cat-file -p <address>\n"))

(describe "The program"
  (it "prints usage given no command-line arguments"
    (should=* usage-str (run "")))

  (it "prints usage given arg of -h"
    (should=* usage-str (run "-h")))

  (it "prints usage given arg of --help"
    (should=* usage-str (run "--help")))

  (it "handles an unrecognized command"
    (should=* "Error: invalid command\n" (run "unknown-command"))))

(def help-usage-str
  (str "idiot help: print help for a command\n\n"
       "Usage: idiot help <command>\n\n"
       "Arguments:\n"
       "   <command>   the command to print help for\n\n"
       "Commands:\n"
       "   help\n"
       "   init\n"
       "   hash-object [-w] <file>\n"
       "   cat-file -p <address>\n"))

(def init-usage-str
  (str "idiot init: initialize a new database\n\n"
       "Usage: idiot init\n\n"
       "Arguments:\n"
       "   -h   print this message\n"))

(def hash-object-usage-str
  (str "idiot hash-object: compute address and maybe create blob from file\n\n"
       "Usage: idiot hash-object [-w] <file>\n\n"
       "Arguments:\n"
       "   -h       print this message\n"
       "   -w       write the file to database as a blob object\n"
       "   <file>   the file\n"))

(def cat-file-usage-str
  (str "idiot cat-file: print information about an object\n\n"
       "Usage: idiot cat-file -p <address>\n\n"
       "Arguments:\n"
       "   -h          print this message\n"
       "   -p          pretty-print contents based on object type\n"
       "   <address>   the SHA1-based address of the object\n"))

(describe "The help subprogram"
  (it "prints top level usage given no arguments"
    (should=* usage-str (run "help")))

  (it "prints help usage given -h argument"
    (should=* help-usage-str (run "help -h")))

  (it "prints help usage given --help argument"
    (should=* help-usage-str (run "help --help")))

  (it "prints help usage given help argument"
    (should=* help-usage-str (run "help help")))

  (it "prints usage for init command"
    (should=* init-usage-str (run "help init")))

  (it "prints usage for hash-object command"
    (should=* hash-object-usage-str (run "help hash-object")))

  (it "prints usage for cat-file command"
    (should=* cat-file-usage-str (run "help cat-file")))

  (it "ignores extra arguments"
    (should=* init-usage-str (run "help init extra-arg")))

  (it "prints an error message given an unknown command"
    (should=* "Error: invalid command\n" (run "help unknown-command"))))

(describe "The init subprogram"
  (with tmp-file "a-temp-file")
  (after (rm-rf (io/file ".git") (io/file @tmp-file)))

  (it "prints usage given -h argument"
    (should=* init-usage-str (run "init -h")))

  (it "prints usage given --help argument"
    (should=* init-usage-str (run "init --help")))

  (it "prints an error when there are any invalid args"
    (should=* "Error: init accepts no arguments\n" (run "init database")))

  (context "called with no args"
    (it "notifies user of new directory"
      (should=* "Initialized empty Idiot repository in .git directory\n"
                (run "init")))

    (it "creates a .git directory"
      (run "init")
      (should (.isDirectory (io/file ".git"))))

    (it "creates a .git/objects directory"
      (run "init")
      (should (.isDirectory (io/file ".git/objects"))))

    (it "prints an error message if .git directory already exists"
      (.mkdir (io/file ".git"))
      (should=* "Error: .git directory already exists\n"
                (run "init")))))

(describe "The hash-object subprogram"
  (with tmp-file "a-temp-file")
  (after (rm-rf (io/file ".git") (io/file @tmp-file)))

  (it "prints usage given -h argument"
    (should=* hash-object-usage-str (run "hash-object -h")))

  (it "prints usage given --help argument"
    (should=* hash-object-usage-str (run "hash-object --help")))

  (it "prints an error message when run outside of a repo"
    (should=* "Error: could not find database. (Did you run `idiot init`?)\n"
              (run "hash-object a-file")))

  (context "in a repo context"
    (before (run "init"))

    (it "prints an error message when there is no file given"
      (should=* "Error: you must specify a file.\n" (run "hash-object")))

    (it "prints an error message when the given arg isn't a readable file"
      (should=* "Error: that file isn't readable\n"
                (run "hash-object nonexistent-file")))

    (it "prints an error message when there is no file given but -w is given"
      (should=* "Error: you must specify a file.\n"
                (run "hash-object -w")))

    (it "prints the correct address when no -w switch is given"
      (let [data "hello\n"]
        (spit @tmp-file data)
        (should=* (str (git-address data) "\n")
                  (run (str "hash-object " @tmp-file)))))

    (context "with the -w switch and a readable file"
      (with data "something\n")
      (with header+blob (data->blob @data))
      (with address (sha1-sum @header+blob))
      (with dir (str ".git/objects/" (subs @address 0 2)))
      (with full-path (str @dir "/" (subs @address 2)))
      (before (spit @tmp-file @data))

      (it "prints the correct address"
        (should=* (str @address "\n") (run (str "hash-object -w " @tmp-file))))

      (it "creates a .git/objects subdirectory from the address"
        (run (str "hash-object -w " @tmp-file))
        (should (.isDirectory (io/file @dir))))

      (it "creates a file in the .git/objects/ dir from the address"
        (run (str "hash-object -w " @tmp-file))
        (should (.isFile (io/file @full-path))))

      (it "creates a file with the correct contents"
        (run (str "hash-object -w " @tmp-file))
        (let [unzipped-contents (unzip @full-path)]
          (should=* @header+blob unzipped-contents))))))

(describe "The cat-file subprogram"
  (with tmp-file "a-temp-file")
  (after (rm-rf (io/file ".git") (io/file @tmp-file)))

  (it "prints usage given -h argument"
    (should=* cat-file-usage-str (run "cat-file -h")))

  (it "prints usage given --help argument"
    (should=* cat-file-usage-str (run "cat-file --help")))

  (it "prints an error message when run outside of a repo"
    (should=* "Error: could not find database. (Did you run `idiot init`?)\n"
              (run "cat-file a-file")))

  (context "in a repo context"
    (with data "anything\n")
    (with other-data "anything else\n")
    (with address (git-address @data))
    (with other-address (git-address @other-data))
    (before (run "init"))

    (context "without the -p switch"
      (it "prints an error message"
        (should=* "Error: the -p switch is required\n" (run "cat-file"))))

    (context "with the -p switch"
      (it "prints an error message when no address given"
        (should=* "Error: you must specify an address\n" (run "cat-file -p")))

      (it "prints an error message when the given arg isn't a valid address"
        (should=* "Error: that address doesn't exist\n"
                  (run "cat-file -p 0123456789abcdef0123456789abcdef01234567")))

      (it "prints the corresponding blob when a valid address is given"
        (spit @tmp-file @data)
        (run (str "hash-object -w " @tmp-file))
        (should=*
          @data
          (run (str "cat-file -p " @address))))

      (it "prints the corresponding blob when a different valid address is given"
        (spit @tmp-file @other-data)
        (run (str "hash-object -w " @tmp-file))
        (should=*
          @other-data
          (run (str "cat-file -p " @other-address)))))))
