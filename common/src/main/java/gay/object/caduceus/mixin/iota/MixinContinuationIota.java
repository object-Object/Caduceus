package gay.object.caduceus.mixin.iota;

import gay.object.caduceus.utils.continuation.ContinuationUtils;
import at.petrak.hexcasting.api.casting.eval.vm.SpellContinuation;
import at.petrak.hexcasting.api.casting.iota.ContinuationIota;
import com.llamalad7.mixinextras.sugar.Local;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = ContinuationIota.class, remap = false)
public abstract class MixinContinuationIota {
    @Shadow public abstract SpellContinuation getContinuation();

    @ModifyArg(
        method = "<init>",
        at = @At(
            value = "INVOKE",
            target = "Lat/petrak/hexcasting/api/casting/iota/Iota;<init>(Lat/petrak/hexcasting/api/casting/iota/IotaType;Ljava/lang/Object;)V"
        ),
        index = 1
    )
    private static @NotNull Object caduceus$copyThothAccumulatorsToStopThemFromCrashingTheGame(@NotNull Object payload) {
        return ContinuationUtils.cleanThothFrames((SpellContinuation) payload);
    }

    @Redirect(
        method = "size",
        at = @At(value = "INVOKE", target = "Ljava/lang/Math;min(II)I")
    )
    private int caduceus$stopIgnoringTheEntireSizeCalculation(int a, int b, @Local int size) {
        return size;
    }
}
