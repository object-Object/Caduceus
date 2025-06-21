(ns gay.object.caduceus.casting.actions.marks
  (:require [gay.object.caduceus.utils.casting :as casting]
            [gay.object.caduceus.utils.continuation :as continuation])
  (:import (at.petrak.hexcasting.api.casting.castables Action ConstMediaAction ConstMediaAction$DefaultImpls)
           (at.petrak.hexcasting.api.casting.eval OperationResult)
           (at.petrak.hexcasting.api.casting.mishaps MishapInvalidIota MishapNotEnoughArgs MishapOthersName)
           (at.petrak.hexcasting.common.lib.hex HexEvalSounds)))

(deftype OpReadLocalMark []
  Action
  (operate [_this _env image cont]
    (let [mark (continuation/get-mark cont)]
      (OperationResult/new
        (casting/copy-image
          (.withUsedOp image)
          :stack (-> image .getStack vec (conj mark))) ; convert to vec because conj doesn't seem to work on java types
        []
        cont
        HexEvalSounds/NORMAL_EXECUTE))))

(deftype OpReadIotaMark []
  ConstMediaAction
  (getArgc [_this] 1)
  (getMediaCost [_this] 0)
  (execute [this args _env]
    (-> args
        (casting/get-continuation 0 (.getArgc this))
        continuation/get-mark
        vector))
  (executeWithOpCount [this args env]
    (ConstMediaAction$DefaultImpls/executeWithOpCount this args env))
  (operate [this env image cont]
    (ConstMediaAction$DefaultImpls/operate this env image cont)))

(deftype OpWriteLocalMark []
  Action
  (operate [_this env image cont]
    (let [stack (-> image .getStack vec)
          mark (last stack)]
      (cond
        (nil? mark) (throw (MishapNotEnoughArgs/new 1 0))
        (> (.size mark) 1) (throw (MishapInvalidIota/ofType mark 0 "continuation_mark")))
      (when-let [true-name (MishapOthersName/getTrueNameFromDatum mark nil)]
        (throw (MishapOthersName/new true-name)))
      (continuation/set-mark cont mark (.getWorld env))
      (OperationResult/new
        (casting/copy-image
          (.withUsedOp image)
          :stack (pop stack))
        []
        cont
        HexEvalSounds/NORMAL_EXECUTE))))
