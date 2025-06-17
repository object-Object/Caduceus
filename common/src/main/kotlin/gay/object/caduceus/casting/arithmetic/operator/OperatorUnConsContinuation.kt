package gay.`object`.caduceus.casting.arithmetic.operator

import at.petrak.hexcasting.api.casting.arithmetic.operator.OperatorBasic
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.eval.vm.SpellContinuation
import at.petrak.hexcasting.api.casting.iota.ContinuationIota
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.common.lib.hex.HexIotaTypes.CONTINUATION
import gay.`object`.caduceus.casting.arithmetic.allOfType
import gay.`object`.caduceus.casting.arithmetic.downcastContinuation

object OperatorUnConsContinuation : OperatorBasic(1, allOfType(CONTINUATION)) {
    override fun apply(iotas: Iterable<Iota>, env: CastingEnvironment): Iterable<Iota> =
        when (val cont = downcastContinuation(iotas.first())) {
            is SpellContinuation.NotDone -> listOf(
                cont.next,
                cont.copy(next = SpellContinuation.Done)
            )
            else -> listOf(cont, cont)
        }.map(::ContinuationIota)
}
