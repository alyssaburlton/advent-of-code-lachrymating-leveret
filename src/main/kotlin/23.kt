class Day23 : Solver {
    override val day = 23

    private val input = readStringGrid("23")
    private val startingElves = input.map.filter { it.value == "#" }.keys.groupBy { it.y }

    private var proposalTime: Long = 0

    override fun partA() =
        (0..9).fold(startingElves, ::iterateElves).let(::countEmptySpaces)

    private fun countEmptySpaces(elves: Map<Int, List<Point>>): Int {
        val points = elves.values.flatten()
        val xBounds = (points.maxOf { it.x } - points.minOf { it.x }) + 1
        val yBounds = (points.maxOf { it.y } - points.minOf { it.y }) + 1
        return (xBounds * yBounds) - points.size
    }

    private fun iterateElves(elvesMap: Map<Int, List<Point>>, roundNumber: Int): Map<Int, List<Point>> {
        val proposedPositions = mutableMapOf<Point, MutableList<Point>>()
        val elvesNotMoved = mutableSetOf<Point>()

        val flatElves = elvesMap.values.flatten()
        flatElves.forEach { elf ->
            val timer = DurationTimer()
            val proposal = proposePosition(elf, elvesMap, roundNumber)
            proposalTime += timer.duration()

            if (proposal != null) {
                val current = proposedPositions.getOrPut(proposal, ::mutableListOf)
                current.add(elf)
            } else {
                elvesNotMoved.add(elf)
            }
        }

        val otherElvesNotMoved = proposedPositions.filter { it.value.size > 1 }.values.flatten().toSet()
        val validResults = proposedPositions.filter { it.value.size == 1 }
        val movedElves = validResults.keys

        val newElves = elvesNotMoved + otherElvesNotMoved + movedElves
        // printElves(newElves)
        check(newElves.size == flatElves.size)
        return newElves.groupBy { it.y }
    }

//    private fun printElves(elves: Set<Point>) {
//        val yMin = elves.minOf { it.y }
//        val yMax = elves.maxOf { it.y }
//        val xMin = elves.minOf { it.x }
//        val xMax = elves.maxOf { it.x }
//
//        val str = (yMin..yMax).joinToString("\n") { y ->
//            (xMin..xMax).joinToString("") { x -> if (elves.contains(Point(x, y))) "#" else "." }
//        }
//        println(str)
//        println()
//    }

    private fun proposePosition(elf: Point, elvesMap: Map<Int, List<Point>>, roundNumber: Int): Point? {
        val elvesWithRelevantY: List<Point> = (elf.y - 1..elf.y + 1).flatMap { elvesMap[it] ?: emptyList() }
        val relevantOtherElves =
            elvesWithRelevantY.filter { it.x in (elf.x - 1..elf.x + 1) && it != elf }

        if (relevantOtherElves.isEmpty()) {
            // Don't move
            return null
        }

        val northernElves = relevantOtherElves.filter { it.y < elf.y }
        val southernElves = relevantOtherElves.filter { it.y > elf.y }
        val westernElves = relevantOtherElves.filter { it.x < elf.x }
        val easternElves = relevantOtherElves.filter { it.x > elf.x }

        val movements = listOf(
            move(northernElves, Point(elf.x, elf.y - 1)),
            move(southernElves, Point(elf.x, elf.y + 1)),
            move(westernElves, Point(elf.x - 1, elf.y)),
            move(easternElves, Point(elf.x + 1, elf.y))
        )

        val indices = (roundNumber..roundNumber + 3).map { it.mod(4) }
        return indices.firstNotNullOfOrNull { movements[it] }
    }

    private fun move(elvesToCheck: List<Point>, newPoint: Point) = if (elvesToCheck.isEmpty()) newPoint else null

    override fun partB(): Any {
        var elves = startingElves

        var elfMoved = true
        var round = 0

        while (elfMoved) {
            val newElves = iterateElves(elves, round)

            elfMoved = (newElves.values.flatten() - elves.values.flatten()).isNotEmpty()
            elves = newElves
            round += 1
        }

        println(proposalTime)

        return round
    }
}