(ns gay.object.caduceus.casting.mishaps.no-prompt
  (:require [gay.object.caduceus.utils.component :as component])
  (:import (at.petrak.hexcasting.api.casting.mishaps Mishap)
           (at.petrak.hexcasting.api.pigment FrozenPigment)
           (at.petrak.hexcasting.common.lib HexItems)))

(defn dye-color [color]
  (FrozenPigment/new
    (^[net.minecraft.world.level.ItemLike] net.minecraft.world.item.ItemStack/new
      (.get HexItems/DYE_PIGMENTS color))
    net.minecraft.Util/NIL_UUID))

(defn ->MishapNoPrompt []
  (proxy
    [Mishap] []
    (accentColor [_ctx _errorCtx]
      (dye-color net.minecraft.world.item.DyeColor/RED))
    (execute [env _errorCtx _stack]
      ; TODO: come up with a better mishap instead of just stealing EvalTooMuch
      (-> env .getMishapEnvironment .drown))
    (errorMessage [_ctx _errorCtx]
      (component/translatable "hexcasting.mishap.no_prompt"))))
