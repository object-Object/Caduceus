package gay.object.caduceus.mixin.actions;

import at.petrak.hexcasting.api.casting.castables.ConstMediaAction;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.common.casting.actions.lists.OpSplat;
import gay.object.caduceus.casting.actions.mixin.OpSplatContinuation;
import gay.object.caduceus.casting.iota.ContinuationUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(OpSplat.class)
public abstract class MixinOpSplat implements ConstMediaAction {
    @Inject(method = "execute", at = @At("HEAD"), cancellable = true, remap = false)
    private void caduceus$overloadContinuation(
        List<? extends Iota> args,
        CastingEnvironment env,
        CallbackInfoReturnable<List<Iota>> cir
    ) {
        ContinuationUtils.getContinuationOrList(args, 0, getArgc()).ifLeft(cont ->
            cir.setReturnValue(OpSplatContinuation.execute(args, env))
        );
    }
}
