package gay.`object`.caduceus.casting.arithmetic

import at.petrak.hexcasting.api.casting.arithmetic.Arithmetic
import at.petrak.hexcasting.api.casting.arithmetic.Arithmetic.*
import at.petrak.hexcasting.api.casting.arithmetic.engine.InvalidOperatorException
import at.petrak.hexcasting.api.casting.arithmetic.operator.OperatorBinary
import at.petrak.hexcasting.api.casting.arithmetic.operator.OperatorUnary
import at.petrak.hexcasting.api.casting.arithmetic.predicates.IotaMultiPredicate.all
import at.petrak.hexcasting.api.casting.arithmetic.predicates.IotaPredicate
import at.petrak.hexcasting.api.casting.eval.vm.SpellContinuation
import at.petrak.hexcasting.api.casting.iota.ContinuationIota
import at.petrak.hexcasting.api.casting.iota.DoubleIota
import at.petrak.hexcasting.api.casting.math.HexPattern
import at.petrak.hexcasting.common.lib.hex.HexIotaTypes.CONTINUATION
import gay.`object`.caduceus.casting.arithmetic.operator.OperatorUnConsContinuation
import gay.`object`.caduceus.casting.iota.frameSequence
import java.util.function.BinaryOperator

object ContinuationArithmetic : Arithmetic {
    private val OPS = listOf(
        ABS,
        ADD,
        CONS,
        UNCONS,
    )

    override fun arithName() = "continuation_ops"

    override fun opTypes() = OPS

    override fun getOperator(pattern: HexPattern?) = when (pattern) {
        ABS -> OperatorUnary(all(IotaPredicate.ofType(CONTINUATION))) { iota ->
            downcastContinuation(iota).frameSequence()
                .count()
                .toDouble()
                .let(::DoubleIota)
        }
        ADD -> make2 { i, j ->
            i.frameSequence()
                .toList()
                .asReversed()
                .fold(j, SpellContinuation::pushFrame)
        }
        CONS -> make2 { i, j ->
            when (j) {
                is SpellContinuation.NotDone -> i.pushFrame(j.frame)
                else -> i
            }
        }
        UNCONS -> OperatorUnConsContinuation
        else -> throw InvalidOperatorException("$pattern is not a valid operator in Arithmetic $this.")
    }

    private fun make2(op: BinaryOperator<SpellContinuation>): OperatorBinary =
        OperatorBinary(all(IotaPredicate.ofType(CONTINUATION))) { i, j ->
            ContinuationIota(op.apply(downcastContinuation(i), downcastContinuation(j)))
        }
}
