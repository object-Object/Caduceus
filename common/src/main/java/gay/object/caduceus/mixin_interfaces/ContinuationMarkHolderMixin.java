package gay.object.caduceus.mixin_interfaces;

import gay.object.caduceus.utils.continuation.ContinuationMarkHolder;
import at.petrak.hexcasting.api.casting.iota.Iota;

public interface ContinuationMarkHolderMixin extends ContinuationMarkHolder {
    /** only for use in ContinuationFrame.Type deserializer mixins */
    void caduceus$setMark(Iota mark);

    String TAG = "caduceus:mark";
}
