package gay.object.caduceus.mixin.frames;

import at.petrak.hexcasting.api.casting.SpellList;
import at.petrak.hexcasting.api.casting.eval.vm.FrameEvaluate;
import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.casting.iota.IotaType;
import at.petrak.hexcasting.api.casting.iota.NullIota;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import gay.object.caduceus.mixin_interfaces.ContinuationMarkHolderMixin;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@SuppressWarnings("UnreachableCode")
@Mixin(value = FrameEvaluate.class, remap = false)
public abstract class MixinFrameEvaluate implements ContinuationMarkHolderMixin {
    @Shadow public abstract SpellList getList();
    @Shadow public abstract boolean isMetacasting();
    @Shadow public abstract FrameEvaluate copy(SpellList list, boolean isMetacasting);

    @Unique
    @Nullable
    private Iota caduceus$mark;

    @Unique
    @Override
    public Iota caduceus$getMark() {
        if (caduceus$mark == null) {
            return new NullIota();
        }
        return caduceus$mark;
    }

    @Unique
    @Override
    public FrameEvaluate caduceus$withMark(Iota newMark) {
        var newFrame = copy(getList(), isMetacasting());
        ((MixinFrameEvaluate)(Object)newFrame).caduceus$mark = newMark;
        return newFrame;
    }

    @Unique
    @Override
    public void caduceus$setMark(Iota newMark) {
        caduceus$mark = newMark;
    }

    @ModifyExpressionValue(
        method = "evaluate",
        at = @At(value = "NEW", target = "at/petrak/hexcasting/api/casting/eval/vm/FrameEvaluate")
    )
    private FrameEvaluate caduceus$addMarkToCdrFrame(FrameEvaluate newFrame) {
        ((MixinFrameEvaluate)(Object)newFrame).caduceus$mark = caduceus$mark;
        return newFrame;
    }

    @ModifyReturnValue(method = "copy", at = @At("RETURN"))
    private FrameEvaluate caduceus$copyMark(FrameEvaluate newFrame) {
        ((MixinFrameEvaluate)(Object)newFrame).caduceus$mark = caduceus$mark;
        return newFrame;
    }

    @ModifyReturnValue(method = "serializeToNBT", at = @At("RETURN"))
    private CompoundTag caduceus$serializeMark(CompoundTag tag) {
        if (caduceus$mark != null) {
            tag.put(TAG, IotaType.serialize(caduceus$mark));
        }
        return tag;
    }
}
