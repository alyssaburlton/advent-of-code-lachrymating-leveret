class Day21 : Solver {
    override val day = 21

    private val input = readStringList("21")
    private val numericMonkeys = input.mapNotNull(::parseNumericMonkey).toMap()
    private val equationMonkeys = input.mapNotNull(::parseEquationMonkey).toMap()
    private val operationToInverse: Map<String, (Long, Long) -> Long> = mapOf(
        "+" to Long::minus,
        "-" to Long::plus,
        "*" to Long::div,
        "/" to Long::times
    )

    override fun partA() = recursivelySimplify(numericMonkeys, equationMonkeys).first.getValue("root")

    override fun partB(): Any {
        val numericMonkeysB = numericMonkeys.minus("humn")
        val replacedRoot = replaceRootOperation(equationMonkeys.getValue("root"))
        val equationMonkeysB = equationMonkeys.plus("root" to replacedRoot)

        val (_, resolvedEquation) = recursivelySimplify(numericMonkeysB, equationMonkeysB)

        val rootEquation = expandOutEquations(resolvedEquation).getValue("root")
        return solveEquation(0L, rootEquation)
    }

    private fun expandOutEquations(equations: Map<String, String>) =
        equations.keys.fold(equations) { equationsSoFar, monkey ->
            val equation = equationsSoFar.getValue(monkey)
            equationsSoFar.mapValues { (_, value) -> value.replace(monkey, "($equation)") }
        }

    private fun recursivelySimplify(
        numericMonkeys: Map<String, Long>,
        equationMonkeys: Map<String, String>,
        numericMonkeysToApply: Map<String, Long> = numericMonkeys
    ): Pair<Map<String, Long>, Map<String, String>> {
        if (numericMonkeysToApply.isEmpty()) {
            return numericMonkeys to equationMonkeys
        }

        val replacementFn = generateAllReplacements(numericMonkeysToApply)

        val newEquationMonkeys = equationMonkeys.mapValues { (_, value) -> replacementFn(value) }
        val newNumericMonkeys = newEquationMonkeys
            .filter { (_, value) -> evaluateExpression(value) != null }
            .mapValues { (_, value) -> evaluateExpression(value)!! }

        return recursivelySimplify(
            numericMonkeys + newNumericMonkeys,
            newEquationMonkeys - newNumericMonkeys.keys,
            newNumericMonkeys
        )
    }

    private fun generateAllReplacements(numericMonkeysToApply: Map<String, Long>): (String) -> String =
        numericMonkeysToApply.entries.fold({ it }) { fn, (monkey, value) ->
            { str -> fn(str).replace(monkey, value.toString()) }
        }

    private fun evaluateExpression(expression: String): Long? {
        val (thingOne, operator, thingTwo) = expression.split(" ")
        val numOne = thingOne.toLongOrNull() ?: return null
        val numTwo = thingTwo.toLongOrNull() ?: return null
        return when (operator) {
            "+" -> numOne + numTwo
            "-" -> numOne - numTwo
            "*" -> numOne * numTwo
            "/" -> numOne / numTwo
            else -> throw Error("Unknown operation: $operator")
        }
    }

    private fun replaceRootOperation(expression: String): String {
        val (thingOne, _, thingTwo) = expression.split(" ")
        return "$thingOne - $thingTwo"
    }

    private fun solveEquation(lhs: Long, rhs: String): Long {
        if (!rhs.contains("(")) {
            val (leftValue, operation, rightValue) = rhs.split(" ")
            return if (leftValue.toLongOrNull() == null) {
                reverseOperationFromTheRight(lhs, operation, rightValue.toLong())
            } else {
                reverseOperationFromTheLeft(lhs, operation, leftValue.toLong())
            }
        }

        return if (rhs.startsWith("(")) {
            val substring = rhs.substringAfterLast(") ")
            val (operation, number) = substring.split(" ")
            val newLhs = reverseOperationFromTheRight(lhs, operation, number.toLong())
            val newRhs = rhs.removePrefix("(").substringBeforeLast(")")
            solveEquation(newLhs, newRhs)
        } else if (rhs.endsWith(")")) {
            val substring = rhs.substringBefore(" (")
            val (number, operation) = substring.split(" ")
            val newLhs = reverseOperationFromTheLeft(lhs, operation, number.toLong())
            val newRhs = rhs.substringAfter("(").removeSuffix(")")
            solveEquation(newLhs, newRhs)
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

    private fun parseEquationMonkey(inputLine: String): Pair<String, String>? {
        val (monkey, rest) = inputLine.split(": ")
        if (rest.toLongOrNull() != null) {
            return null
        }

        return monkey to rest
    }

    private fun parseNumericMonkey(inputLine: String): Pair<String, Long>? {
        val (monkey, rest) = inputLine.split(": ")
        rest.toLongOrNull() ?: return null
        return monkey to rest.toLong()
    }
}