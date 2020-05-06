(ns jst-explore-test-a6
  (:require [clj-http.client :as http]
            [clojure.core.async :refer [alts!! go-loop timeout]]
            [clojure.data.json :as json]
            [clojure.java.shell :refer [sh]]
            [clojure.set :as set]
            [clojure.string :as str]
            [net.cgrand.enlive-html :as html :refer [html-snippet select text texts]])
  (:import java.io.IOException
           java.net.ConnectException))

(def global-timeout-secs
  "How long the entire program will wait on a result before giving up."
  20)

(def process-heartbeat-period-msecs
  "How long to sleep between checks of whether the server process is alive."
  50)

(def nonblocking-timeout-msecs
  "How long to wait on a nonblocking result before giving up."
  100)

(def startup-grace-period-msecs
  "How long to allow the process to start before attempting to send an HTTP
  request. This is important because in the event the server can't bind to the
  given port, we _would_ get an HTTP response back from the already-bound
  service, less we gave the process a chance to start and throw an exception
  before we start sending requests."
  (* 5 1000))

(def http-request-period-msecs
  "How long to sleep between attempts to get a response from the server."
  200)

(def process-destroy-grace-msecs
  "How long to wait after nicely destroying the process before forcibly
  destroying the process."
  200)

(def tmp-dir "spec-run-tmp")

(defn- setup [tmp-dir]
  (let [{:keys [exit] :as result} (sh "bash" "setup-explore-test-a6.sh" tmp-dir)]
    (assert (zero? exit) (prn-str result))))

(defn- start-server [port]
  (let [cmd-words ["clojure" "-m" "idiot" "-r" tmp-dir "explore" "-p" (str port)]]
    (.start (new ProcessBuilder (into-array cmd-words)))))

(defn- process-result-channel [process]
  (go-loop []
    (if (.isAlive process)
      (do
        (Thread/sleep process-heartbeat-period-msecs)
        (recur))
      (let [stderr (try (slurp (.getErrorStream process))
                        (catch IOException _ ""))
            base {:stderr stderr}
            regex #"\nFull report at:\n([^\n]+)\n"
            report-path (second (re-find regex stderr))]
        (if-not report-path
          base
          (assoc base :full-report (slurp report-path)))))))

(defn- make-one-request [port]
  (let [url (str "http://localhost:" port)]
    (try
      [true (http/get url {:throw-exceptions false})]
      (catch Exception e
        [false e]))))

(defn- response-channel [port]
  (go-loop []
    ;; sleep initially to give a chance for the process to throw an address
    ;; already bound or similar exception.
    (Thread/sleep startup-grace-period-msecs)
    (let [[ok? result] (make-one-request port)]
      (cond
        ok? [true result]
        ;; This is a bit odd b/c we have to recur from tail position only.
        ;; The goal is to poll until the server is up and listening.
        ;; ConnectException means no server is listening on that port.
        (not= ConnectException (type result)) [false result]
        :else (do
                (Thread/sleep http-request-period-msecs)
                (recur))))))

(defn- kill-process [process]
  (.destroy process)
  (Thread/sleep process-destroy-grace-msecs)
  (when (.isAlive process)
    (.destroyForcibly process)))

(def test-names
  (let [make-msg #(str "The explore subcommand in a repo context " %)
        msg-roots ["starts an HTTP server that responds to requests"
                   "at the / endpoint returns a 200 response"
                   "at the / endpoint returns text/html content"
                   "at the / endpoint returns an HTML document that has a div.head-info element"
                   "at the / endpoint returns an HTML document that indicates HEAD pointing to a ref"
                   "at the / endpoint returns an HTML document that links HEAD's ref to the appropriate place"
                   "at the / endpoint returns an HTML document that has a ul.branch-list element"
                   "at the / endpoint returns an HTML document that includes sorted branch names as <li> elements"
                   "at the / endpoint returns an HTML document that links branch names to the appropriate place"
                   "at the /branch/<branch> endpoint returns 404 status if branch not found"
                   "at the /branch/<branch> endpoint returns 200 status if branch found"
                   "at the /branch/<branch> endpoint returns text/html content"
                   "at the /branch/<branch> endpoint returns an HTML document that has a ul.commit-list element"
                   "at the /branch/<branch> endpoint returns an HTML document that has an <li> per commit"
                   "at the /branch/<branch> endpoint returns an HTML document that has <li> elements that have the abbreviated commit address"
                   "at the /branch/<branch> endpoint returns an HTML document that has <li> elements that link the abbreviated commit address to the appropriate place"
                   "at the /branch/<branch> endpoint returns an HTML document that has <li> elements that has (only) the first line of the commit message"
                   "at the /commit/<address> endpoint returns 404 status if no object found"
                   "at the /commit/<address> endpoint returns 200 status if one commit found"
                   "at the /commit/<address> endpoint returns text/html content"
                   "at the /commit/<address> endpoint returns an HTML document that has an <h1> element with the right text"
                   "at the /commit/<address> endpoint returns an HTML document that has a div.tree element with the right text"
                   "at the /commit/<address> endpoint returns an HTML document that links the tree address to the appropriate place"
                   "at the /commit/<address> endpoint returns an HTML document that skips the parent line if there are no parents"
                   "at the /commit/<address> endpoint returns an HTML document that includes a single parent line if there is one parent"
                   "at the /commit/<address> endpoint returns an HTML document that includes multiple parent lines if necessary"
                   "at the /commit/<address> endpoint returns an HTML document that includes a div.parent element with parent info"
                   "at the /commit/<address> endpoint returns an HTML document that links the parent address to the appropriate place"
                   "at the /commit/<address> endpoint returns an HTML document that has a div.author element with the right text"
                   "at the /commit/<address> endpoint returns an HTML document that has a div.committer element with the right text"
                   "at the /commit/<address> endpoint returns an HTML document that has a pre.message element with the right text"
                   "at the /tree/<address> endpoint returns 404 status if no object found"
                   "at the /tree/<address> endpoint returns 200 status if one tree found"
                   "at the /tree/<address> endpoint returns text/html content"
                   "at the /tree/<address> endpoint returns an HTML document that has an <h1> element with the right text"
                   "at the /tree/<address> endpoint returns an HTML document that has a ul.tree-entries element"
                   "at the /tree/<address> endpoint returns an HTML document that has an <li> element for each tree entry"
                   "at the /tree/<address> endpoint returns an HTML document that has <li> elements whose text is wrapped in a <tt> tag"
                   "at the /tree/<address> endpoint returns an HTML document that has <li> elements with the right text for tree objects"
                   "at the /tree/<address> endpoint returns an HTML document that has <li> elements with the right text for blob objects"
                   "at the /tree/<address> endpoint returns an HTML document that links tree addresses to the appropriate place"
                   "at the /tree/<address> endpoint returns an HTML document that links blob addresses to the appropriate place"
                   "at the /blob/<address> endpoint returns 404 status if no object found"
                   "at the /blob/<address> endpoint returns 200 status if one blob found"
                   "at the /blob/<address> endpoint returns text/html content"
                   "at the /blob/<address> endpoint returns an HTML document that has an <h1> element with the right text"
                   "at the /blob/<address> endpoint returns an HTML document that has a <pre> element with the blob text"
                   "at the /commit/<address> endpoint redirects to /tree/<address> if necessary"
                   "at the /commit/<address> endpoint redirects to /blob/<address> if necessary"
                   "at the /tree/<address> endpoint redirects to /commit/<address> if necessary"
                   "at the /tree/<address> endpoint redirects to /blob/<address> if necessary"
                   "at the /blob/<address> endpoint redirects to /commit/<address> if necessary"
                   "at the /blob/<address> endpoint redirects to /tree/<address> if necessary"
                   "at the /commit/<address> endpoint when there are multiple matches returns a 300 status"
                   "at the /commit/<address> endpoint when there are multiple matches returns text/html content"
                   "at the /commit/<address> endpoint when there are multiple matches returns an HTML document that has a p tag with information"
                   "at the /commit/<address> endpoint when there are multiple matches returns an HTML document that has a ul.disambiguation-list element"
                   "at the /commit/<address> endpoint when there are multiple matches returns an HTML document that has an <li> element with relevant text for each option"
                   "at the /commit/<address> endpoint when there are multiple matches returns an HTML document that links each option to the appropriate place"
                   "at the /tree/<address> endpoint when there are multiple matches returns a 300 status"
                   "at the /tree/<address> endpoint when there are multiple matches returns text/html content"
                   "at the /tree/<address> endpoint when there are multiple matches returns an HTML document that has a p tag with information"
                   "at the /tree/<address> endpoint when there are multiple matches returns an HTML document that has a ul.disambiguation-list element"
                   "at the /tree/<address> endpoint when there are multiple matches returns an HTML document that has an <li> element with relevant text for each option"
                   "at the /tree/<address> endpoint when there are multiple matches returns an HTML document that links each option to the appropriate place"
                   "at the /blob/<address> endpoint when there are multiple matches returns a 300 status"
                   "at the /blob/<address> endpoint when there are multiple matches returns text/html content"
                   "at the /blob/<address> endpoint when there are multiple matches returns an HTML document that has a p tag with information"
                   "at the /blob/<address> endpoint when there are multiple matches returns an HTML document that has a ul.disambiguation-list element"
                   "at the /blob/<address> endpoint when there are multiple matches returns an HTML document that has an <li> element with relevant text for each option"
                   "at the /blob/<address> endpoint when there are multiple matches returns an HTML document that links each option to the appropriate place"]]
    (map make-msg msg-roots)))

(defn- make-result [name passed? fail-output]
  {:name name
   :score (if passed? 1 0)
   :max_score 1
   :output (if passed? "" fail-output)})

(defn- make-results [raw-results]
  (mapv (fn [msg [score fail-output]] (make-result msg score fail-output))
        test-names raw-results))

(defn- branch-list-tests [url-base]
  (let [response (http/get url-base {:throw-exceptions false})
        {:keys [status headers body]} response
        doc (html-snippet body)]
    [;; returns a 200 response
     [(= 200 status) (format "Expected response status to be 200, but it was %d." status)]

     ;; returns text/html content
     (let [ct-hdr? (fn [[k _]] (= "content-type" (.toLowerCase k)))
           content-type (some->> headers
                                 (filter ct-hdr?)
                                 first
                                 val)]
       (if (= content-type "text/html")
         [true ""]
         [false (format "Expected content-type header to be text/html, but was `%s`." (pr-str content-type))]))

     ;; returns an HTML document that has a div.head-info element
     (let [div (select doc [:div.head-info])]
       [(not-empty div)
        (format "Expected to find a <div> element with class 'head-info', but did not.\nbody was `%s`." body)])

     ;; returns an HTML document that indicates HEAD pointing to a ref
     (let [text (text (first (select doc [:div.head-info])))]
       [(= text "HEAD points to ref master")
        (format "Expected head-info <div> to mention which ref HEAD is pointing to, but it did not.\nbody was `%s`." body)])

     ;; returns an HTML document that links HEAD's ref to the appropriate place
     (let [a (first (select doc [:div.head-info :a]))
           href (-> a :attrs :href)]
       [(= href "/branch/master")
        (format "Expected head-info <div> to have an <a> tag linking to branch detail, but it did not.\nbody was `%s`." body)])

     ;; returns an HTML document that has a ul.branch-list element
     (let [ul (select doc [:ul.branch-list])]
       [(not-empty ul)
        (format "Expected to find a <ul> element with class 'branch-list', but did not.\nbody was `%s`." body)])

     ;; returns an HTML document that includes sorted branch names as <li> elements
     (let [lis (select doc [:ul.branch-list :li])
           branch-names (texts lis)]
       [(= branch-names ["feature-x" "master"])
        (cond
          (= (set branch-names) #{"feature-x" "master"}) (format "Expected to find <li> elements with sorted branch names, but they were unsorted.\nbody was `%s`." body)
          :else (format "Expected to find <li> elements with sorted branch names, but did not even find the right set of branch names.\nbody was `%s`." body))])

     ;; returns an HTML document that links branch names to the appropriate place
     (let [as (select doc [:ul.branch-list :li :a])
           hrefs (for [a as] (-> a :attrs :href))]
       [(= hrefs ["/branch/feature-x" "/branch/master"])
        (format "Expected to find <a> elements with links to '/branch/<branch>' in sorted order, but did not.\nbody was `%s`." body)])]))

(defn- commit-list-tests [url-base]
  (let [response404 (http/get (str url-base "/branch/unknown-branch") {:throw-exceptions false})
        response (http/get (str url-base "/branch/master") {:throw-exceptions false})
        {:keys [status headers body]} response
        doc (html-snippet body)]
    [;; returns 404 status if branch not found
     [(= 404 (:status response404))
      (format "Expected response status to be 404 for an unknown branch, but it was %d." (:status response404))]
     ;; returns 200 status if branch found
     [(= 200 status) (format "Expected response status to be 200 for a known branch, but it was %d." status)]

     ;; returns text/html content
     (let [ct-hdr? (fn [[k _]] (= "content-type" (.toLowerCase k)))
           content-type (some->> headers
                                 (filter ct-hdr?)
                                 first
                                 val)]
       (if (= content-type "text/html")
         [true ""]
         [false (format "Expected content-type header to be text/html, but was `%s`." (pr-str content-type))]))

     ;; returns an HTML document that has a ul.commit-list element
     (let [ul (select doc [:ul.commit-list])]
       [(not-empty ul)
        (format "Expected to find a <ul> element with class 'commit-list', but did not.\nbody was `%s`." body)])

     ;; returns an HTML document that has an <li> per commit
     (let [lis (select doc [:ul.commit-list :li])
           n 2]
       [(= n (count lis))
        (format "Expected to find one <li> element for each commit in the chain (%d total), but found %d instead.\nbody was `%s`." n (count lis) body)])

     ;; returns an HTML document that has <li> elements that have the abbreviated commit address
     (let [li-texts (texts (select doc [:ul.commit-list :li]))]
       [(and (= 2 (count li-texts))
             (.startsWith (nth li-texts 0) "a7bf360 ")
             (.startsWith (nth li-texts 1) "ead2ae4 "))
        (format "Expected to find abbreviated commit addresses in the <li> elements, but did not.\nbody was `%s`." body)])

     ;; returns an HTML document that has <li> elements that link the abbreviated commit address to the appropriate place
     (let [as (select doc [:ul.commit-list :li :a])
           hrefs (mapv #(-> % :attrs :href) as)]
       [(= hrefs
           ["/commit/a7bf360" "/commit/ead2ae4"])
        (format "Expected to find links to the commit details in the <li> elements, but did not.\nbody was `%s`." body)])

     ;; returns an HTML document that has <li> elements that has (only) the first line of the commit message
     (let [li-texts (texts (select doc [:ul.commit-list :li]))]
       [(and (= 2 (count li-texts))
             (re-matches #"[0-9a-f]{7} A merge commit with 2 parents" (nth li-texts 0))
             (re-matches #"[0-9a-f]{7} commit message" (nth li-texts 1)))
        (format "Expected the <li> elements to contain (only) the first line of the commit messages, but they did not.\nbody was `%s`." body)])]))

(defn- get-commit-tests [url-base]
  (let [response404 (http/get (str url-base "/commit/abcd") {:throw-exceptions false})
        response (http/get (str url-base "/commit/e319") {:throw-exceptions false})
        response0 (http/get (str url-base "/commit/ead2") {:throw-exceptions false})
        response2 (http/get (str url-base "/commit/a7bf") {:throw-exceptions false})
        {:keys [status headers body]} response
        doc (html-snippet body)]
    [;; returns 404 status if no object found
     [(= 404 (:status response404))
      (format "Expected response status to be 404 for an unknown commit, but it was %d." (:status response404))]
     ;; returns 200 status if one commit found
     [(= 200 status) (format "Expected response status to be 200 for a known commit, but it was %d." status)]

     ;; returns text/html content
     (let [ct-hdr? (fn [[k _]] (= "content-type" (.toLowerCase k)))
           content-type (some->> headers
                                 (filter ct-hdr?)
                                 first
                                 val)]
       (if (= content-type "text/html")
         [true ""]
         [false (format "Expected content-type header to be text/html, but was `%s`." (pr-str content-type))]))

     ;; returns an HTML document that has an <h1> element with the right text
     (let [h1 (first (select doc [:h1]))]
       [(= "Commit e319" (text h1))
        (format "Expected to find an <h1> element with text 'Commit e319', but %s.\nbody was `%s`."
                (if h1 (format "text was `%s`" (text h1)) "no <h1> element was found")
                body)])

     ;; returns an HTML document that has a div.tree element with the right text
     (let [tree (first (select doc [:div.tree]))
           expected "tree 8430bcd2a813f5cc9171b68befafe08c7b6c5336"]
       [(= expected (text tree))
        (format "Expected to find an <div> element with class 'tree' and text '%s', but %s.\nbody was `%s`."
                expected
                (if tree (format "text was `%s`" (text tree)) "no such <div> element was found")
                body)])

     ;; returns an HTML document that links the tree address to the appropriate place
     (let [a (first (select doc [:div.tree :a]))
           addr "8430bcd2a813f5cc9171b68befafe08c7b6c5336"
           expected (str "/tree/" addr)
           href (-> a :attrs :href)]
       [(= expected href)
        (format "Expected to find an <a> element within the 'tree' div whose href attribute is '%s', but %s.\nbody was `%s`."
                expected
                (if a (format "the href was `%s`" href) "no such <a> element was found")
                body)])

     ;; returns an HTML document that skips the parent line if there are no parents
     (let [doc (-> response0 :body html-snippet)
           parents (select doc [:div.parent])]
       [(empty? parents)
        (format "Expected no <div> elements with class 'parent' for a commit with no parents, but got %d.\nbody was `%s`." (count parents) body)])

     ;; returns an HTML document that includes a single parent line if there is one parent
     (let [parents (select doc [:div.parent])]
       [(= 1 (count parents))
        (format "Expected 1 <div> element with class 'parent' for a commit with 1 parent, but got %d.\nbody was `%s`." (count parents) body)])

     ;; returns an HTML document that includes multiple parent lines if necessary
     (let [doc (-> response2 :body html-snippet)
           parents (select doc [:div.parent])]
       [(= 2 (count parents))
        (format "Expected 2 <div> elements with class 'parent' for a commit with 2 parents, but got %d.\nbody was `%s`." (count parents) body)])

     ;; returns an HTML document that includes a div.parent element with parent info
     (let [parent (first (select doc [:div.parent]))
           expected "parent ead2ae448bea9273a38c8673a73426e252e5ad79"]
       [(= expected (text parent))
        (format "Expected a <div> element with class 'parent' and text '%s', but %s.\nbody was `%s`."
                expected
                (if parent (format "text was `%s`" (text parent)) "no such <div> element was found")
                body)])

     ;; returns an HTML document that links the parent address to the appropriate place
     (let [a (first (select doc [:div.parent :a]))
           expected "/commit/ead2ae448bea9273a38c8673a73426e252e5ad79"
           href (-> a :attrs :href)]
       [(= expected href)
        (format "Expected a <div> element with class 'parent' to have a link to '%s', but %s.\nbody was `%s`."
                expected
                (if a (format "the link was to `%s` instead" href) "no such <a> element was found")
                body)])

     ;; returns an HTML document that has a div.author element with the right text
     (let [author (first (select doc [:div.author]))
           expected "author Linus Torvalds <torvalds@transmeta.com> 1581997446 -0500"]
       [(= expected (text author))
        (format "Expected a <div> element with class 'author' and text '%s', but %s.\nbody was `%s`."
                expected
                (if author (format "text was `%s`" (text author)) "no such <div> element was found")
                body)])

     ;; returns an HTML document that has a div.committer element with the right text
     (let [committer (first (select doc [:div.committer]))
           expected "committer Linus Torvalds <torvalds@transmeta.com> 1581997446 -0500"]
       [(= expected (text committer))
        (format "Expected a <div> element with class 'committer' and text '%s', but %s.\nbody was `%s`."
                expected
                (if committer (format "text was `%s`" (text committer)) "no such <div> element was found")
                body)])

     ;; returns an HTML document that has a pre.message element with the right text
     (let [message (first (select doc [:pre.message]))
           expected "add feature X\n\nlotsa stuff got done here yo"]
       [(= expected (-> message text str/trimr))
        (format "Expected a <pre> element with class 'message' and text '%s', but %s.\nbody was `%s`."
                expected
                (if message (format "text was `%s`" (text message)) "no such <pre> element was found")
                body)])]))

(defn- get-tree-tests [url-base]
  (let [response404 (http/get (str url-base "/tree/0000") {:throw-exceptions false})
        addr "1c3a"
        response (http/get (str url-base "/tree/" addr) {:throw-exceptions false})
        {:keys [status headers body]} response
        doc (html-snippet body)]
    [;; returns 404 status if no object found
     [(= 404 (:status response404))
      (format "Expected response status to be 404 for an unknown tree, but it was %d." (:status response404))]
     ;; returns 200 status if one tree found
     [(= 200 status) (format "Expected response status to be 200 for a known tree, but it was %d." status)]

     ;; returns text/html content
     (let [ct-hdr? (fn [[k _]] (= "content-type" (.toLowerCase k)))
           content-type (some->> headers
                                 (filter ct-hdr?)
                                 first
                                 val)]
       (if (= content-type "text/html")
         [true ""]
         [false (format "Expected content-type header to be text/html, but was `%s`." (pr-str content-type))]))

     ;; returns an HTML document that has an <h1> element with the right text
     (let [h1 (first (select doc [:h1]))
           expected (str "Tree " addr)]
       [(= expected (text h1))
        (format "Expected to find an <h1> element with text '%s', but %s.\nbody was `%s`."
                expected
                (if h1 (format "text was `%s`" (text h1)) "no <h1> element was found")
                body)])

     ;; returns an HTML document that has a ul.tree-entries element
     (let [tree (first (select doc [:ul.tree-entries]))]
       [(some? tree)
        (format "Expected to find a <ul> element with class 'tree-entries', but no such element was found.\nbody was `%s`." body)])

     ;; returns an HTML document that has an <li> element for each tree entry
     (let [lis (select doc [:ul.tree-entries :li])]
       [(= 2 (count lis))
        (format "Expected to find 2 <li> elements, but found %s.\nbody was `%s`." (count lis) body)])

     ;; returns an HTML document that has <li> elements whose text is wrapped in a <tt> tag
     (let [li (first (select doc [:ul.tree-entries :li]))
           first-child (-> li :content first)]
       [(and (map? first-child)
             (contains? first-child :tag)
             (= :tt (:tag first-child)))
        (format "Expected to find a <tt> element as the first child of the <li> elements, but %s.\nbody was `%s`."
                (cond
                  (nil? li) "did not find any <li> elements"
                  (map? first-child) (format "found a <%s> tag instead" (-> first-child :tag name))
                  (string? first-child) (format "found text `%s` instead" first-child)
                  (nil? first-child) "did not find any children of the first <li>"
                  :else (format "encountered something unexpected (first child of first <li> is `%s`)" pr-str first-child))
                body)])

     ;; returns an HTML document that has <li> elements with the right text for tree objects
     (let [li-text (text (first (select doc [:ul.tree-entries :li])))
           expected "040000 tree 831755bc673a097e71f89f558c745d730b54e0a1 direct"]
       [(= expected li-text)
        (format "Expected to see `%s` as the text of the first <li> element (which should be for a tree object), but it was `%s`.\nbody was `%s`."
                expected li-text body)])

     ;; returns an HTML document that has <li> elements with the right text for blob objects
     (let [li-text (text (second (select doc [:ul.tree-entries :li])))
           expected "100644 blob 84309306d477cbd20d814c75d71214a29a5f35c9 file1"]
       [(= expected li-text)
        (format "Expected to see `%s` as the text of the second <li> element (which should be for a blob object), but it was `%s`.\nbody was `%s`."
                expected li-text body)])

     ;; returns an HTML document that links tree addresses to the appropriate place
     (let [a (first (select doc [:ul.tree-entries :li :a]))
           href (-> a :attrs :href)
           expected (str "/tree/831755bc673a097e71f89f558c745d730b54e0a1")]
       [(= expected href)
        (format "Expected to see a link to `%s` in the first <li> element (which should be for a tree object), but `%s`.\nbody was `%s`."
                expected
                (cond
                  (nil? a) "no <a> element was found in a <li> element"
                  :else (format "the href attribute of the found <a> tag was %s instead" href))
                body)])

     ;; returns an HTML document that links blob addresses to the appropriate place
     (let [a (second (select doc [:ul.tree-entries :li :a]))
           href (-> a :attrs :href)
           expected (str "/blob/84309306d477cbd20d814c75d71214a29a5f35c9")]
       [(= expected href)
        (format "Expected to see a link to `%s` in the second <li> element (which should be for a blob object), but `%s`.\nbody was `%s`."
                expected
                (cond
                  (nil? a) "no <a> element was found in a <li> element"
                  :else (format "the href attribute of the found <a> tag was %s instead" href))
                body)])]))

(defn- get-blob-tests [url-base]
  (let [response404 (http/get (str url-base "/blob/0000") {:throw-exceptions false})
        addr "168a"
        response (http/get (str url-base "/blob/" addr) {:throw-exceptions false})
        {:keys [status headers body]} response
        doc (html-snippet body)]
    [;; returns 404 status if no object found
     [(= 404 (:status response404))
      (format "Expected response status to be 404 for an unknown blob, but it was %d." (:status response404))]
     ;; returns 200 status if one blob found
     [(= 200 status) (format "Expected response status to be 200 for a known blob, but it was %d." status)]

     ;; returns text/html content
     (let [ct-hdr? (fn [[k _]] (= "content-type" (.toLowerCase k)))
           content-type (some->> headers
                                 (filter ct-hdr?)
                                 first
                                 val)]
       (if (= content-type "text/html")
         [true ""]
         [false (format "Expected content-type header to be text/html, but was `%s`." (pr-str content-type))]))

     ;; returns an HTML document that has an <h1> element with the right text
     (let [h1 (first (select doc [:h1]))
           expected (str "Blob " addr)]
       [(= expected (text h1))
        (format "Expected to find an <h1> element with text '%s', but %s.\nbody was `%s`."
                expected
                (if h1 (format "text was `%s`" (text h1)) "no <h1> element was found")
                body)])

     ;; returns an HTML document that has a <pre> element with the blob text
     (let [pre (first (select doc [:pre]))
           expected "Adelaide\n"]
       [(= expected (text pre))
        (format "Expected to find a <pre> element with text '%s', but %s.\nbody was `%s`."
                expected
                (if pre (format "text was `%s`" (text pre)) "no <pre> element was found")
                body)])]))

(defn- redirect-tests [url-base]
  (let [commit-addr "e319"
        tree-addr "1c3a"
        blob-addr "168a"
        commit-url #(str url-base "/commit/" %)
        tree-url #(str url-base "/tree/" %)
        blob-url #(str url-base "/blob/" %)
        commit-rel-url (str "/commit/" commit-addr)
        tree-rel-url (str "/tree/" tree-addr)
        blob-rel-url (str "/blob/" blob-addr)
        loc-hdr? (fn [[k _]] (= "location" (.toLowerCase k)))
        location #(some->> % :headers (filter loc-hdr?) first val)
        resp-data #(let [response (http/get (%1 %2) {:throw-exceptions false, :redirect-strategy :none})]
                     [(:status response) (location response) response])]

    [;; at the /commit/<address> endpoint redirects to /tree/<address> if necessary
     (let [[status location response] (resp-data commit-url tree-addr)]
       [(and (= 302 status)
             (= location tree-rel-url))
        (cond
          (not= 302 status) (format "Expected a request to show commit data for a tree to respond with a 302 status, but status was %d." status)
          (not= location tree-rel-url) (format "Expected a request to show commit data for a tree to respond with a Location header of %s, but Location header was %s.\nFull response is:\n%s"
                                               (pr-str tree-rel-url) (pr-str location) (pr-str response)))])

     ;; at the /commit/<address> endpoint redirects to /blob/<address> if necessary
     (let [[status location response] (resp-data commit-url blob-addr)]
       [(and (= 302 status)
             (= location blob-rel-url))
        (cond
          (not= 302 status) (format "Expected a request to show commit data for a blob to respond with a 302 status, but status was %d." status)
          (not= location blob-rel-url) (format "Expected a request to show commit data for a blob to respond with a Location header of %s, but Location header was %s.\nFull response is:\n%s"
                                               (pr-str blob-rel-url) (pr-str location) (pr-str response)))])

     ;; at the /tree/<address> endpoint redirects to /commit/<address> if necessary
     (let [[status location response] (resp-data tree-url commit-addr)]
       [(and (= 302 status)
             (= location commit-rel-url))
        (cond
          (not= 302 status) (format "Expected a request to show tree data for a commit to respond with a 302 status, but status was %d." status)
          (not= location commit-rel-url) (format "Expected a request to show tree data for a commit to respond with a Location header of %s, but Location header was %s.\nFull response is:\n%s"
                                                 (pr-str commit-rel-url) (pr-str location) (pr-str response)))])

     ;; at the /tree/<address> endpoint redirects to /blob/<address> if necessary
     (let [[status location response] (resp-data tree-url blob-addr)]
       [(and (= 302 status)
             (= location blob-rel-url))
        (cond
          (not= 302 status) (format "Expected a request to show tree data for a blob to respond with a 302 status, but status was %d." status)
          (not= location blob-rel-url) (format "Expected a request to show tree data for a blob to respond with a Location header of %s, but Location header was %s.\nFull response is:\n%s"
                                               (pr-str blob-rel-url) (pr-str location) (pr-str response)))])

     ;; at the /blob/<address> endpoint redirects to /commit/<address> if necessary
     (let [[status location response] (resp-data blob-url commit-addr)]
       [(and (= 302 status)
             (= location commit-rel-url))
        (cond
          (not= 302 status) (format "Expected a request to show blob data for a commit to respond with a 302 status, but status was %d." status)
          (not= location commit-rel-url) (format "Expected a request to show blob data for a commit to respond with a Location header of %s, but Location header was %s.\nFull response is:\n%s"
                                                 (pr-str commit-rel-url) (pr-str location) (pr-str response)))])

     ;; at the /blob/<address> endpoint redirects to /tree/<address> if necessary
     (let [[status location response] (resp-data blob-url tree-addr)]
       [(and (= 302 status)
             (= location tree-rel-url))
        (cond
          (not= 302 status) (format "Expected a request to show blob data for a tree to respond with a 302 status, but status was %d." status)
          (not= location tree-rel-url) (format "Expected a request to show blob data for a tree to respond with a Location header of %s, but Location header was %s.\nFull response is:\n%s"
                                               (pr-str tree-rel-url) (pr-str location) (pr-str response)))])]))

(defn- ambiguous-prefix-tests-for-type [url-base type]
  (let [prefix "8430"
        response (http/get (format "%s/%s/%s" url-base type prefix))
        {:keys [status headers body]} response
        doc (html-snippet body)]
    [;; returns a 300 status
     [(= 300 status) (format "Expected response status to be 300, but it was %d." status)]

     ;; returns text/html content
     (let [ct-hdr? (fn [[k _]] (= "content-type" (.toLowerCase k)))
           content-type (some->> headers (filter ct-hdr?) first val)]
       [(= content-type "text/html")
        (format "Expected content-type header to be text/html, but was `%s`." (pr-str content-type))])

     ;; returns an HTML document that has a p tag with information
     (let [p (first (select doc [:p]))
           expected "The given address prefix is ambiguous. Please disambiguate your intent by choosing from the following options."]
       [(= expected (text p))
        (format "Expected to find a <p> element with text %s, but %s.\nbody was `%s`."
                (pr-str expected)
                (if (nil? p)
                  "no <p> element was found"
                  (format "the text of the found <p> element was `%s`" (text p)))
                body)])

     ;; returns an HTML document that has a ul.disambiguation-list element
     (let [ul (first (select doc [:ul.disambiguation-list]))]
       [(some? ul)
        (format "Expected to find a <ul> element with class 'disambiguation-list', but did not.\nbody was `%s`."
                body)])

     ;; returns an HTML document that has an <li> element with relevant text for each option
     (let [lis (select doc [:ul.disambiguation-list :li])
           texts (texts lis)
           blob-addr "84309306d477cbd20d814c75d71214a29a5f35c9"
           commit-addr "8430b5667bb562336eadca4d9ce4f0a1ee5487f6"
           tree-addr "8430bcd2a813f5cc9171b68befafe08c7b6c5336"
           blob-text (str blob-addr " (blob)")
           commit-text (str commit-addr " (commit)")
           tree-text (str tree-addr " (tree)")]
       [(and (= 3 (count lis))
             (some #{blob-text} texts)
             (some #{commit-text} texts)
             (some #{tree-text} texts))
        (cond
          (not= 3 (count lis)) (format "Expected to find a <ul> element with class 'disambiguation-list' with 3 <li> children, but found %d such children.\nbody was `%s`."
                                       (count lis)
                                       body)
          (not-any? #{blob-text} texts) (format "Expected to find an <li> element with text %s but did not find one.\nbody was `%s`."
                                                (pr-str blob-text) body)
          (not-any? #{commit-text} texts) (format "Expected to find an <li> element with text %s but did not find one.\nbody was `%s`."
                                                  (pr-str commit-text) body)
          (not-any? #{tree-text} texts) (format "Expected to find an <li> element with text %s but did not find one.\nbody was `%s`."
                                                (pr-str tree-text) body))])

     ;; returns an HTML document that links each option to the appropriate place
     (let [as (select doc [:ul.disambiguation-list :li :a])
           hrefs (map #(-> % :attrs :href) as)
           blob-addr "84309306d477cbd20d814c75d71214a29a5f35c9"
           commit-addr "8430b5667bb562336eadca4d9ce4f0a1ee5487f6"
           tree-addr "8430bcd2a813f5cc9171b68befafe08c7b6c5336"
           blob-href (str "/blob/" blob-addr)
           commit-href (str "/commit/" commit-addr)
           tree-href (str "/tree/" tree-addr)]
       [(and (= 3 (count as))
             (some #{blob-href} hrefs)
             (some #{commit-href} hrefs)
             (some #{tree-href} hrefs))
        (cond
          (not= 3 (count as)) (format "Expected to find a <ul> element with class 'disambiguation-list' with 3 <li> children, each with an <a> child, but found %d such children.\nbody was `%s`."
                                       (count as)
                                       body)
          (not-any? #{blob-href} hrefs) (format "Expected to find an <a> element with href %s but did not find one.\nbody was `%s`."
                                                (pr-str blob-href) body)
          (not-any? #{commit-href} hrefs) (format "Expected to find an <li> element with href %s but did not find one.\nbody was `%s`."
                                                (pr-str commit-href) body)
          (not-any? #{tree-href} hrefs) (format "Expected to find an <li> element with href %s but did not find one.\nbody was `%s`."
                                                (pr-str tree-href) body))])]))

(defn- ambiguous-prefix-tests [url-base]
  (mapcat (partial ambiguous-prefix-tests-for-type url-base)
          ["commit" "tree" "blob"]))

(defn- test-results [port]
  (let [url-base (str "http://localhost:" port)]
    (make-results
      (concat
        ;; 1. started an HTTP server--if we get this far, we're good on this point.
        [[true ""]]
        (branch-list-tests url-base)
        (commit-list-tests url-base)
        (get-commit-tests url-base)
        (get-tree-tests url-base)
        (get-blob-tests url-base)
        (redirect-tests url-base)
        (ambiguous-prefix-tests url-base)))))

(defn- timeout-results []
  (let [msg (format "The server failed to start within %d seconds"
                    global-timeout-secs)
        result [false msg]
        results (repeat (count test-names) result)]
    (make-results results)))

(defn- process-result-string [{:keys [stderr full-report]}]
  (let [out (str "The standard output from your process has been consumed by the test suite,\n"
                 "which is testing that your output matches the requirements.\n"
                 "If you want to print extra things, e.g. for debugging purposes, wrap your\n"
                 "print statements in a binding block to print to stderr instead, like so:\n"
                 "(binding [*out* *err*] (prn ,,,))\n")
        between-braces "between these triple braces:"
        make-str #(format "The %s is %s {{{\n%s}}}\n" %1 between-braces %2)
        err (make-str "standard error output from your process" stderr)
        more (make-str "full report referenced in the err output" full-report)]
    (str/join "\n" (if full-report [out err more] [out err]))))

(defn- process-results [process-output]
  (let [first-msg (process-result-string process-output)
        msg "The server process crashed; see above."
        messages (cons first-msg (repeat (dec (count test-names)) msg))
        raw-results (mapv #(vector false %) messages)]
    (make-results raw-results)))

(defn- error-results [error]
  (let [first-msg (format (str "Sending an HTTP request threw an exception.\n"
                               "The exception data follows.\n%s")
                          (prn-str error))
        msg "Sending an HTTP request failed; see above."
        messages (cons first-msg (repeat (dec (count test-names)) msg))
        raw-results (mapv #(vector false %) messages)]
    (make-results raw-results)))

(defn- alts!!-with-timeout
  [timeout-msecs named-channels]
  (let [timeout-channel (timeout timeout-msecs)
        named-channels (assoc named-channels :timeout timeout-channel)
        channel->name (set/map-invert named-channels)
        [val channel] (alts!! (vec (vals named-channels)))
        name (channel->name channel)]
    (when (not= :timeout name)
      [name val])))

#_(defn- print-results-for-gradescope [results]
  (let [json-str (json/write-str results)
        json-str-to-splice (-> (str/trimr json-str)
                               (str/replace #"^\[" ",")
                               (str/replace #"\]$" ""))]
    (println json-str-to-splice)))

(defn- pprint-results [results]
  (doseq [{:keys [name score max_score output]} results]
    (let [red #(format "\033[31m%s\033[0m" %)
          green #(format "\033[32m%s\033[0m" %)
          colorize (if (= score max_score) green red)]
      (println (colorize name))
      (when (not= "" output) (println output)))))

(defn -main []
  (setup tmp-dir)
  (let [port 3000
        server-proc (start-server port)
        channels {:process (process-result-channel server-proc)
                  :response (response-channel port)}
        timeout-msecs (* global-timeout-secs 1000)
        [name val] (alts!!-with-timeout timeout-msecs channels)
        results (cond
                  (nil? name) (timeout-results)
                  (= :process name) (process-results val)
                  (not (first val)) (error-results (second val))
                  :else (test-results port))]
    ;; (print-results-for-gradescope results)
    (pprint-results results)
    (kill-process server-proc)
    (shutdown-agents)))

(comment
  ;; goal: find a blob with this hex prefix: 8430
  ;; (I already have a tree with that prefix.)
  (import 'java.security.MessageDigest)

  (defn- hex-digits->byte
    [[dig1 dig2]]
    ;; This is tricky because something like "ab" is "out of range" for a Byte,
    ;; because Bytes are signed and can only be between -128 and 127 (inclusive).
    ;; So we have to temporarily use an int to give us the room we need, then
    ;; adjust the value if needed to get it in the range for a byte, and finally
    ;; cast to a byte. I wish Java provided an unsigned Byte.
    (let [i (Integer/parseInt (str dig1 dig2) 16)
          byte-ready-int (if (< Byte/MAX_VALUE i)
                           (byte (- i 256))
                           i)]
      (byte byte-ready-int)))
  (hex-digits->byte ["8" "4"]) ; => -124
  (hex-digits->byte ["3" "0"]) ; => 48

  ;; n causing collision is 13994
  (letfn [(n->str [n] (Integer/toString n 36))
          (as-blob [data] (format "blob %d\000%s" (count data) data))
          (n->sha [n]
            (.digest (MessageDigest/getInstance "sha1")
                     (-> n n->str as-blob .getBytes)))
          (goal? [n]
            (let [[b1 b2] (n->sha n)]
              (= [b1 b2] [-124 48])))]
    (loop [n 0]
      ;; (if (zero? (rem n 10000)) (prn n))
      (if (goal? n)
        [n (n->str n)]
        (recur (inc n)))))

  ;; n causing collision is 167276
  (letfn [(n->str [n] (Integer/toString n 36))
          (commit-msg [n] (str "cause a 3-type collision with prefix 8430\n\n"
                               (n->str n)))
          (commit-data [n]
            (let [line "Linus Torvalds <torvalds@transmeta.com> 1581997446 -0500"]
              (format "tree %s\nparent %s\nauthor %s\ncommitter %s\n\n%s\n"
                      "1c3af436347dc90210416f34c6e6a8cdf66c909b"
                      "e319323baaa0b6fbb5d883446bc513ff20ed1e92"
                      line line
                      (commit-msg n))))
          (as-commit [n] (let [data (commit-data n)]
                           (format "commit %s\000%s" (count data) data)))
          (n->sha [n]
            (.digest (MessageDigest/getInstance "sha1")
                     (-> n as-commit .getBytes)))
          (goal? [n]
            (let [[b1 b2] (n->sha n)]
              (= [b1 b2] [-124 48])))]
    (loop [n 0]
      ;; (if (zero? (rem n 10000)) (prn n))
      (if (goal? n)
        [n (commit-msg n)]
        (recur (inc n)))))
  )
