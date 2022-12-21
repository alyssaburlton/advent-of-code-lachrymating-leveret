class Day21 : Solver {
    override val day = 21

    private val monkeyPairs = readStringList("21").map(::parseMonkey)
    private val operationToInverse: Map<String, (Long, Long) -> Long> = mapOf(
        "+" to Long::minus,
        "-" to Long::plus,
        "*" to Long::div,
        "/" to Long::times
    )

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
        val numericEntries =
            mutableMapOf(*monkeyPairs.filter { it.second is Long }.toTypedArray()) as MutableMap<String, Long>
        numericEntries.remove("humn")

        val stringEntries =
            mutableMapOf(*monkeyPairs.filter { it.second is String }.toTypedArray()) as MutableMap<String, String>
        var newNumericEntries = numericEntries.toMap()

        val rootEntry = stringEntries.remove("root")!!
        val (rootFirst, _, rootSecond) = rootEntry.split(" ")

        while (newNumericEntries.isNotEmpty()) {
            newNumericEntries = iterateMaps(stringEntries, numericEntries, newNumericEntries)
        }

        val rootEntries = listOf(rootFirst to numericEntries[rootFirst], rootSecond to numericEntries[rootSecond])
        val targetValue = rootEntries.firstNotNullOf { it.second }
        val targetVariable = rootEntries.first { it.second == null }.first

        println("Trying to get $targetVariable = $targetValue")

        stringEntries.forEach { (monkey, replacementValue) ->
            stringEntries.forEach { (key, value) ->
                stringEntries[key] = value.replace(monkey, "($replacementValue)")
            }
        }

        println()
        println()
        return solveEquation(targetValue, stringEntries[targetVariable]!!)
    }

    private fun solveEquation(lhs: Long, rhs: String): Long {
        if (!rhs.contains("(")) {
            val (leftValue, operation, rightValue) = rhs.split(" ")
            if (leftValue.toLongOrNull() == null) {
                return reverseOperationFromTheRight(lhs, operation, rightValue.toLong())
            } else {
                return reverseOperationFromTheLeft(lhs, operation, leftValue.toLong())
            }
        }

        println("$lhs = $rhs")

        if (rhs.startsWith("(")) {
            val substring = rhs.substringAfterLast(") ")
            val (operation, number) = substring.split(" ")
            val newLhs = reverseOperationFromTheRight(lhs, operation, number.toLong())
            val newRhs = rhs.removePrefix("(").substringBeforeLast(")")
            return solveEquation(newLhs, newRhs)
        } else if (rhs.endsWith(")")) {
            val substring = rhs.substringBefore(" (")
            val (number, operation) = substring.split(" ")
            val newLhs = reverseOperationFromTheLeft(lhs, operation, number.toLong())
            val newRhs = rhs.substringAfter("(").removeSuffix(")")
            return solveEquation(newLhs, newRhs)
        } else {
            throw Error("Argh")
        }
    }

    private fun reverseOperationFromTheLeft(currentValue: Long, operation: String, operand: Long): Long {
        if (operation == "/") {
            throw Error("Cannot deal with $operation on LHS")
        }

        val inverseOp = operationToInverse.getValue(operation)
        return if (operation == "-") inverseOp(-1 * currentValue, operand) else inverseOp(currentValue, operand)
    }

    private fun reverseOperationFromTheRight(currentValue: Long, operation: String, operand: Long): Long {
        val inverseOp = operationToInverse.getValue(operation)
        return inverseOp(currentValue, operand)
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