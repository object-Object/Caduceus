package gay.object.caduceus.mixin;

import at.petrak.hexcasting.api.casting.iota.IotaType;
import gay.object.caduceus.utils.continuation.ContinuationMarkHolder;
import gay.object.caduceus.utils.continuation.ContinuationUtils;
import at.petrak.hexcasting.api.casting.eval.vm.ContinuationFrame;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ContinuationFrame.Companion.class)
public class MixinContinuationFrameCompanion {
    @ModifyReturnValue(method = "fromNBT", at = @At("RETURN"))
    private ContinuationFrame caduceus$deserializeMark(ContinuationFrame frame, CompoundTag tag, ServerLevel world) {
        if (
            frame instanceof ContinuationMarkHolder holder
            && tag.contains(ContinuationUtils.getMarkTagKey(), Tag.TAG_COMPOUND)
            // CURSED: pass the frame through serialization to detect singletons
            && frame.getType().deserializeFromNBT(frame.serializeToNBT(), world) != frame
        ) {
            var markTag = tag.getCompound(ContinuationUtils.getMarkTagKey());
            var mark = IotaType.deserialize(markTag, world);
            holder.caduceus$setMark(mark);
        }
        return frame;
    }

    @ModifyReturnValue(method = "toNBT", at = @At("RETURN"))
    private CompoundTag caduceus$serializeMark(CompoundTag tag, ContinuationFrame frame) {
        if (frame instanceof ContinuationMarkHolder holder) {
            var markTag = IotaType.serialize(holder.caduceus$getMark());
            tag.put(ContinuationUtils.getMarkTagKey(), markTag);
        }
        return tag;
    }
}
