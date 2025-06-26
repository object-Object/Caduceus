(ns gay.object.caduceus.utils.continuation
  (:require [clojure.string :as str]
            [gay.object.caduceus.utils.component :as component])
  (:import (at.petrak.hexcasting.api HexAPI)
           (at.petrak.hexcasting.api.casting.eval.vm SpellContinuation SpellContinuation$Done SpellContinuation$NotDone)
           (at.petrak.hexcasting.api.casting.iota IotaType NullIota)
           (at.petrak.hexcasting.common.lib.hex HexContinuationTypes HexIotaTypes)
           (dev.architectury.platform Mod Platform)))

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

(defn push-all
  "Takes a sequence of frames from bottom to top, and pushes them to a continuation."
  ([coll] (push-all SpellContinuation$Done/INSTANCE coll))
  ([cont coll]
   (reduce #(.pushFrame %1 %2) cont coll)))

(defn add [i j]
  (->> i
       (frames '()) ; get frames in reverse order
       (push-all j)))

(definterface ContinuationMarkHolder
  (^at.petrak.hexcasting.api.casting.iota.Iota
    caduceus$getMark
    []
   "Returns the continuation mark on this frame, or NullIota if none.")
  (^void
    caduceus$setMark
    [^at.petrak.hexcasting.api.casting.iota.Iota mark]
   "Mutably sets the continuation mark on this frame. DO NOT CALL THIS METHOD ON SINGLETONS!"))

; CURSED
; we catch IllegalArgumentException instead of just checking instance? so other addons can add support without depending on us
(defn get-frame-mark
  ([frame] (get-frame-mark frame nil))
  ([frame not-found]
   (if (instance? ContinuationMarkHolder frame)
     (.caduceus$getMark frame)
     not-found)))

(defn get-mark [cont]
  (if-let [frame (frame cont)]
    (get-frame-mark frame (NullIota/new))
    (NullIota/new)))

(defn set-frame-mark [frame iota world]
  (let [frame-type (.getType frame)]
    (when (and
            (instance? ContinuationMarkHolder frame)
            ; CURSED: pass the frame through serialization to detect singletons
            (-> frame
                .serializeToNBT
                (#(.deserializeFromNBT frame-type % world))
                (identical? frame)
                not))
      (.caduceus$setMark frame iota))))

(defn set-mark [cont mark world]
  (when-let [frame (frame cont)]
    (set-frame-mark frame mark world)))

(defn- frame-tag-type-id ^net.minecraft.resources.ResourceLocation [^net.minecraft.nbt.CompoundTag tag]
  (-> tag
      (.getString HexContinuationTypes/KEY_TYPE)
      (net.minecraft.resources.ResourceLocation/tryParse)
      (or (HexAPI/modLoc "evaluate"))))

(def MARK-TAG "caduceus:mark")

(defn- get-frame-tag-mark [^net.minecraft.nbt.CompoundTag tag]
  (if (.contains tag MARK-TAG net.minecraft.nbt.Tag/TAG_COMPOUND)
    (let [mark-tag (.getCompound tag MARK-TAG)
          mark-type (.getString mark-tag HexIotaTypes/KEY_TYPE)]
      (when-not (= mark-type "hexcasting:null")
        mark-tag))))

(defn- mod-name [id]
  (if-let [mod (-> id Platform/getOptionalMod (.orElse nil))]
    (.getName ^Mod mod) ; type hint is required to prevent reflection from loading client method on server
    (as-> id v
          (str/split v #"_")
          (map str/capitalize v)
          (str/join " " v))))

(defn- display-frame [tag]
  (let [type-id (frame-tag-type-id tag)
        type-str (str type-id)
        mark-tag (get-frame-tag-mark tag)
        name (component/translatable-with-fallback
               (format "caduceus.tooltip.continuation.frame.%s" type-str)
               type-str)]
    (component/hover
      (if mark-tag
        (component/translatable "caduceus.tooltip.continuation.frame.mark.inline" name)
        name)
      (component/join-some "\n" [name
                                 (when mark-tag
                                   (component/translatable
                                     "caduceus.tooltip.continuation.frame.mark.hover"
                                     (IotaType/getDisplay mark-tag)))
                                 (-> type-id
                                     .getNamespace
                                     mod-name
                                     component/literal
                                     component/blue
                                     component/italic)
                                 (-> type-str
                                     component/literal
                                     component/dark-gray)]))))

(defn display
  ([tag] (display tag "caduceus.tooltip.continuation"))
  ([tag i18n-key]
   (->> tag
        (#(.getList ^net.minecraft.nbt.CompoundTag % SpellContinuation/TAG_FRAME net.minecraft.nbt.Tag/TAG_COMPOUND))
        (mapv display-frame)
        (component/join ", ")
        (component/translatable i18n-key)
        component/red)))

(gen-class
  :name gay.object.caduceus.utils.continuation.ContinuationUtils
  :methods [^:static [getMarkTagKey [] String]
            ^:static [display
                      [net.minecraft.nbt.Tag]
                      net.minecraft.network.chat.Component]
            ^:static [^{org.jetbrains.annotations.Nullable {}} getFrameMark
                      [at.petrak.hexcasting.api.casting.eval.vm.ContinuationFrame]
                      at.petrak.hexcasting.api.casting.iota.Iota]])

(defn -getMarkTagKey [] MARK-TAG)

(defn -display [tag] (display tag))

(defn -getFrameMark [frame] (get-frame-mark frame))
