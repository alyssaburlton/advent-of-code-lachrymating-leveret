data class Monkey(val items: List<Long>, val itemsInspected: Long, val operation: (Long) -> Long, val test: MonkeyTest)
data class MonkeyTest(val divisor: Int, val monkeyIfTrue: Int, val monkeyIfFalse: Int)
data class ThrownItem(val item: Long, val newMonkey: Int)

class Day11 : Solver {
    override val day = 11

    private val input: List<List<String>> = readGroupedList("11")

    override fun partA() = calculateMonkeyBusiness(20, true)

    override fun partB() = calculateMonkeyBusiness(10000, false)

    private fun parseMonkeys() = input.map(::parseMonkey)

    private fun parseMonkey(input: List<String>): Monkey {
        val startingItemStr = input[1].split(": ")[1]
        val startingItems = startingItemStr.split(", ").map(String::toLong)
        val operationParts = input[2].split(" = ")[1].split(" ")
        val operationAmount = operationParts[2].toLongOrNull()

        val operation: (Long) -> Long = when (operationParts[1]) {
            "+" -> ({ it + (operationAmount ?: it) })
            else -> ({ it * (operationAmount ?: it) })
        }

        val testDivisor = input[3].split(" divisible by ")[1].toInt()
        val monkeyIfTrue = input[4].split("throw to monkey ")[1].toInt()
        val monkeyIfFalse = input[5].split("throw to monkey ")[1].toInt()

        return Monkey(startingItems, 0, operation, MonkeyTest(testDivisor, monkeyIfTrue, monkeyIfFalse))
    }

    private fun calculateMonkeyBusiness(rounds: Int, divideByThree: Boolean): Long {
        val initialMonkeys = parseMonkeys()
        val monkeyIndexes = initialMonkeys.indices
        val modBy = initialMonkeys.map { it.test.divisor }.distinct().product()

        val resultingMonkeys = (1..rounds).fold(parseMonkeys()) { monkeysForRound, _ ->
            monkeyIndexes.fold(monkeysForRound) { currentMonkeys, monkeyIx ->
                val monkey = currentMonkeys[monkeyIx]

                val thrownItems = monkey.items.map { item ->
                    val newValue = if (divideByThree) monkey.operation(item) / 3 else monkey.operation(item) % modBy

                    val newMonkey =
                        if (newValue % monkey.test.divisor == 0L) monkey.test.monkeyIfTrue else monkey.test.monkeyIfFalse

                    ThrownItem(newValue, newMonkey)
                }

                currentMonkeys.mapIndexed { newMonkeyIx, newMonkey ->
                    when (newMonkeyIx) {
                        monkeyIx -> newMonkey.copy(
                            items = emptyList(),
                            itemsInspected = newMonkey.itemsInspected + monkey.items.size
                        )

                        else -> newMonkey.copy(
                            items = newMonkey.items + thrownItems.filter { it.newMonkey == newMonkeyIx }.map { it.item }
                        )
                    }
                }
            }
        }

        val result = resultingMonkeys.map { it.itemsInspected }.sortedDescending()
        return result[0] * result[1]
    }
}