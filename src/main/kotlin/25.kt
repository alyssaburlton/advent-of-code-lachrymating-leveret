import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sign

class Day25 : Solver {
    override val day = 25

    private val input = readStringList("25")
    private val snafuMap: Map<Char, Int> = mapOf(
        '2' to 2,
        '1' to 1,
        '0' to 0,
        '-' to -1,
        '=' to -2
    )

    override fun partA() = input.sumOf(::snafuToDecimal).let(::decimalToSnafu)

    override fun partB() = "Merry Christmas!"

    fun snafuToDecimal(snafu: String) =
        snafu.toCharArray().mapIndexed { ix, char ->
            val power = snafu.length - ix - 1
            snafuMap.getValue(char) * 5.0.pow(power)
        }.sum().toLong()

    fun decimalToSnafu(number: Long): String {
        val map = decimalToSnafuRecursive(-number, emptyMap())
        val maxPower = map.keys.max()

        return (maxPower downTo 0).joinToString("") {
            val count = map[it] ?: 0
            if (count == -2) "=" else if (count == -1) "-" else count.toString()
        }
    }

    private tailrec fun decimalToSnafuRecursive(amountRemaining: Long, powersSoFar: Map<Int, Int>): Map<Int, Int> {
        if (amountRemaining == 0L) {
            return powersSoFar
        }

        val minPowerSoFar = powersSoFar.keys.minOrNull() ?: 100

        val power = findRelevantPowerOfFive(amountRemaining, minPowerSoFar)

        val diff2 = (2 * 5.0.pow(power)) - abs(amountRemaining)
        val diff = 5.0.pow(power) - abs(amountRemaining)
        val amountToUse = if (abs(diff2) < abs(diff)) 2 else 1

        val sign = amountRemaining.sign
        val newPair = power to (-sign) * amountToUse

        return decimalToSnafuRecursive(
            amountRemaining + ((-sign) * amountToUse * 5.0.pow(power)).toLong(),
            powersSoFar + newPair
        )
    }

    private fun findRelevantPowerOfFive(amountRemaining: Long, maxPower: Int = 100) =
        (0 until maxPower).firstOrNull { n ->
            2 * 5.0.pow(n) >= abs(amountRemaining)
        } ?: (maxPower - 1)
}