(ns gay.object.caduceus.utils.casting
  (:require [gay.object.caduceus.utils.kt :as kt])
  (:import (at.petrak.hexcasting.api.casting.eval.vm CastingImage)
           (at.petrak.hexcasting.api.casting.iota ContinuationIota)
           (at.petrak.hexcasting.api.casting.mishaps MishapInvalidIota MishapNotEnoughArgs)))

(defn- reverse-idx [idx argc]
  (if (zero? argc)
    idx
    (- argc (inc idx))))

(defn get-continuation
  ([args idx] (get-continuation args idx 0))
  ([args idx argc]
   (let [datum (nth args idx nil)]
     (cond
       (instance? ContinuationIota datum)
       (.getContinuation ^ContinuationIota datum)

       (nil? datum)
       (throw (MishapNotEnoughArgs/new
                (inc idx)
                (count args)))

       :else
       (throw (MishapInvalidIota/ofType
                datum
                (reverse-idx idx argc)
                "continuation"))))))

(defn copy-image [^CastingImage image & {:as args}]
  (kt/copy-dataclass
    image args
    :stack
    :parenCount
    :parenthesized
    :escapeNext
    :opsConsumed
    :userData))
