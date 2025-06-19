(ns gay.object.caduceus.utils.continuation
  (:require [gay.object.caduceus.utils.component :as component])
  (:import (at.petrak.hexcasting.api HexAPI)
           (at.petrak.hexcasting.api.casting.eval.vm SpellContinuation SpellContinuation$Done SpellContinuation$NotDone)
           (at.petrak.hexcasting.common.lib.hex HexContinuationTypes)))

(defn done? [cont]
  (instance? SpellContinuation$Done cont))

(defn not-done? [cont]
  (instance? SpellContinuation$NotDone cont))

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

(defn- frame-type-id [tag]
  (-> tag
      (.getString HexContinuationTypes/KEY_TYPE)
      (net.minecraft.resources.ResourceLocation/tryParse)
      (or (HexAPI/modLoc "evaluate"))))

(defn- display-frame [tag]
  (let [type-id (str (frame-type-id tag))]
    (component/translatable-with-fallback
      (format "caduceus.tooltip.continuation.frame.%s" type-id)
      type-id)))

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
