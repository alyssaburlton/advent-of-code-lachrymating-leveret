class Day21 : Solver {
    override val day = 21

    private val monkeyPairs = readStringList("21").map(::parseMonkey)

    override fun partA(): Any {
        val numericEntries =
            mutableMapOf(*monkeyPairs.filter { it.second is Long }.toTypedArray()) as MutableMap<String, Long>
        val stringEntries =
            mutableMapOf(*monkeyPairs.filter { it.second is String }.toTypedArray()) as MutableMap<String, String>
        var newNumericEntries = numericEntries.toMap()

        while (stringEntries.isNotEmpty()) {
            newNumericEntries = iterateMaps(stringEntries, numericEntries, newNumericEntries)
        }

        return numericEntries["root"]!!
    }

    override fun partB(): Any {
        return ""
    }

    private fun iterateMaps(
        stringMap: MutableMap<String, String>,
        numericMap: MutableMap<String, Long>,
        newNumerics: Map<String, Long>
    ): Map<String, Long> {
        stringMap.keys.forEach { key ->
            newNumerics.forEach { (monkey, number) ->
                val newValue = stringMap.getValue(key).replace(monkey, number.toString())
                stringMap[key] = newValue
            }
        }

        val nowNumeric: Map<String, Long> = stringMap
            .filter {
                val (first, _, second) = it.value.split(" ")
                first.toLongOrNull() != null && second.toLongOrNull() != null
            }
            .mapValues { (_, value) -> evaluateExpression(value) }


        nowNumeric.keys.forEach {
            stringMap.remove(it)
        }

        numericMap.putAll(nowNumeric)
        return nowNumeric
    }

    private fun evaluateExpression(expression: String): Long {
        return if (expression.contains(" - ")) {
            val (thingOne, thingTwo) = expression.split(" - ")
            thingOne.toLong() - thingTwo.toLong()
        } else if (expression.contains(" + ")) {
            val (thingOne, thingTwo) = expression.split(" + ")
            thingOne.toLong() + thingTwo.toLong()
        } else if (expression.contains(" * ")) {
            val (thingOne, thingTwo) = expression.split(" * ")
            thingOne.toLong() * thingTwo.toLong()
        } else {
            val (thingOne, thingTwo) = expression.split(" / ")
            thingOne.toLong() / thingTwo.toLong()
        }
    }

    private fun parseMonkey(inputLine: String): Pair<String, Any> {
        val (monkey, rest) = inputLine.split(": ")
        if (rest.toLongOrNull() != null) {
            return monkey to rest.toLong()
        } else {
            return monkey to rest
        }
    }
}