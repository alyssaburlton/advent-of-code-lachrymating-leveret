import kotlin.math.pow

class Day25(mode: SolverMode) : Solver(25, mode) {
    private val input = readStringList(filename)

    override fun partA() = input.sumOf(::snafuToDecimal).let(::decimalToSnafu)

    override fun partB() = "Merry Christmas!"

    companion object {
        private val snafuMap: Map<Char, Int> = mapOf(
            '2' to 2,
            '1' to 1,
            '0' to 0,
            '-' to -1,
            '=' to -2
        )
        private val inverseSnafuMap = snafuMap.map { it.value to it.key }.toMap()

        fun snafuToDecimal(snafu: String) =
            snafu.toCharArray().mapIndexed { ix, char ->
                val power = snafu.length - ix - 1
                snafuMap.getValue(char) * 5.0.pow(power)
            }.sum().toLong()

        fun decimalToSnafu(number: Long) = decimalToSnafuRecursive(number, "")

        private tailrec fun decimalToSnafuRecursive(amountRemaining: Long, stringSoFar: String): String {
            if (amountRemaining == 0L) {
                return stringSoFar
            }

            val positiveMod = amountRemaining.mod(5)
            val mod = if (positiveMod > 2) positiveMod - 5 else positiveMod
            val newCharacter = inverseSnafuMap.getValue(mod)

            val newRemainder = (amountRemaining - mod) / 5
            return decimalToSnafuRecursive(newRemainder, newCharacter + stringSoFar)
        }
    }
}