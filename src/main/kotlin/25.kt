import kotlin.math.pow

class Day25 : Solver {
    override val day = 25

    private val input = readStringList("25").map(::snafuToDecimal)
    private val snafuMap: Map<String, Int> = mapOf(
        "2" to 2,
        "1" to 1,
        "0" to 0,
        "-" to -1,
        "=" to -2
    )

    override fun partA(): Any {
        println(snafuToDecimal("12-0=01----22-0-1-10"))
        return input.sum()
    }

    override fun partB(): Any {
        return ""
    }

    private fun snafuToDecimal(snafu: String): Long {
        val length = snafu.length
        val characters = snafu.toCharArray().toList()

        val snafuMap: Map<String, Int> = mapOf(
            "2" to 2,
            "1" to 1,
            "0" to 0,
            "-" to -1,
            "=" to -2
        )

        // println(snafuMap)

        return characters.mapIndexed { ix, char ->
            val power = length - ix - 1
            // println(char)
            snafuMap.getValue(char.toString()).toDouble() * 5.0.pow(power)
        }.sum().toLong()
    }
}