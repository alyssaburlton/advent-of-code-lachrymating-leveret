class Day22 : Solver {
    override val day = 22

    private val input = readGroupedList("22")
    private val instructions = parseInstructions(input[1].only())
    private val grid = parseGrid(input[0], ' ')
    private val edgeRulesB = buildEdgeRules()

    override fun partA() = ""

    private fun doPartA(): Any {
        println(instructions)
        grid.print()

        val startX = grid.map.filter { entry -> entry.key.y == 0 && entry.value == "." }.minOf { it.key.x }
        val startPos = Point(startX, 0)
        println(startPos)

        println(instructions)
        println()
        var currentPos = startPos
        var currentDirection = Point(1, 0)
        val instructionsToGo = instructions.toMutableList()
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

    override fun partB(): Any {
        println(instructions)
        grid.print()

        val startX = grid.map.filter { entry -> entry.key.y == 0 && entry.value == "." }.minOf { it.key.x }
        val startPos = Point(startX, 0)
        println(startPos)

        println(instructions)
        println()
        var currentPos = startPos
        var currentDirection = Point(1, 0)
        val instructionsToGo = instructions.toMutableList()
        while (instructionsToGo.isNotEmpty()) {
            val instruction = instructionsToGo.removeFirst()
            if (instruction is Int) {
                val (newPos, newDir) = moveInDirectionB(currentPos, currentDirection, instruction)
                currentPos = newPos
                currentDirection = newDir
            } else if (instruction == 'L') {
                currentDirection = turnLeft(currentDirection)
            } else if (instruction == 'R') {
                currentDirection = turnRight(currentDirection)
            } else if (instruction == "E") {
                println("END")
            } else {
                throw Error("Unexpected: $instruction")
            }

            println("$currentPos, facing $currentDirection")
        }

        return (1000 * (currentPos.y + 1)) + (4 * (currentPos.x + 1)) + scoreFacing(currentDirection)
    }

    private fun moveInDirectionB(startPos: Point, direction: Point, amount: Int): Pair<Point, Point> {
        var hitWall = false
        var amountMoved = 0
        var pos = startPos
        var direction = direction

        while (!hitWall && amountMoved < amount) {
            var nextPos = Point(pos.x + direction.x, pos.y + direction.y)
            var currentDirection = direction
            val value = grid.map[nextPos]
            if (value == null || value == " ") {
                // wrap around
                val wrapResult = wrapAroundB(pos, direction)
                nextPos = wrapResult.first
                currentDirection = wrapResult.second
            }

            if (grid.map[nextPos] == "#") {
                hitWall = true
            } else {
                pos = nextPos
                direction = currentDirection
                amountMoved++
            }
        }

        return pos to direction
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

    private data class WrapRule(
        val ptPairs: List<Pair<Point, Point>>,
        val directions: Pair<Point, Point>,
        val directionTransform: (Point) -> Point
    )

    fun wrapAroundB(currentPos: Point, direction: Point): Pair<Point, Point> {
        val rules = edgeRulesB.filter { rule ->
            (rule.directions.first == direction && rule.ptPairs.map { it.first }.contains(currentPos))
                    || (rule.directions.second == direction && rule.ptPairs.map { it.second }.contains(currentPos))
        }

        if (rules.size != 1) {
            println("UNEXPECTED wrap outcome for $currentPos facing $direction")
            println(rules)
            throw Error("Argh")
        }

        val rule = rules.only()
        val pair = rule.ptPairs.first { it.first == currentPos || it.second == currentPos }
        if (pair.first == currentPos) {
            return pair.second to rule.directionTransform(direction)
        } else {
            return pair.first to rule.directionTransform(direction)
        }
    }

    private fun buildEdgeRules(): List<WrapRule> {
        return listOf(
            buildPencilRule(),
            buildRedSquiggleRule(),
            buildGreenLineRule(),
            buildRedLineRule(),
            buildBlueLineRule(),
            buildGreenSquiggleRule(),
            buildBlueSquiggleRule()
        )

    }

    private fun buildBlueSquiggleRule(): WrapRule {
        val topEdgeKeys =
            grid.map.filter { entry -> entry.key.y == 149 && entry.key.x in (50..99) && entry.value != " " }.keys.sortedBy { it.x }
        check(topEdgeKeys.size == 50)

        val rightEdgeKeys =
            grid.map.filter { entry -> entry.key.x == 49 && entry.key.y in (150..199) && entry.value != " " }.keys.sortedBy { it.y }
        check(rightEdgeKeys.size == 50)

        val pairs = topEdgeKeys zip rightEdgeKeys

        return WrapRule(pairs, Point(0, 1) to Point(1, 0)) { Point(-it.y, -it.x) }
    }

    private fun buildGreenSquiggleRule(): WrapRule {
        val leftEdgeKeys =
            grid.map.filter { entry -> entry.key.x == 50 && entry.key.y in (50..99) && entry.value != " " }.keys.sortedBy { it.y }
        check(leftEdgeKeys.size == 50)

        val topEdgeKeys =
            grid.map.filter { entry -> entry.key.y == 100 && entry.key.x in (0..49) && entry.value != " " }.keys.sortedBy { it.x }
        check(topEdgeKeys.size == 50)

        val pairs = leftEdgeKeys zip topEdgeKeys
        // println(pairs)

        return WrapRule(pairs, Point(-1, 0) to Point(0, -1)) { Point(-it.y, -it.x) }
    }

    private fun buildBlueLineRule(): WrapRule {
        val topLeftEdgeKeys =
            grid.map.filter { entry -> entry.key.x == 50 && entry.key.y in (0..49) && entry.value != " " }.keys.sortedBy { it.y }
        check(topLeftEdgeKeys.size == 50)

        val bottomLeftEdgeKeys =
            grid.map.filter { entry -> entry.key.x == 0 && entry.key.y in (100..149) && entry.value != " " }.keys.sortedBy { -it.y }
        check(bottomLeftEdgeKeys.size == 50)

        val pairs = topLeftEdgeKeys zip bottomLeftEdgeKeys
        // println(pairs)

        return WrapRule(pairs, Point(-1, 0) to Point(-1, 0)) { Point(-it.x, it.y) }
    }

    private fun buildRedLineRule(): WrapRule {
        val topEdgeKeys =
            grid.map.filter { entry -> entry.key.y == 0 && entry.key.x in (100..149) && entry.value != " " }.keys.sortedBy { it.x }
        check(topEdgeKeys.size == 50)

        val bottomEdgeKeys =
            grid.map.filter { entry -> entry.key.y == grid.yMax && entry.value != " " }.keys.sortedBy { it.x }

        check(bottomEdgeKeys.size == 50)

        val pairs = topEdgeKeys zip bottomEdgeKeys
        // println(pairs)

        return WrapRule(pairs, Point(0, -1) to Point(0, 1)) { it }
    }

    private fun buildGreenLineRule(): WrapRule {
        val topEdgeKeys =
            grid.map.filter { entry -> entry.key.y == 0 && entry.key.x in (50..99) && entry.value != " " }.keys.sortedBy { it.x }
        check(topEdgeKeys.size == 50)

        val leftEdgeKeys =
            grid.map.filter { entry -> entry.key.x == 0 && entry.key.y >= 150 && entry.value != " " }.keys.sortedBy { it.y }
        check(leftEdgeKeys.size == 50)

        val pairs = topEdgeKeys zip leftEdgeKeys
        // println(pairs)

        return WrapRule(pairs, Point(0, -1) to Point(-1, 0)) { Point(-it.y, -it.x) }
    }

    private fun buildRedSquiggleRule(): WrapRule {
        val bottomEdgeKeys =
            grid.map.filter { entry -> entry.key.x >= 100 && entry.key.y == 49 && entry.value != " " }.keys.sortedBy { it.x }
        check(bottomEdgeKeys.size == 50)

        val rightEdgeKeys =
            grid.map.filter { entry -> entry.key.x == 99 && entry.key.y in (50..99) && entry.value != " " }.keys.sortedBy { it.x }
        check(rightEdgeKeys.size == 50)

        val pairs = bottomEdgeKeys zip rightEdgeKeys

        // println(pairs)
        return WrapRule(pairs, Point(0, 1) to Point(1, 0)) { Point(-it.y, -it.x) }
    }

    private fun buildPencilRule(): WrapRule {
        val topRightEdgeKeys =
            grid.map.filter { entry -> entry.key.x == grid.xMax && entry.value != " " }.keys.sortedBy { it.y }

        val joinedEdgeKeys =
            grid.map.filter { entry -> entry.key.x == 99 && entry.key.y > 99 && entry.value != " " }.keys.sortedBy { -it.y }

        check(topRightEdgeKeys.size == joinedEdgeKeys.size)
        check(topRightEdgeKeys.size == 50)

        val pts = topRightEdgeKeys zip joinedEdgeKeys
        // println(pts)
        return WrapRule(pts, Point(1, 0) to Point(1, 0)) { Point(-it.x, it.y) }
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


    private fun parseInstructions(instructionStr: String): List<Any> {
        val numbers = instructionStr.split("L").flatMap { it.split("R") }.map { it.toInt() }
        val instructions = instructionStr.filter { it == 'L' || it == 'R' }.toCharArray().toList() + "E"
        // println(numbers)
        // println(instructions)

        return (numbers zip instructions).flatMap { listOf(it.first, it.second) }
    }
}