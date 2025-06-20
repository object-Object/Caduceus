package gay.object.caduceus.mixin.frames;

import at.petrak.hexcasting.api.casting.SpellList;
import at.petrak.hexcasting.api.casting.eval.vm.FrameForEach;
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

import java.util.List;

@SuppressWarnings("UnreachableCode")
@Mixin(value = FrameForEach.class, remap = false)
public abstract class MixinFrameForEach implements ContinuationMarkHolderMixin {
    @Shadow public abstract SpellList getData();
    @Shadow public abstract SpellList getCode();
    @Shadow public abstract List<Iota> getBaseStack();
    @Shadow @Nullable public abstract List<Iota> getAcc();
    @Shadow public abstract FrameForEach copy(SpellList data, SpellList code, List<Iota> baseStack, List<Iota> acc);

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
    public FrameForEach caduceus$withMark(Iota newMark) {
        var newFrame = copy(getData(), getCode(), getBaseStack(), getAcc());
        ((MixinFrameForEach)(Object)newFrame).caduceus$mark = newMark;
        return newFrame;
    }

    @Unique
    @Override
    public void caduceus$setMark(Iota newMark) {
        caduceus$mark = newMark;
    }

    @ModifyExpressionValue(
        method = "evaluate",
        at = @At(value = "NEW", target = "at/petrak/hexcasting/api/casting/eval/vm/FrameForEach")
    )
    private FrameForEach caduceus$addMarkToCdrFrame(FrameForEach newFrame) {
        ((MixinFrameForEach)(Object)newFrame).caduceus$mark = caduceus$mark;
        return newFrame;
    }

    @ModifyReturnValue(method = "copy", at = @At("RETURN"))
    private FrameForEach caduceus$copyMark(FrameForEach newFrame) {
        ((MixinFrameForEach)(Object)newFrame).caduceus$mark = caduceus$mark;
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
