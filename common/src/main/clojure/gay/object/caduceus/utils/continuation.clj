(ns gay.object.caduceus.utils.continuation
  (:require [gay.object.caduceus.utils.component :as component])
  (:import (at.petrak.hexcasting.api HexAPI)
           (at.petrak.hexcasting.api.casting.eval.vm ContinuationFrame SpellContinuation SpellContinuation$Done SpellContinuation$NotDone)
           (at.petrak.hexcasting.api.casting.iota Iota NullIota)
           (at.petrak.hexcasting.common.lib.hex HexContinuationTypes)))

(defn done? [cont]
  (instance? SpellContinuation$Done cont))

(defn not-done? [cont]
  (instance? SpellContinuation$NotDone cont))

(defn frame [cont]
  (when (not-done? cont) (.getFrame cont)))

(defn make
  ([frame] (.pushFrame SpellContinuation$Done/INSTANCE frame))
  ([frame next] (SpellContinuation$NotDone/new frame next)))

(defn frames
  ([cont] (frames [] cont))
  ([acc cont]
   (if (done? cont)
     acc
     (recur
       (conj acc (.getFrame cont))
       (.getNext cont)))))

(definterface ContinuationMarkHolder
  (^at.petrak.hexcasting.api.casting.iota.Iota
    caduceus$getMark
    []
   "Returns the continuation mark on this frame, or NullIota if none.")
  (^at.petrak.hexcasting.api.casting.eval.vm.ContinuationFrame
    caduceus$withMark
    [^at.petrak.hexcasting.api.casting.iota.Iota mark]
   "Returns a copy of this frame with the given continuation mark."))

; CURSED
; we catch IllegalArgumentException instead of just checking instance? so other addons can add support without depending on us
(defn get-frame-mark [frame]
  (try
    (.caduceus$getMark frame)
    (catch IllegalArgumentException _ (NullIota/new))))

(defn with-frame-mark [frame iota]
  (try
    (.caduceus$withMark frame iota)
    (catch IllegalArgumentException _ frame)))

(defn get-mark [cont]
  (if-let [frame (frame cont)]
    (get-frame-mark frame)
    (NullIota/new)))

(defn with-mark [cont mark]
  (if-let [frame (frame cont)]
    (.pushFrame
      (.getNext cont)
      (with-frame-mark frame mark))
    cont))

(defn- frame-type-id [tag]
  (-> tag
      (.getString HexContinuationTypes/KEY_TYPE)
      (net.minecraft.resources.ResourceLocation/tryParse)
      (or (HexAPI/modLoc "evaluate"))))

(defn- display-frame [tag]
  (let [type-id (-> tag frame-type-id str)]
    (component/hover
     (component/translatable-with-fallback
       (format "caduceus.tooltip.continuation.frame.%s" type-id)
       type-id)
     (-> type-id component/literal component/dark-gray))))

(defn display [tag]
  (let [frames (as-> tag v
                     (.getList v
                               SpellContinuation/TAG_FRAME
                               net.minecraft.nbt.Tag/TAG_COMPOUND)
                     (mapv display-frame v))]
    (component/red
      (if (empty? frames)
        (component/translatable "caduceus.tooltip.continuation.done")
        (component/translatable "caduceus.tooltip.continuation.not_done" (component/join ", " frames))))))

(gen-class
  :name gay.object.caduceus.casting.continuation.ContinuationUtils
  :methods [^:static [display
                      [net.minecraft.nbt.Tag]
                      net.minecraft.network.chat.Component]])

(defn -display [tag] (display tag))
