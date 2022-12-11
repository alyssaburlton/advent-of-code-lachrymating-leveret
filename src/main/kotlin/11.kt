data class Monkey(val items: List<Long>, val itemsInspected: Long, val operation: (Long) -> Long, val test: MonkeyTest)
data class MonkeyTest(val divisor: Int, val monkeyIfTrue: Int, val monkeyIfFalse: Int)
data class ThrownItem(val item: Long, val newMonkey: Int)

class Day11 : Solver {
    override val day = 11

    private val input: List<List<String>> = readGroupedList("11")

    override fun partA() = calculateMonkeyBusiness(20, true)

    override fun partB() = calculateMonkeyBusiness(10000, false)

    private fun calculateMonkeyBusiness(rounds: Int, divideByThree: Boolean): Long {
        val initialMonkeys =
            parseMonkeys().let { monkeys -> monkeys.map { updateMonkeyOperation(it, monkeys, divideByThree) } }

        return (1..rounds)
            .fold(initialMonkeys) { monkeysForRound, _ -> doMonkeyRound(monkeysForRound) }
            .map { it.itemsInspected }
            .sortedDescending()
            .take(2)
            .product()
    }

    private fun doMonkeyRound(monkeysForRound: List<Monkey>) =
        monkeysForRound.indices.fold(monkeysForRound, ::doMonkeyTurn)

    private fun doMonkeyTurn(currentMonkeys: List<Monkey>, monkeyIx: Int): List<Monkey> {
        val monkey = currentMonkeys[monkeyIx]

        val thrownItems = monkey.items.map { item ->
            val newValue = monkey.operation(item)
            ThrownItem(newValue, monkey.calculateThrow(newValue))
        }

        return currentMonkeys.mapIndexed { newMonkeyIx, newMonkey ->
            when (newMonkeyIx) {
                monkeyIx -> monkey.copy(
                    items = emptyList(),
                    itemsInspected = monkey.itemsInspected + monkey.items.size
                )

                else -> newMonkey.copy(
                    items = newMonkey.items + thrownItems.filter { it.newMonkey == newMonkeyIx }.map { it.item }
                )
            }
        }
    }

    private fun Monkey.calculateThrow(item: Long) =
        if (item % test.divisor == 0L) test.monkeyIfTrue else test.monkeyIfFalse

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

    private fun updateMonkeyOperation(monkey: Monkey, monkeys: List<Monkey>, divideByThree: Boolean): Monkey {
        val modBy = monkeys.map { it.test.divisor }.distinct().product()
        return monkey.copy(
            operation = { if (divideByThree) monkey.operation(it) / 3 else monkey.operation(it) % modBy }
        )
    }
}