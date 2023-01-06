class Day23(mode: SolverMode) : Solver(23, mode) {
    private val input = readStringGrid(filename)
    private val startingElves = input.map.filter { it.value == "#" }.keys.toList()
    // private var filterTime = 0L

    override fun partA() =
        (0..9).fold(startingElves) { elves, round -> iterateElves(elves, round).newElves }.let(::countEmptySpaces)

    override fun partB() = iterateElvesUntilDone(startingElves)

    private fun countEmptySpaces(elves: List<Point>): Int {
        val xBounds = (elves.maxOf { it.x } - elves.minOf { it.x }) + 1
        val yBounds = (elves.maxOf { it.y } - elves.minOf { it.y }) + 1
        return (xBounds * yBounds) - elves.size
    }

    private data class ElfRoundResult(val newElves: List<Point>, val elfMoved: Boolean)

    private tailrec fun iterateElvesUntilDone(elves: List<Point>, roundNumber: Int = 0): Int {
        val result = iterateElves(elves, roundNumber)
        if (!result.elfMoved) {
            // println(filterTime)
            return roundNumber + 1
        }

        return iterateElvesUntilDone(result.newElves, roundNumber + 1)
    }

    private fun iterateElves(elves: List<Point>, roundNumber: Int): ElfRoundResult {
        val elvesByX = elves.groupBy { it.x }
        val xBuckets = (elves.minOf { it.x }..elves.maxOf { it.x }).associateWith { x ->
            (x - 1..x + 1).flatMap { elvesByX[it] ?: emptyList() }
        }

        val result: Map<Point?, List<Point>> = elves.groupBy { elf -> proposePosition(elf, xBuckets, roundNumber) }

        val newElves =
            result.flatMap { (newPos, elves) -> if (newPos == null || elves.size > 1) elves else listOf(newPos) }
        return ElfRoundResult(newElves, result.size > 1)
    }

    private fun proposePosition(elf: Point, xBuckets: Map<Int, List<Point>>, roundNumber: Int): Point? {
        // val filterStart = System.currentTimeMillis()
        val relevantElves = xBuckets[elf.x]!!.filter { it.y in (elf.y - 1..elf.y + 1) }
        // filterTime += (System.currentTimeMillis() - filterStart)

        // Just us
        if (relevantElves.size == 1) {
            return null
        }

        val northernElves = relevantElves.filter { it.y < elf.y }
        val southernElves = relevantElves.filter { it.y > elf.y }
        val westernElves = relevantElves.filter { it.x < elf.x }
        val easternElves = relevantElves.filter { it.x > elf.x }

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
}