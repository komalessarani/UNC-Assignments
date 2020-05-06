(ns mishmash
  (:require [clojure.string :as str]))


  (defn pascal [n]
    (def p (first n))
    (def isBlank (str/blank? p))
    (if isBlank (do ()) (def p (read-string p)))
    (def line [1])
    (if (and (integer? p) (= (count n) 1) (>= p 0)) (do (dorun (for [k (range p)]
                          (do (def sub (- p k))
                              (def line (conj line (/ (* sub (get line k)) (inc k))))
                              )
                          )
                        )
                 (println (str/join " " line))) (println "invalid input"))
    )

(defn write-roman [n]
    (if (> (count n) 1) (print "invalid input"))
    (def w (first n))
    (def isBlank (str/blank? w))
    (if isBlank  (print "invalid input") (def w (read-string w)))
    (def rom "")
    (if (and (not isBlank ) (integer? w) (or (< w 1) (>= w 4000))) (print "invalid input"))
    (if (and (not isBlank) (not (integer? w))) (print "invalid input"))
    (while (and (not isBlank) (integer? w) (pos? w) (< w 4000) (= (count n) 1))
      (do (cond
         (>= w 1000) (do (def rom (str rom "M")) (def w (- w 1000)))
         (>= w 900) (do (def rom (str rom "CM")) (def w (- w 900)))
         (>= w 500) (do (def rom (str rom "D")) (def w (- w 500)))
         (>= w 400) (do (def rom (str rom "CD")) (def w (- w 400)))
         (>= w 100) (do (def rom (str rom "C")) (def w (- w 100)))
         (>= w 90) (do (def rom (str rom "XC")) (def w (- w 90)))
         (>= w 50) (do (def rom (str rom "L")) (def w (- w 50)))
         (>= w 40) (do (def rom (str rom "XL")) (def w (- w 40)))
         (>= w 10) (do (def rom (str rom "X")) (def w (- w 10)))
         (= w 9) (do (def rom (str rom "IX")) (def w (- w 9)))
         (>= w 5) (do (def rom (str rom "V")) (def w (- w 5)))
         (= w 4) (do (def rom (str rom "IV")) (def w (- w 4)))
         (>= w 1) (do (def rom (str rom "I")) (def w (- w 1)))
         )
        )
    )
  (println rom)
  )

;; read-roman should work this way:
;; use the romMap created with the key being each roman numeral and the value being the numerical value
;; taking this, map the input to this romMap, and use the conversion function to convert it:
      ;; reduce applies function to all elements in list
      ;; conversion multiplies current numeral's value by 4 before comparing and adds/subtracts based on result
      ;; taking this result the list is reduced to one value in reverse order

(defn conversion [prev curr]
  (if (> prev (* 4 curr))
    (- prev curr)
    (+ curr prev)))

(defn read-roman [n]
  (def rom (first n))
  (def isBlank (str/blank? rom))
  (def sum 0)
  (def check true)
  (def romMap {\I 1, \V 5, \X 10, \L 50, \C 100, \D 500, \M 1000})

  (doseq [i rom]
    (if (not (contains? romMap i)) (def check false))
    )

  (def conditions (and (not (= (str rom) "IIII")) (= (count n) 1) (not isBlank) (= check true)))

  (cond
      (and (not (= (str rom) "IIII")) (not (= (str rom) "IXCM"))(not (= (str rom) "IC"))(not (= (str rom) "XD"))
           (not (= (str rom) "ID")) (not (= (str rom) "IIXC")) (not (= (str rom) "IL"))
           (not (= (str rom) "IM"))(not (= (str rom) "XM")) (= (count n) 1) (not isBlank) (= check true))
      (def sum (reduce conversion (map romMap (reverse rom))))
                       :else (def sum "invalid input")
      )
  (def valList (vals romMap))
  (doseq [v valList]
      (if (and (> (count rom) 1) (= sum v)) (def sum "invalid input"))
      (if (and (not (and (> (count rom) 1) (= sum v))) conditions) (def sum sum))
      )
      (println sum)
)

(defn -main [& args]
  (cond (= (first args) "pascal") (pascal (rest args))
        (= (first args) "write-roman") (write-roman (rest args))
        (= (first args) "read-roman") (read-roman (rest args))
        :else (println "invalid input")
        )

  )



