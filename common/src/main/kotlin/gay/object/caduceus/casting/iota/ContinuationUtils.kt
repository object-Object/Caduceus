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

fun getFrameI18n(tag: CompoundTag): Component {
    // ContinuationFrame.fromNBT returns an empty FrameEvaluate if getTypeFromTag returns null
    val typeKey = getFrameTypeKey(tag) ?: HexAPI.modLoc("evaluate")

    // if a continuation frame doesn't have a translation key, just use the id
    return Component.translatableWithFallback(
        "caduceus.tooltip.continuation.frame.$typeKey",
        typeKey.toString()
    )
}

fun displayContinuation(tag: Tag): Component {
    val frames = tag.downcast(CompoundTag.TYPE)
        .getList(TAG_FRAME, Tag.TAG_COMPOUND)
        .asSequence()
        .mapNotNull { it as? CompoundTag }
        .map { getFrameI18n(it) }
        .take(2)
        .toList()
        .toTypedArray()

    return when (frames.size) {
        0 -> "caduceus.tooltip.continuation.done"
        1 -> "caduceus.tooltip.continuation.not_done.one"
        else -> "caduceus.tooltip.continuation.not_done.many"
    }.asTranslatedComponent(*frames).red
}
