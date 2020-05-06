(ns explore
  (:require [ring.adapter.jetty :refer [run-jetty]]
            [hiccup.page :refer [html5]]
            [clojure.java.io :as io]
            [clojure.string :as str]
            rev
            commit-tree
            cat-file)
  (:import (java.io ByteArrayOutputStream)
           (java.util.zip InflaterInputStream)))

(defn unzip
  "Unzip the given file's contents with zlib."
  [path]
  (with-open [input (-> path io/file io/input-stream)
              unzipper (InflaterInputStream. input)
              out (ByteArrayOutputStream.)]
    (io/copy unzipper out)
    (.toByteArray out)))

(defn bytes->str [bytes]
  (->> bytes (map char) (apply str)))

(defn to-hex-string
  "Convert the given byte array into a hex string, 2 characters per byte."
  [bytes]
  (letfn [(to-hex [byte]
            (format "%02x" (bit-and 0xff byte)))]
    (->> bytes (map to-hex) (apply str))))

(defn split-at-byte [b bytes]
  (let [part1 (take-while (partial not= b) bytes)
        part2 (nthrest bytes (-> part1 count inc))]
    [part1 part2]))

(defn list-basic-info [world]
  {:status  200
   :headers {"Content-Type" "text/html"}
   :body    (html5 [:head [:title "Idiot Content Tracker"]]
                   [:body [:div {:class "head-info"} "HEAD points to ref "
                           [:a {:href (str "/branch/" (:ref world))} (:ref world)]]
                    [:ul {:class "branch-list"} (:branch-list world)]])})

(defn log-output [world uri]
  (let [in-branch (last (str/split uri #"/"))
        dir (:dir world)
        db (:db world)]
    (if (not (.exists (io/file (str dir "/" db "/refs/heads/" in-branch))))
      {:status 404 :body "404 Error"}
      (let [commits (with-out-str (rev/recurse-log dir db (str/trim-newline (slurp (io/file (str dir "/" db "/refs/heads/" in-branch))))))
            commit-arr (str/split-lines commits)
            separated-arr (map #(str/split % #"\s+") commit-arr)
            abb-ad  (map #(vector :a {:href (format "/commit/%s" %)} %) (map #(first %) separated-arr))]
        {:status  200
         :headers {"Content-Type" "text/html"}
         :body    (html5 [:head [:title "Idiot Content Tracker"]]
                         [:body [:ul {:class "commit-list"} (map (fn [a m] [:li a " " (str/join " " (rest m))]) abb-ad separated-arr)]])}))))

(defn parent-div [parent]
  [:div {:class "parent"} (str (first parent) " ") [:a {:href (format "/commit/%s" (second parent))} (second parent)]])

(defn list-commits [world uri]
  (let [addy (last (str/split uri #"/"))
        dir (:dir world)
        db (:db world)
        full-addy (first (commit-tree/abbrev addy dir db))]
    (cond
      (commit-tree/ambig? addy dir db)
      (let [addies (map #(str (subs addy 0 2) %) (.list (io/file (str dir "/" db "/objects/" (subs addy 0 2) "/"))))
            types (map #(first (str/split (get (cat-file/format-object dir db %) 0) #"\s+")) addies)
            format-addies (map (fn [a t] (vector :a {:href (format "/%s/%s" t a)} a)) addies types)]
        {:status  300
         :headers {"Content-Type" "text/html"}
         :body    (html5 [:head [:title "Idiot Content Tracker"]]
                         [:p "The given address prefix is ambiguous. Please disambiguate your intent by choosing from the following options."]
                         [:ul {:class "disambiguation-list"} (map (fn [a t] [:li a (str " (" t ")")]) format-addies types)])})
      (nil? full-addy) {:status 404}
      (= "tree" (first (str/split (get (cat-file/format-object dir db full-addy) 0) #"\s+")))
      {:status 302
       :headers {"Location" (format "/tree/%s" addy)
                 "Content-Type" "text/html"}}
      (= "blob" (first (str/split (get (cat-file/format-object dir db full-addy) 0) #"\s+")))
      {:status 302
       :headers {"Location" (format "/blob/%s" addy)
                 "Content-Type" "text/html"}}
      (= "commit" (first (str/split (get (cat-file/format-object dir db full-addy) 0) #"\s+")))
      (let [contents (second (cat-file/format-object dir db full-addy))
            rest-contents (str/split contents #"\n")
            tree (str/split (first rest-contents) #"\s+")
            parent-list (filter #(str/includes? % "parent ") (rest rest-contents))
            parents (map #(str/split % #"\s+") parent-list)
            after-parent (str/split-lines (first (drop 1 (str/split contents #"author"))))
            author (str/split (first after-parent) #"<torvalds@transmeta.com>")
            committer (str/split (second after-parent) #"<torvalds@transmeta.com>")
            message (rest (rest after-parent))]
        {:status  200
         :headers {"Content-Type" "text/html"}
         :body    (html5 [:head [:title "Idiot Content Tracker"]] [:h1 (format "Commit %s" addy)]
                         [:div {:class "tree"} (str (first tree) " ") [:a {:href (format "/tree/%s" (second tree))} (second tree)]]
                         (map #(parent-div %) parents)
                         [:div {:class "author"} "author" (first author) "&lt;torvalds@transmeta.com&gt;" (second author)]
                         [:div {:class "committer"} (first committer) "&lt;torvalds@transmeta.com&gt;" (second committer)]
                         [:pre {:class "message"} (str/trim (str/join "\n" message))])}))))

(defn show-tree [world uri]
  (let [addy (last (str/split uri #"/"))
        dir (:dir world)
        db (:db world)
        full-addy (first (commit-tree/abbrev addy dir db))]
    (cond
      (commit-tree/ambig? addy dir db)
      (let [addies (map #(str (subs addy 0 2) %) (.list (io/file (str dir "/" db "/objects/" (subs addy 0 2) "/"))))
            types (map #(first (str/split (get (cat-file/format-object dir db %) 0) #"\s+")) addies)
            format-addies (map (fn [a t] (vector :a {:href (format "/%s/%s" t a)} a)) addies types)]
        {:status  300
         :headers {"Content-Type" "text/html"}
         :body    (html5 [:head [:title "Idiot Content Tracker"]]
                         [:p "The given address prefix is ambiguous. Please disambiguate your intent by choosing from the following options."]
                         [:ul {:class "disambiguation-list"} (map (fn [a t] [:li a (str " (" t ")")]) format-addies types)])})
      (nil? full-addy) {:status 404}
      (= "commit" (first (str/split (get (cat-file/format-object dir db full-addy) 0) #"\s+")))
      {:status 302
       :headers {"Location" (format "/commit/%s" addy)
                 "Content-Type" "text/html"}}
      (= "blob" (first (str/split (get (cat-file/format-object dir db full-addy) 0) #"\s+")))
      {:status 302
       :headers {"Location" (format "/blob/%s" addy)
                 "Content-Type" "text/html"}}
      (= "tree" (first (str/split (get (cat-file/format-object dir db full-addy) 0) #"\s+")))
      (let [contents (cat-file/format-object dir db full-addy)
            tree-obj (unzip (str dir "/" db "/objects/" (subs full-addy 0 2) "/" (subs full-addy 2)))
            start (str/split (first (drop 1 (get (split-at-byte 0 contents) 0))) #"\s+") first-part (if (= "40000" (first start)) "040000" (first start))
            last-part (first (rest start)) byte-stuff (first (rest (seq (split-at-byte 0 tree-obj))))
            header (first (str/split (bytes->str (first (seq (split-at-byte 0 tree-obj)))) #"\s+"))
            tree-ad (to-hex-string (drop 13 (drop-last 33 byte-stuff)))
            file-obj (unzip (str dir "/" db "/objects/" (subs tree-ad 0 2) "/" (subs tree-ad 2)))
            f-contents (cat-file/format-object dir db tree-ad)
            f-header (if (= "class [B" (str (type file-obj))) "blob" ())
            f-start (str/split (first (drop 1 (get (split-at-byte 0 f-contents) 0))) #"\s+")
            fi-part (if (= "40000" (first f-start)) "040000" (first f-start))
            file-l-part (first (rest f-start))]
        {:status  200
         :headers {"Content-Type" "text/html"}
         :body    (html5 [:head [:title "Idiot Content Tracker"]] [:h1 "Tree " addy]
                         [:ul {:class "tree-entries"}
                          [:li [:tt first-part " " header " " [:a {:href (format "/tree/%s" tree-ad)} tree-ad] " " last-part]]
                          [:li [:tt fi-part " " f-header " " [:a {:href (format "/blob/%s" (to-hex-string (take-last 20 tree-obj)))}
                                                              (to-hex-string (take-last 20 tree-obj))] " " (subs file-l-part 0 (dec (count file-l-part))) "1"]]])}))))

(defn read-blob [world uri]
  (let [addy (last (str/split uri #"/"))
        dir (:dir world)
        db (:db world)
        full-addy (first (commit-tree/abbrev addy dir db))]
    (cond
      (commit-tree/ambig? addy dir db)
      (let [addies (map #(str (subs addy 0 2) %) (.list (io/file (str dir "/" db "/objects/" (subs addy 0 2) "/"))))
            types (map #(first (str/split (get (cat-file/format-object dir db %) 0) #"\s+")) addies)
            format-addies (map (fn [a t] (vector :a {:href (format "/%s/%s" t a)} a)) addies types)]
        {:status  300
         :headers {"Content-Type" "text/html"}
         :body    (html5 [:head [:title "Idiot Content Tracker"]]
                         [:p "The given address prefix is ambiguous. Please disambiguate your intent by choosing from the following options."]
                         [:ul {:class "disambiguation-list"} (map (fn [a t] [:li a (str " (" t ")")]) format-addies types)])})
      (nil? full-addy) {:status 404}
      (= "tree" (first (str/split (get (cat-file/format-object dir db full-addy) 0) #"\s+")))
      {:status 302
       :headers {"Location" (format "/tree/%s" addy)
                 "Content-Type" "text/html"}}
      (= "commit" (first (str/split (get (cat-file/format-object dir db full-addy) 0) #"\s+")))
      {:status 302
       :headers {"Location" (format "/commit/%s" addy)
                 "Content-Type" "text/html"}}
      (= "blob" (first (str/split (get (cat-file/format-object dir db full-addy) 0) #"\s+")))
      (let [contents (cat-file/format-object dir db full-addy)]
        ;(println (first contents))
        {:status  200
         :headers {"Content-Type" "text/html"}
         :body    (html5 [:head [:title "Idiot Content Tracker"]] [:h1 (format "Blob %s" addy)]
                         [:pre (first (rest contents))])}))))

(defn handler [request]
  (let [{:keys [request-method uri]} request
        method|path (str (.toUpperCase (name request-method)) uri)]
    (cond
      (= method|path "GET/") (list-basic-info (:world request))
      (str/includes? method|path "GET/branch") (log-output (:world request) uri)
      (str/includes? method|path "GET/commit") (list-commits (:world request) uri)
      (str/includes? method|path "GET/tree") (show-tree (:world request) uri)
      (str/includes? method|path "GET/blob") (read-blob (:world request) uri)
      :else {:status 200 :body "404 Error"})))

(defn wrap-world [handler world]
  (fn [request]
    (let [request (assoc request :world world)]
      (handler request))))

(defn explore [dir db args]
  (cond
    (not (.exists (io/file (str dir "/" db)))) (println "Error: could not find database. (Did you run `idiot init`?)")
    (and (empty? (rest args)) (= "-p" (first args))) (println "Error: you must specify a numeric port with '-p'.")
    (and (= "-p" (first args)) (or (nil? (cond (re-find #"^-?\d+\.?\d*$" (first (rest args))) (read-string (first (rest args)))))
                                   (< (Integer/parseInt (first (rest args))) 0)))
    (println "Error: the argument for '-p' must be a non-negative integer.")
    :else
    (let [port (if (= "-p" (first args)) (Integer/parseInt (first (rest args))) 3000)
          ref (last (str/split (str/trim-newline (slurp (io/file (str dir "/" db "/HEAD")))) #"/"))
          branch-list (map #(.getName %) (.listFiles (io/file (str dir "/" db "/refs/heads"))))
          format-branch (map #(vector :li [:a {:href (format "/branch/%s" %)} %]) (sort branch-list))
          world {:dir dir, :db db, :port port, :branch-list format-branch, :ref ref}]
      (println (str "Starting server on port " port "."))
      (run-jetty (-> handler
                     (wrap-world world))
                 {:port port}))))
