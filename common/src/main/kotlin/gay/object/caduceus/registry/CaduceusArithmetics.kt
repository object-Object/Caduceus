package gay.`object`.caduceus.registry

import at.petrak.hexcasting.api.casting.arithmetic.Arithmetic
import at.petrak.hexcasting.common.lib.HexRegistries
import at.petrak.hexcasting.common.lib.hex.HexArithmetics
import gay.`object`.caduceus.casting.arithmetic.ContinuationArithmetic

object CaduceusArithmetics : CaduceusRegistrar<Arithmetic>(
    HexRegistries.ARITHMETIC,
    { HexArithmetics.REGISTRY },
) {
    val CONTINUATION = register("continuation") { ContinuationArithmetic }
}
