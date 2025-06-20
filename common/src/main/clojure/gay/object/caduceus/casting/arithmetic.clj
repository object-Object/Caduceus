(ns gay.object.caduceus.casting.arithmetic
  (:require [gay.object.caduceus.utils.continuation :as continuation])
  (:import (at.petrak.hexcasting.api.casting.arithmetic Arithmetic)
           (at.petrak.hexcasting.api.casting.arithmetic.operator OperatorBasic)
           (at.petrak.hexcasting.api.casting.arithmetic.predicates IotaMultiPredicate IotaPredicate)
           (at.petrak.hexcasting.api.casting.eval.vm SpellContinuation)
           (at.petrak.hexcasting.api.casting.iota ContinuationIota DoubleIota)
           (at.petrak.hexcasting.common.lib.hex HexIotaTypes)))

(defn- all-of-type [iota-type]
  (IotaMultiPredicate/all (IotaPredicate/ofType iota-type)))

(defn- make-op [n op]
  (proxy
    [OperatorBasic]
    [n (all-of-type HexIotaTypes/CONTINUATION)]
    (apply [iotas _env]
      (as-> iotas v
            (mapv #(.getContinuation %) v)
            (apply op v)
            (cond
              (sequential? v) v
              (instance? SpellContinuation v) [(ContinuationIota/new v)]
              :else [v])))))

(defn- make1 [op] (make-op 1 op))

(defn- make2 [op] (make-op 2 op))

(deftype ContinuationArithmetic []
  Arithmetic
  (arithName [_this] "continuation_ops")
  (opTypes [_this] [Arithmetic/ABS
                    Arithmetic/ADD
                    Arithmetic/CONS
                    Arithmetic/UNCONS])
  (getOperator [_this pattern]
    (condp = pattern
      Arithmetic/ABS (make1
                       (fn [cont]
                         (-> cont
                             (continuation/frames)
                             (count)
                             (double)
                             (DoubleIota/new))))
      Arithmetic/ADD (make2
                       (fn [i j]
                         (->> i
                              (continuation/frames '()) ; get frames in reverse order
                              (reduce #(.pushFrame %1 %2) j))))
      Arithmetic/CONS (make2
                        (fn [i j]
                          (if (continuation/not-done? j)
                            (.pushFrame i (.getFrame j))
                            i)))
      Arithmetic/UNCONS (make1
                          (fn [cont]
                            (mapv
                              ContinuationIota/new
                              (if (continuation/not-done? cont)
                                [(.getNext cont)
                                 (continuation/make (.getFrame cont))]
                                [cont cont])))))))
