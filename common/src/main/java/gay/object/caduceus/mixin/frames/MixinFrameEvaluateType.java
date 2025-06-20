package gay.object.caduceus.mixin.frames;

import at.petrak.hexcasting.api.casting.eval.vm.FrameEvaluate;
import at.petrak.hexcasting.api.casting.iota.IotaType;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import gay.object.caduceus.mixin_interfaces.ContinuationMarkHolderMixin;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(targets = "at/petrak/hexcasting/api/casting/eval/vm/FrameEvaluate$Companion$TYPE$1", remap = false)
public abstract class MixinFrameEvaluateType {
    @ModifyReturnValue(method = "deserializeFromNBT", at = @At("RETURN"))
    private FrameEvaluate caduceus$deserializeMark(
        FrameEvaluate newFrame,
        @Local(argsOnly = true) CompoundTag tag,
        @Local(argsOnly = true) ServerLevel world
    ) {
        if (tag.contains(ContinuationMarkHolderMixin.TAG, Tag.TAG_COMPOUND)) {
            var mark = IotaType.deserialize(tag.getCompound(ContinuationMarkHolderMixin.TAG), world);
            ((ContinuationMarkHolderMixin)(Object)newFrame).caduceus$setMark(mark);
        }
        return newFrame;
    }
}
