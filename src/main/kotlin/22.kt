class Day22 : Solver {
    override val day = 22

    private val input = readGroupedList("22")
    private val instructions = parseInstructions(input[1].only())
    private val grid = parseGrid(input[0], ' ')

    override fun partA(): Any {
        println(instructions)
        grid.print()

        val startX = grid.map.filter { entry -> entry.key.y == 0 && entry.value == "." }.minOf { it.key.x }
        val startPos = Point(startX, 0)
        println(startPos)

        println(instructions)
        println()
        var currentPos = startPos
        var currentDirection = Point(1, 0)
        var instructionsToGo = instructions.toMutableList()
        while (instructionsToGo.isNotEmpty()) {
            val instruction = instructionsToGo.removeFirst()
            if (instruction is Int) {
                currentPos = moveInDirection(currentPos, currentDirection, instruction)
            } else if (instruction == 'L') {
                currentDirection = turnLeft(currentDirection)
            } else if (instruction == 'R') {
                currentDirection = turnRight(currentDirection)
            } else if (instruction == "E") {
                println("END")
            } else {
                throw Error("Unexpected: $instruction")
            }

            println(currentPos)
        }

        return (1000 * (currentPos.y + 1)) + (4 * (currentPos.x + 1)) + scoreFacing(currentDirection)
    }

    private fun moveInDirection(startPos: Point, direction: Point, amount: Int): Point {
        var hitWall = false
        var amountMoved = 0
        var pos = startPos
        while (!hitWall && amountMoved < amount) {
            var nextPos = Point(pos.x + direction.x, pos.y + direction.y)
            val value = grid.map[nextPos]
            if (value == null || value == " ") {
                // wrap around
                nextPos = wrapAround(nextPos, direction)
            }

            if (grid.map[nextPos] == "#") {
                hitWall = true
            } else {
                pos = nextPos
                amountMoved++
            }
        }

        return pos
    }

    private fun wrapAround(currentPos: Point, direction: Point): Point {
        return if (direction == Point(1, 0)) {
            val x = grid.map.filter { it.key.y == currentPos.y && it.value != " " }.keys.minOf { it.x }
            Point(x, currentPos.y)
        } else if (direction == Point(-1, 0)) {
            val x = grid.map.filter { it.key.y == currentPos.y && it.value != " " }.keys.maxOf { it.x }
            Point(x, currentPos.y)
        } else if (direction == Point(0, 1)) {
            val y = grid.map.filter { it.key.x == currentPos.x && it.value != " " }.keys.minOf { it.y }
            Point(currentPos.x, y)
        } else if (direction == Point(0, -1)) {
            val y = grid.map.filter { it.key.x == currentPos.x && it.value != " " }.keys.maxOf { it.y }
            Point(currentPos.x, y)
        } else {
            throw Error("What direction: $direction")
        }
    }

    private fun turnLeft(direction: Point) = Point(direction.y, -direction.x)
    private fun turnRight(direction: Point) = Point(-direction.y, direction.x)
    private fun scoreFacing(direction: Point) = when (direction) {
        Point(1, 0) -> 0
        Point(0, 1) -> 1
        Point(-1, 0) -> 2
        Point(0, -1) -> 3
        else -> throw Error("What")
    }

    override fun partB(): Any {
        return ""
    }

    private fun parseInstructions(instructionStr: String): List<Any> {
        val numbers = instructionStr.split("L").flatMap { it.split("R") }.map { it.toInt() }
        val instructions = instructionStr.filter { it == 'L' || it == 'R' }.toCharArray().toList() + "E"
        println(numbers)
        println(instructions)

        return (numbers zip instructions).flatMap { listOf(it.first, it.second) }
    }
}