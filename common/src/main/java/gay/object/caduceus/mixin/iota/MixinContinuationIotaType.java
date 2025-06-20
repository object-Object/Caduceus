package gay.object.caduceus.mixin.iota;

import gay.object.caduceus.utils.continuation.ContinuationUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "at.petrak.hexcasting.api.casting.iota.ContinuationIota$1", remap = false)
public abstract class MixinContinuationIotaType {
    @Inject(method = "display", at = @At("HEAD"), cancellable = true)
    private void caduceus$betterDisplay(Tag tag, CallbackInfoReturnable<Component> cir) {
        var component = ContinuationUtils.display(tag);
        cir.setReturnValue(component);
    }
}
