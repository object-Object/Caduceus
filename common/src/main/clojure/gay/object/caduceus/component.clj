(ns gay.object.caduceus.component
  (:import (net.minecraft.network.chat Component MutableComponent)))

(defn ^MutableComponent translatable [^String key & args]
  (Component/translatable key (object-array args)))

(defn ^MutableComponent join [^String separator coll]
  (reduce
    #(-> %1
         (.append separator)
         (.append ^Component %2))
    (.copy (first coll))
    (rest coll)))
