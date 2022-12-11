data class Monkey(val items: List<Long>, val operation: (Long) -> Long, val test: MonkeyTest)
data class MonkeyTest(val divisor: Int, val monkeyIfTrue: Int, val monkeyIfFalse: Int)

class Day11 : Solver {
    override val day = 11

    private val input: List<List<String>> = readGroupedList("11e")

    override fun partA(): Any {
        var currentMonkeys = parseMonkeys()
        val monkeyIndexes = currentMonkeys.keys.sorted()
        val itemsInspected = mutableMapOf<Int, Int>()

        (1..20).forEach { _ ->
            monkeyIndexes.forEach { monkeyIx ->
                val newItemLocations = mutableMapOf<Int, List<Long>>()
                println("Monkey $monkeyIx:")
                val monkey = currentMonkeys.getValue(monkeyIx)
                val inspecting = itemsInspected.getOrDefault(monkeyIx, 0) + monkey.items.size
                itemsInspected[monkeyIx] = inspecting

                monkey.items.forEach { item ->
                    println("   Monkey inspects an item with a worry level of $item")
                    val intermediateValue = monkey.operation(item)
                    println("       After worry level: $intermediateValue")
                    val newValue = intermediateValue / 3
                    println("       After boredom: $newValue")
                    val newMonkey =
                        if (newValue % monkey.test.divisor == 0L) monkey.test.monkeyIfTrue else monkey.test.monkeyIfFalse

                    println("       Thrown to monkey $newMonkey")

                    val items = newItemLocations.getOrDefault(newMonkey, emptyList())
                    newItemLocations[newMonkey] = items + newValue
                }

                println(newItemLocations)
                currentMonkeys = currentMonkeys.mapValues { (newMonkeyIx, newMonkey) ->
                    when (newMonkeyIx) {
                        monkeyIx -> newMonkey.copy(items = emptyList())
                        else -> newMonkey.copy(
                            items = newMonkey.items + newItemLocations.getOrDefault(
                                newMonkeyIx,
                                emptyList()
                            )
                        )
                    }
                }
            }

            println(currentMonkeys.mapValues { it.value.items })
        }

        println(itemsInspected)
        val result = itemsInspected.values.sortedDescending()
        println(result)
        return result[0] * result[1]
    }

    private fun parseMonkeys(): Map<Int, Monkey> {
        return input
            .groupBy { it[0].split(" ")[1].replace(":", "").toInt() }
            .mapValues { (_, value) -> parseMonkey(value.only()) }
    }

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

        return Monkey(startingItems, operation, MonkeyTest(testDivisor, monkeyIfTrue, monkeyIfFalse))
    }

    override fun partB(): Any {
        var currentMonkeys = parseMonkeys()
        val monkeyIndexes = currentMonkeys.keys.sorted()
        val itemsInspected = mutableMapOf<Int, Long>()

        val uniqueDivisors = currentMonkeys.values.map { it.test.divisor }.distinct()
        val modBy = uniqueDivisors.distinct().product()

        (1..10000).forEach { _ ->
            monkeyIndexes.forEach { monkeyIx ->
                val newItemLocations = mutableMapOf<Int, List<Long>>()
                // println("Monkey $monkeyIx:")
                val monkey = currentMonkeys.getValue(monkeyIx)
                val inspecting = itemsInspected.getOrDefault(monkeyIx, 0) + monkey.items.size
                itemsInspected[monkeyIx] = inspecting

                monkey.items.forEach { item ->
                    // println("   Monkey inspects an item with a worry level of $item")
                    val newValue = monkey.operation(item) % modBy
                    // println("       After boredom: $newValue")
                    val newMonkey =
                        if (newValue % monkey.test.divisor == 0L) monkey.test.monkeyIfTrue else monkey.test.monkeyIfFalse

                    // println("       Thrown to monkey $newMonkey")

                    val items = newItemLocations.getOrDefault(newMonkey, emptyList())
                    newItemLocations[newMonkey] = items + newValue
                }

                // println(newItemLocations)
                currentMonkeys = currentMonkeys.mapValues { (newMonkeyIx, newMonkey) ->
                    when (newMonkeyIx) {
                        monkeyIx -> newMonkey.copy(items = emptyList())
                        else -> newMonkey.copy(
                            items = newMonkey.items + newItemLocations.getOrDefault(
                                newMonkeyIx,
                                emptyList()
                            )
                        )
                    }
                }
            }

            // println(currentMonkeys.mapValues { it.value.items })
        }

        println(itemsInspected)
        val result = itemsInspected.values.sortedDescending()
        println(result)
        return result[0] * result[1]
    }
}