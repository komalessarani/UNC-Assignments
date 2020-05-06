(ns init
  (:require [clojure.java.io :as io])
  (:import (java.io File)))


; creates a new git directory with objects if it does not exist, if it does exist, then prints an error message


(defn init [directory database]
  (if (not (.exists (io/file (str directory "/" database))))
    (do
      (.mkdir (File. (str directory "/" database))) (.mkdir (File. (str directory "/" database "/objects")))
      (.mkdir (File. (str directory "/" database "/refs"))) (.mkdir (File. (str directory "/" database "/refs/heads")))
      (spit (io/file (str directory "/" database "/HEAD")) "ref: refs/heads/master\n")
      (println (str "Initialized empty Idiot repository in " directory " directory")))
    (println (str "Error: " database " directory already exists"))))