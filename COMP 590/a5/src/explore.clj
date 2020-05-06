(ns explore
  (:require [clojure.java.io :as io]
            [ring.adapter.jetty :refer [run-jetty]]
            [hiccup.page :refer [html5]]))

(defn payload [dir db port]
  (let [branches (map #(.getName %) (.listFiles (io/file (str dir "/" db "/refs/heads"))))
        one-branch (map #(into [:li %]) branches)
        handler (fn [_request]
                  {:status  200  ; meaning "OK"
                   :headers {"Content-Type" "text/html"}
                   :body    (html5 [:head [:title "Branches"]] [:body [:ul one-branch]])})]
    (run-jetty handler {:port port})))

(defn explore [dir db args]
  (cond
    (not (.exists (io/file (str dir "/" db)))) (println "Error: could not find database. (Did you run `idiot init`?)")
    (and (empty? (rest args)) (= "-p" (first args))) (println "Error: you must specify a numeric port with '-p'.")
    (and (= "-p" (first args)) (or (nil? (cond (re-find #"^-?\d+\.?\d*$" (first (rest args))) (read-string (first (rest args)))))
                                   (< (Integer/parseInt (first (rest args))) 0)))
    (println "Error: the argument for '-p' must be a non-negative integer.")
    :else
    (let [port (if (= "-p" (first args)) (Integer/parseInt (first (rest args))) 3000)]
      (println (str "Starting server on port " port "."))
      (payload dir db port))))
