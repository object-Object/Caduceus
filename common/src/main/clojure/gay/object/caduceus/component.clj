(ns gay.object.caduceus.component
  (:import (net.minecraft.network.chat Component)))

(defn translatable [key & args]
  (Component/translatable key (object-array args)))

(defn join [separator coll]
  (reduce
    #(-> %1
         (.append separator)
         (.append %2))
    (.copy (first coll))
    (rest coll)))
