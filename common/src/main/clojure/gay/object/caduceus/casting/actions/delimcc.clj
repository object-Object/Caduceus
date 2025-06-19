(ns gay.object.caduceus.casting.actions.delimcc
  (:require [gay.object.caduceus.casting.eval.vm.frames :as frames])
  (:import (at.petrak.hexcasting.api.casting.castables Action)
           (at.petrak.hexcasting.common.casting.actions.eval OpEval)))

; from alwinfy:
;   prompt: eval/wrapping, takes a list
;   control: like iris, but
;   (1) only captures the frames up to the last prompt in the call stack
;   (2) ejects those frames as if an exception had been thrown up to the prompt
;   (3) pushes the slice taken in (1), then invokes the code argument
;   invoking those captured frames pushes them to the stack again

(def op-prompt
  (reify
    Action
    (operate [_self env image cont]
      (.operate OpEval/INSTANCE
                env
                image
                (.pushFrame cont frames/prompt-frame)))))

(def op-control
  (reify
    Action
    (operate [_self env image cont])))
