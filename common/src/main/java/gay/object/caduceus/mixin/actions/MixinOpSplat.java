package gay.object.caduceus.mixin.actions;

import at.petrak.hexcasting.api.casting.castables.ConstMediaAction;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.iota.ContinuationIota;
import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.casting.mishaps.Mishap;
import at.petrak.hexcasting.api.casting.mishaps.MishapNotEnoughArgs;
import at.petrak.hexcasting.common.casting.actions.lists.OpSplat;
import gay.object.caduceus.casting.actions.mixins.OpSplatContinuation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import java.util.List;

@Mixin(OpSplat.class)
public abstract class MixinOpSplat implements ConstMediaAction {
    @SuppressWarnings("unchecked")
    @Inject(method = "execute", at = @At("HEAD"), cancellable = true, remap = false)
    private void caduceus$overloadContinuation(
        List<? extends Iota> args,
        CastingEnvironment env,
        CallbackInfoReturnable<List<Iota>> cir
    ) throws Mishap {
        if (args.isEmpty()) {
            throw new MishapNotEnoughArgs(1, 0);
        }
        var datum = args.get(0);
        if (datum instanceof ContinuationIota iota) {
            cir.setReturnValue(OpSplatContinuation.execute(iota.getContinuation()));
        }
    }
}
