(ns gay.object.caduceus.casting.actions.delimcc
  (:require [gay.object.caduceus.casting.eval.vm.frames :as frames]
            [gay.object.caduceus.utils.continuation :as continuation]
            [gay.object.caduceus.casting.mishaps.no-prompt :as no-prompt]
            [gay.object.caduceus.casting.iota.delimcc :as delimcc])
  (:import (at.petrak.hexcasting.api.casting.castables Action)
           (at.petrak.hexcasting.api.casting.mishaps MishapNotEnoughArgs)
           (at.petrak.hexcasting.common.casting.actions.eval OpEval)
           (gay.object.caduceus.casting.eval.vm.frames PromptFrame)
           (java.util ArrayList Collection)))

; from alwinfy:
;   prompt: eval/wrapping, takes a list
;   control: like iris, but
;   (1) only captures the frames up to the last prompt in the call stack
;   (2) ejects those frames as if an exception had been thrown up to the prompt
;   (3) pushes the slice taken in (1), then invokes the code argument
;   invoking those captured frames pushes them to the stack again

(deftype OpPrompt []
  Action
  (operate [_this env image cont]
    (.operate OpEval/INSTANCE
              env
              image
              (.pushFrame cont frames/prompt-frame))))

(defn split-at-prompt
  ([cont] (split-at-prompt '() cont))
  ([coll cont]
   (if (or (continuation/done? cont)
           (instance? PromptFrame (.getFrame cont)))
     [(continuation/push-all coll) cont]
     (recur
       (conj coll (.getFrame cont))
       (.getNext cont)))))

(deftype OpControl []
  Action
  (operate [_this env image cont]
    (let [stack (-> image .getStack vec)
          [slice new-cont] (split-at-prompt cont)]
      (if (empty? stack)
        (throw (MishapNotEnoughArgs/new 1 0)))
      (if (continuation/done? new-cont)
        (throw (no-prompt/->MishapNoPrompt)))
      (.exec OpEval/INSTANCE
             env
             image
             new-cont
             (-> stack
                 pop
                 (conj (delimcc/->DelimitedContinuationIota slice))
                 ^[Collection] ArrayList/new)
             (last stack)))))
