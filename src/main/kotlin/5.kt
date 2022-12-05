class Day5 : Solver {
    override val day = 5

    private val input = readGroupedList("5")

    override fun partA(): Int {
        val s = parseStacks(input[0])

        print(input[1])
        val result = input[1].fold(s) { stacks, instruction ->
            processInstruction(stacks, instruction)
        }

        println(result)

        println(result.values.map { it.last() }.joinToString(""))

        return - 1
    }

    private fun processInstruction(stacks: Map<Int, List<Char>>, instruction: String): Map<Int, List<Char>> {
        val parts = instruction.split(" ")
        val amountToMove = parts[1].toInt()
        val fromColIx = parts[3].toInt()
        val toColIx = parts[5].toInt()
        val fromCol = stacks[fromColIx]!!
        val toCol = stacks[toColIx]!!

        val thingsToMove = fromCol.reversed().take(amountToMove).reversed()

        val newToCol = toCol + thingsToMove
        val newFromCol = fromCol.dropLast(thingsToMove.size)

        val newMap = stacks.toMutableMap()
        newMap[fromColIx] = newFromCol
        newMap[toColIx] = newToCol
        return newMap.toMap()
    }

    private fun parseStacks(rawGrid: List<String>): Map<Int, List<Char>> {
        val replaced: List<String> = rawGrid.map { it.replace("    ", "-").replace("]", "").replace("[", "").replace(" ", "")}

        println(replaced)

        val columnCount = replaced[0].length
        val rowCount = replaced.size - 1

        val columnMap = (0 until columnCount).associate { col ->
            val list = (0 until rowCount).map { row -> replaced[row][col] }.filter { it != '-' }.reversed()
            (col + 1) to list
        }

        println(columnMap)

        return columnMap
    }

    override fun partB(): Int {

        return -1
    }
}