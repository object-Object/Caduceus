@file:JvmName("ContinuationUtils")

package gay.`object`.caduceus.casting.iota

import at.petrak.hexcasting.api.HexAPI
import at.petrak.hexcasting.api.casting.SpellList
import at.petrak.hexcasting.api.casting.eval.vm.ContinuationFrame
import at.petrak.hexcasting.api.casting.eval.vm.SpellContinuation
import at.petrak.hexcasting.api.casting.eval.vm.SpellContinuation.Companion.TAG_FRAME
import at.petrak.hexcasting.api.casting.iota.ContinuationIota
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.ListIota
import at.petrak.hexcasting.api.casting.mishaps.MishapInvalidIota
import at.petrak.hexcasting.api.casting.mishaps.MishapNotEnoughArgs
import at.petrak.hexcasting.api.utils.asTranslatedComponent
import at.petrak.hexcasting.api.utils.downcast
import at.petrak.hexcasting.api.utils.getList
import at.petrak.hexcasting.api.utils.red
import at.petrak.hexcasting.common.lib.hex.HexContinuationTypes
import com.mojang.datafixers.util.Either
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

fun List<Iota>.getContinuation(idx: Int, argc: Int = 0): SpellContinuation {
    val x = getOrElse(idx) { throw MishapNotEnoughArgs(idx + 1, size) }
    if (x is ContinuationIota) {
        return x.continuation
    } else {
        throw MishapInvalidIota.ofType(x, if (argc == 0) idx else argc - (idx + 1), "continuation")
    }
}

fun List<Iota>.getContinuationOrList(idx: Int, argc: Int = 0): Either<SpellContinuation, SpellList> {
    val datum = getOrElse(idx) { throw MishapNotEnoughArgs(idx + 1, size) }
    return when (datum) {
        is ContinuationIota -> Either.left(datum.continuation)
        is ListIota -> Either.right(datum.list)
        else -> throw MishapInvalidIota.of(
            datum,
            if (argc == 0) idx else argc - (idx + 1),
            "continuation_or_list"
        )
    }
}

/** Returns a sequence of [SpellContinuation.NotDone] starting at the top of the continuation stack. */
fun SpellContinuation.continuationSequence(): Sequence<SpellContinuation.NotDone> = sequence {
    var cont = this@continuationSequence
    while (cont is SpellContinuation.NotDone) {
        yield(cont)
        cont = cont.next
    }
}

/** Returns a sequence of [ContinuationFrame] starting at the top of the continuation stack. */
fun SpellContinuation.frameSequence(): Sequence<ContinuationFrame> =
    continuationSequence().map { it.frame }
