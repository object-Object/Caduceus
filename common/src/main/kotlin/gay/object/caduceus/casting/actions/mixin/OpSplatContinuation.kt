package gay.`object`.caduceus.casting.actions.mixin

import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.eval.vm.SpellContinuation
import at.petrak.hexcasting.api.casting.iota.ContinuationIota
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.common.casting.actions.lists.OpSplat
import gay.`object`.caduceus.casting.iota.frameSequence
import gay.`object`.caduceus.casting.iota.getContinuation

@Suppress("UNUSED_PARAMETER")
object OpSplatContinuation {
    val argc: Int by OpSplat::argc

    // TODO: should the order be reversed?
    @JvmStatic
    fun execute(args: List<Iota>, env: CastingEnvironment): List<Iota> =
        args.getContinuation(0, argc)
            .frameSequence()
            .map { ContinuationIota(SpellContinuation.Done.pushFrame(it)) }
            .toList()
}
