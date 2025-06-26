(ns gay.object.caduceus.casting.iota.delimcc
  (:require [gay.object.caduceus.utils.continuation :as continuation])
  (:import (at.petrak.hexcasting.api.casting.eval CastResult ResolvedPatternType)
           (at.petrak.hexcasting.api.casting.eval.vm SpellContinuation)
           (at.petrak.hexcasting.api.casting.iota Iota IotaType)
           (at.petrak.hexcasting.api.utils HexUtils)
           (at.petrak.hexcasting.common.lib.hex HexEvalSounds)))

(declare iota-type)

; this sucks. but gen-class would suck more, because we can't instantiate it unless it's compiled separately
; TODO: should this type participate in arithmetic?

(definterface DelimitedContinuationIota
  (^at.petrak.hexcasting.api.casting.eval.vm.SpellContinuation getContinuation []))

(defn ->DelimitedContinuationIota [cont]
  (proxy
    [Iota DelimitedContinuationIota] [iota-type (continuation/clean-thoth-frames cont)]
    (getContinuation [] cont)
    (isTruthy [] true)
    (toleratesOther [that]
      (and
        (Iota/typesMatch this that)
        (= (.getContinuation this) (.getContinuation that))))
    (serialize [] (-> this .getContinuation .serializeToNBT))
    (execute [vm _world cont]
      (CastResult/new
        this
        (continuation/add (.getContinuation this) cont)
        (.getImage vm)
        []
        ResolvedPatternType/EVALUATED
        HexEvalSounds/HERMES))
    (executable [] true)
    (size []
      (->> this
           .getContinuation
           continuation/frames
           (map #(+ 1 (.size %)))
           (apply +)))))

(def iota-type
  (proxy
    [IotaType] []
    (deserialize [tag world]
      (-> tag
          (HexUtils/downcast net.minecraft.nbt.CompoundTag/TYPE)
          (SpellContinuation/fromNBT world)
          ->DelimitedContinuationIota))
    (display [tag]
      (continuation/display tag "caduceus.tooltip.continuation.delimited"))
    (color [] 0xffaa0000)))
