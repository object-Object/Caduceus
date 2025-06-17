package gay.`object`.caduceus.casting.arithmetic

import at.petrak.hexcasting.api.casting.arithmetic.operator.Operator.Companion.downcast
import at.petrak.hexcasting.api.casting.arithmetic.predicates.IotaMultiPredicate
import at.petrak.hexcasting.api.casting.arithmetic.predicates.IotaPredicate
import at.petrak.hexcasting.api.casting.eval.vm.SpellContinuation
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.IotaType
import at.petrak.hexcasting.common.lib.hex.HexIotaTypes.CONTINUATION

fun allOfType(type: IotaType<*>) = IotaMultiPredicate.all(IotaPredicate.ofType(type))

fun downcastContinuation(iota: Iota): SpellContinuation =
    downcast(iota, CONTINUATION).continuation
