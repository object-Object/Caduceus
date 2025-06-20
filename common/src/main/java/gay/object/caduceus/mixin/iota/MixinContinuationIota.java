package gay.object.caduceus.mixin.iota;

import at.petrak.hexcasting.api.casting.eval.vm.SpellContinuation;
import at.petrak.hexcasting.api.casting.iota.ContinuationIota;
import com.llamalad7.mixinextras.sugar.Local;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = ContinuationIota.class, remap = false)
public abstract class MixinContinuationIota {
    @Shadow public abstract SpellContinuation getContinuation();

    @Redirect(
        method = "size",
        at = @At(value = "INVOKE", target = "Ljava/lang/Math;min(II)I"),
        remap = false
    )
    private int caduceus$stopIgnoringTheEntireSizeCalculation(int a, int b, @Local int size) {
        return size;
    }
}
