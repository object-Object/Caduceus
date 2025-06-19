(ns gay.object.caduceus.casting.eval.vm.frames
  (:import (at.petrak.hexcasting.api.casting.eval CastResult ResolvedPatternType)
           (at.petrak.hexcasting.api.casting.eval.vm ContinuationFrame ContinuationFrame$Type)
           (at.petrak.hexcasting.api.casting.iota NullIota)
           (at.petrak.hexcasting.common.lib.hex HexEvalSounds)
           (kotlin Pair)))

(declare prompt-frame-type)

; A stack marker (like FrameFinishEval) for delimited continuations.
; TODO: does this being a singleton mess up HexDebug?
; TODO: implement tagged prompts?
(def prompt-frame
  (reify
    ContinuationFrame
    (breakDownwards [_this stack] (Pair/new false stack))
    (evaluate [_this cont _level _harness]
      (CastResult/new
        (NullIota/new)
        cont
        nil
        []
        ResolvedPatternType/EVALUATED
        HexEvalSounds/NOTHING))
    (serializeToNBT [_this] (net.minecraft.nbt.CompoundTag/new))
    (size [_this] 0)
    (getType [_this] prompt-frame-type)))

(def prompt-frame-type
  (reify
    ContinuationFrame$Type
    (deserializeFromNBT [_this _tag _world] prompt-frame)))
