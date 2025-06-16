@file:JvmName("ContinuationUtils")

package gay.`object`.caduceus.casting.iota

import at.petrak.hexcasting.api.HexAPI
import at.petrak.hexcasting.api.casting.eval.vm.SpellContinuation.Companion.TAG_FRAME
import at.petrak.hexcasting.api.utils.asTranslatedComponent
import at.petrak.hexcasting.api.utils.downcast
import at.petrak.hexcasting.api.utils.getList
import at.petrak.hexcasting.api.utils.red
import at.petrak.hexcasting.common.lib.hex.HexContinuationTypes
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.Tag
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation

fun getFrameTypeKey(tag: CompoundTag): ResourceLocation? {
    if (!tag.contains(HexContinuationTypes.KEY_TYPE, Tag.TAG_STRING.toInt())) {
        return null
    }

    val typeKey = tag.getString(HexContinuationTypes.KEY_TYPE)
    if (!ResourceLocation.isValidResourceLocation(typeKey)) {
        return null
    }

    return ResourceLocation(typeKey)
}

fun displayContinuation(tag: Tag): Component {
    val nbt = tag.downcast(CompoundTag.TYPE)
    val frames = nbt.getList(TAG_FRAME, Tag.TAG_COMPOUND)
    val frame = frames.firstNotNullOfOrNull { it as? CompoundTag }

    return when (frame) {
        null -> "caduceus.tooltip.continuation.done".asTranslatedComponent
        else -> {
            // ContinuationFrame.fromNBT returns an empty FrameEvaluate if getTypeFromTag returns null
            val typeKey = getFrameTypeKey(frame) ?: HexAPI.modLoc("evaluate")

            // if a continuation frame doesn't have a translation key, just use the id
            val name = Component.translatableWithFallback(
                "caduceus.tooltip.continuation.frame.$typeKey",
                typeKey.toString()
            )

            "caduceus.tooltip.continuation.not_done".asTranslatedComponent(name)
        }
    }.red
}
