typealias Direction = Point

class Day22 : Solver {
    override val day = 22

    private val input = readGroupedList("22")
    private val instructions = parseInstructions(input[1].only())
    private val pointMap = parsePointMap(input[0])
    private val edgeRulesB = buildEdgeRules()

    override fun partA() = tracePath(::wrapAroundA)

    override fun partB() = tracePath(::wrapAroundB)

    private fun tracePath(wrapFunction: (Point, Direction) -> Pair<Point, Direction>): Int {
        val startX = pointMap.filter { entry -> entry.key.y == 0 && entry.value == "." }.minOf { it.key.x }
        val startPos = Point(startX, 0)

        var currentPos = startPos
        var currentDirection = Point(1, 0)
        val instructionsToGo = instructions.toMutableList()
        while (instructionsToGo.isNotEmpty()) {
            val instruction = instructionsToGo.removeFirst()
            if (instruction is Int) {
                val (newPos, newDir) = moveInDirectionB(currentPos, currentDirection, instruction, wrapFunction)
                currentPos = newPos
                currentDirection = newDir
            } else if (instruction == 'L') {
                currentDirection = turnLeft(currentDirection)
            } else if (instruction == 'R') {
                currentDirection = turnRight(currentDirection)
            } else if (instruction == "E") {
                // println("END")
            } else {
                throw Error("Unexpected: $instruction")
            }

            // println("$currentPos, facing $currentDirection")
        }

        return (1000 * (currentPos.y + 1)) + (4 * (currentPos.x + 1)) + scoreFacing(currentDirection)
    }

    private fun moveInDirectionB(
        startPos: Point,
        startDirection: Direction,
        amount: Int,
        wrapFunction: (Point, Direction) -> Pair<Point, Direction>
    ): Pair<Point, Direction> {
        var hitWall = false
        var amountMoved = 0
        var pos = startPos
        var direction = startDirection

        while (!hitWall && amountMoved < amount) {
            var nextPos = Point(pos.x + direction.x, pos.y + direction.y)
            var currentDirection = direction
            val value = pointMap[nextPos]
            if (value == null || value == " ") {
                // wrap around
                val wrapResult = wrapFunction(pos, direction)
                nextPos = wrapResult.first
                currentDirection = wrapResult.second
            }

            if (pointMap[nextPos] == "#") {
                hitWall = true
            } else {
                pos = nextPos
                direction = currentDirection
                amountMoved++
            }
        }

        return pos to direction
    }

    private fun wrapAroundA(currentPos: Point, direction: Direction): Pair<Point, Direction> {
        val pt = if (direction == Point(1, 0)) {
            val x = pointMap.filter { it.key.y == currentPos.y && it.value != " " }.keys.minOf { it.x }
            Point(x, currentPos.y)
        } else if (direction == Point(-1, 0)) {
            val x = pointMap.filter { it.key.y == currentPos.y && it.value != " " }.keys.maxOf { it.x }
            Point(x, currentPos.y)
        } else if (direction == Point(0, 1)) {
            val y = pointMap.filter { it.key.x == currentPos.x && it.value != " " }.keys.minOf { it.y }
            Point(currentPos.x, y)
        } else if (direction == Point(0, -1)) {
            val y = pointMap.filter { it.key.x == currentPos.x && it.value != " " }.keys.maxOf { it.y }
            Point(currentPos.x, y)
        } else {
            throw Error("What direction: $direction")
        }

        return pt to direction
    }

    private data class WrapRule(
        val ptPairs: List<Pair<Point, Point>>,
        val directions: Pair<Direction, Direction>,
        val directionTransform: (Direction) -> Direction
    )

    fun wrapAroundB(currentPos: Point, direction: Point): Pair<Point, Direction> {
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
            pointMap.filter { entry -> entry.key.y == 149 && entry.key.x in (50..99) && entry.value != " " }.keys.sortedBy { it.x }
        check(topEdgeKeys.size == 50)

        val rightEdgeKeys =
            pointMap.filter { entry -> entry.key.x == 49 && entry.key.y in (150..199) && entry.value != " " }.keys.sortedBy { it.y }
        check(rightEdgeKeys.size == 50)

        val pairs = topEdgeKeys zip rightEdgeKeys

        return WrapRule(pairs, Direction(0, 1) to Direction(1, 0)) { Direction(-it.y, -it.x) }
    }

    private fun buildGreenSquiggleRule(): WrapRule {
        val leftEdgeKeys =
            pointMap.filter { entry -> entry.key.x == 50 && entry.key.y in (50..99) && entry.value != " " }.keys.sortedBy { it.y }
        check(leftEdgeKeys.size == 50)

        val topEdgeKeys =
            pointMap.filter { entry -> entry.key.y == 100 && entry.key.x in (0..49) && entry.value != " " }.keys.sortedBy { it.x }
        check(topEdgeKeys.size == 50)

        val pairs = leftEdgeKeys zip topEdgeKeys
        // println(pairs)

        return WrapRule(pairs, Direction(-1, 0) to Direction(0, -1)) { Direction(-it.y, -it.x) }
    }

    private fun buildBlueLineRule(): WrapRule {
        val topLeftEdgeKeys =
            pointMap.filter { entry -> entry.key.x == 50 && entry.key.y in (0..49) && entry.value != " " }.keys.sortedBy { it.y }
        check(topLeftEdgeKeys.size == 50)

        val bottomLeftEdgeKeys =
            pointMap.filter { entry -> entry.key.x == 0 && entry.key.y in (100..149) && entry.value != " " }.keys.sortedBy { -it.y }
        check(bottomLeftEdgeKeys.size == 50)

        val pairs = topLeftEdgeKeys zip bottomLeftEdgeKeys
        // println(pairs)

        return WrapRule(pairs, Direction(-1, 0) to Direction(-1, 0)) { Direction(-it.x, it.y) }
    }

    private fun buildRedLineRule(): WrapRule {
        val topEdgeKeys =
            pointMap.filter { entry -> entry.key.y == 0 && entry.key.x in (100..149) && entry.value != " " }.keys.sortedBy { it.x }
        check(topEdgeKeys.size == 50)

        val bottomEdgeKeys =
            pointMap.filter { entry -> entry.key.y == 199 && entry.value != " " }.keys.sortedBy { it.x }

        check(bottomEdgeKeys.size == 50)

        val pairs = topEdgeKeys zip bottomEdgeKeys
        // println(pairs)

        return WrapRule(pairs, Direction(0, -1) to Direction(0, 1)) { it }
    }

    private fun buildGreenLineRule(): WrapRule {
        val topEdgeKeys =
            pointMap.filter { entry -> entry.key.y == 0 && entry.key.x in (50..99) && entry.value != " " }.keys.sortedBy { it.x }
        check(topEdgeKeys.size == 50)

        val leftEdgeKeys =
            pointMap.filter { entry -> entry.key.x == 0 && entry.key.y >= 150 && entry.value != " " }.keys.sortedBy { it.y }
        check(leftEdgeKeys.size == 50)

        val pairs = topEdgeKeys zip leftEdgeKeys
        // println(pairs)

        return WrapRule(pairs, Direction(0, -1) to Direction(-1, 0)) { Direction(-it.y, -it.x) }
    }

    private fun buildRedSquiggleRule(): WrapRule {
        val bottomEdgeKeys =
            pointMap.filter { entry -> entry.key.x >= 100 && entry.key.y == 49 && entry.value != " " }.keys.sortedBy { it.x }
        check(bottomEdgeKeys.size == 50)

        val rightEdgeKeys =
            pointMap.filter { entry -> entry.key.x == 99 && entry.key.y in (50..99) && entry.value != " " }.keys.sortedBy { it.x }
        check(rightEdgeKeys.size == 50)

        val pairs = bottomEdgeKeys zip rightEdgeKeys

        // println(pairs)
        return WrapRule(pairs, Direction(0, 1) to Direction(1, 0)) { Direction(-it.y, -it.x) }
    }

    private fun buildPencilRule(): WrapRule {
        val topRightEdgeKeys =
            pointMap.filter { entry -> entry.key.x == 149 && entry.value != " " }.keys.sortedBy { it.y }

        val joinedEdgeKeys =
            pointMap.filter { entry -> entry.key.x == 99 && entry.key.y > 99 && entry.value != " " }.keys.sortedBy { -it.y }

        check(topRightEdgeKeys.size == joinedEdgeKeys.size)
        check(topRightEdgeKeys.size == 50)

        val pts = topRightEdgeKeys zip joinedEdgeKeys
        // println(pts)
        return WrapRule(pts, Direction(1, 0) to Direction(1, 0)) { Direction(-it.x, it.y) }
    }


    private fun turnLeft(direction: Direction) = Direction(direction.y, -direction.x)
    private fun turnRight(direction: Direction) = Direction(-direction.y, direction.x)
    private fun scoreFacing(direction: Direction) = when (direction) {
        Direction(1, 0) -> 0
        Direction(0, 1) -> 1
        Direction(-1, 0) -> 2
        Direction(0, -1) -> 3
        else -> throw Error("What")
    }


    private fun parseInstructions(instructionStr: String): List<Any> {
        val numbers = instructionStr.split("L").flatMap { it.split("R") }.map { it.toInt() }
        val instructions = instructionStr.filter { it == 'L' || it == 'R' }.toCharArray().toList() + "E"

        return (numbers zip instructions).flatMap { listOf(it.first, it.second) }
    }
}