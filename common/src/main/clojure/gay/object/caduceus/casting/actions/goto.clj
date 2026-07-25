(ns gay.object.caduceus.casting.actions.goto
  (:require [gay.object.caduceus.casting.eval.vm.frames :as frames]
            [gay.object.caduceus.utils.casting :as casting]
            [gay.object.caduceus.utils.continuation :as continuation]
            [gay.object.caduceus.casting.mishaps.no-goto :as no-goto])
  (:import (at.petrak.hexcasting.api.casting OperatorUtils SpellList$LList)
           (at.petrak.hexcasting.api.casting.castables Action)
           (at.petrak.hexcasting.api.casting.eval OperationResult)
           (at.petrak.hexcasting.api.casting.eval.vm FrameEvaluate)
           (at.petrak.hexcasting.api.casting.mishaps MishapNotEnoughArgs)
           (at.petrak.hexcasting.common.lib.hex HexEvalSounds)
           (gay.object.caduceus.casting.eval.vm.frames GotoFrame)))

(deftype OpSetupGoto []
  Action
  (operate [_this _env image cont]
    (let [stack (-> image .getStack vec)]
      (if (empty? stack)
        (throw (MishapNotEnoughArgs/new 1 0)))
      (let [stack-size (count stack)
            code (OperatorUtils/getList stack (dec stack-size) stack-size)]
        (OperationResult/new
          (casting/copy-image
            (.withUsedOp image)
            :stack (pop stack))
          []
          (continuation/push-all
            cont
            [(frames/->GotoFrame code)
             (FrameEvaluate/new code true)])
          HexEvalSounds/HERMES)))))

(defn get-goto [cont]
  (if (continuation/done? cont)
    (throw (no-goto/->MishapNoGoto)))
  (if (instance? GotoFrame (.getFrame cont))
    cont
    (recur (.getNext cont))))

(defn goto [index code]
  (if (neg? index)
    (take-last (- index) code)
    (drop index code)))

(deftype OpGoto []
  Action
  (operate [_this _env image cont]
    (let [stack (-> image .getStack vec)]
      (if (empty? stack)
        (throw (MishapNotEnoughArgs/new 1 0)))
      (let [stack-size (count stack)
            new-cont (get-goto cont)
            code (-> new-cont .getFrame .code vec)
            code-size (count code)
            index (OperatorUtils/getIntBetween
                    stack
                    (dec stack-size)
                    (- code-size)
                    code-size
                    stack-size)]
        (OperationResult/new
          (casting/copy-image
            (.withUsedOp image)
            :stack (pop stack))
          []
          (if (= index code-size)
            (.getNext new-cont)
            (as-> code v
                  (goto index v)
                  (SpellList$LList/new 0 v)
                  (FrameEvaluate/new v true)
                  (.pushFrame new-cont v)))
          HexEvalSounds/NORMAL_EXECUTE)))))
