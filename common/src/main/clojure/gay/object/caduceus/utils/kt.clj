(ns gay.object.caduceus.utils.kt
  (:require [clojure.string :as str]))

(defmacro copy-dataclass
  [instance args & fields]
  `(.copy
     ~instance
     ~@(for [field fields]
         `(get ~args ~field
               (. ~instance
                  ~(as-> field v
                         (name v)
                         (apply str
                                "get"
                                (-> v first str/upper-case)
                                (rest v))
                         (symbol v)))))))
