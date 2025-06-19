(ns gay.object.caduceus.casting.actions.mixins
  (:require [gay.object.caduceus.utils.continuation :as continuation])
  (:import (at.petrak.hexcasting.api.casting.iota ContinuationIota)
           (java.util ArrayList Collection)))

(gen-class
  :name gay.object.caduceus.casting.actions.mixins.OpSplatContinuation
  :prefix OpSplatContinuation-
  :methods [^:static [execute
                      [at.petrak.hexcasting.api.casting.eval.vm.SpellContinuation]
                      java.util.List]])

(defn OpSplatContinuation-execute [cont]
  (->> cont
       (continuation/frames)
       (map continuation/make)
       (mapv ContinuationIota/new)
       (^[Collection] ArrayList/new)))
