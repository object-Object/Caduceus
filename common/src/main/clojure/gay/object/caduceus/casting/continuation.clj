(ns gay.object.caduceus.casting.continuation
  (:require [clojure.string :as str]
            [gay.object.caduceus.component :as component])
  (:import (at.petrak.hexcasting.api HexAPI)
           (at.petrak.hexcasting.api.casting.eval.vm SpellContinuation SpellContinuation$Done SpellContinuation$NotDone)
           (at.petrak.hexcasting.common.lib.hex HexContinuationTypes)
           (net.minecraft ChatFormatting)
           (net.minecraft.nbt CompoundTag Tag)
           (net.minecraft.network.chat Component)
           (net.minecraft.resources ResourceLocation)))

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

(defn- frame-type-id [^CompoundTag tag]
  (-> tag
      (.getString HexContinuationTypes/KEY_TYPE)
      (ResourceLocation/tryParse)
      (or (HexAPI/modLoc "evaluate"))))

(defn- display-frame [^CompoundTag tag]
  (let [type-id (str (frame-type-id tag))]
    (Component/translatableWithFallback
      (format "caduceus.tooltip.continuation.frame.%s" type-id)
      type-id)))

(defn display [^CompoundTag tag]
  (let [frames (as-> tag v
                     (.getList v SpellContinuation/TAG_FRAME Tag/TAG_COMPOUND)
                     (mapv display-frame v))]
    (.withStyle
      (if (empty? frames)
        (component/translatable "caduceus.tooltip.continuation.done")
        (component/translatable "caduceus.tooltip.continuation.not_done" (component/join ", " frames)))
      ChatFormatting/RED)))

(gen-class
  :name gay.object.caduceus.casting.continuation.ContinuationUtils
  :methods [^:static [display [net.minecraft.nbt.Tag] net.minecraft.network.chat.Component]])

(defn -display [tag] (display tag))
