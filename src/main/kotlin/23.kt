class Day23(mode: SolverMode) : Solver(23, mode) {
    private val input = readStringGrid(filename)
    private val startingElves = input.map.filter { it.value == "#" }.keys.groupBy { it.y }

    override fun partA() =
        (0..9).fold(startingElves) { elves, round -> iterateElves(elves, round).newElves }.let(::countEmptySpaces)

    override fun partB() = iterateElvesUntilDone(startingElves)

    private fun countEmptySpaces(elves: Map<Int, List<Point>>): Int {
        val points = elves.values.flatten()
        val xBounds = (points.maxOf { it.x } - points.minOf { it.x }) + 1
        val yBounds = (points.maxOf { it.y } - points.minOf { it.y }) + 1
        return (xBounds * yBounds) - points.size
    }

    private data class ElfRoundResult(val newElves: Map<Int, List<Point>>, val elfMoved: Boolean)

    private tailrec fun iterateElvesUntilDone(elvesMap: Map<Int, List<Point>>, roundNumber: Int = 0): Int {
        val result = iterateElves(elvesMap, roundNumber)
        if (!result.elfMoved) {
            return roundNumber + 1
        }

        return iterateElvesUntilDone(result.newElves, roundNumber + 1)
    }

    private fun iterateElves(elvesMap: Map<Int, List<Point>>, roundNumber: Int): ElfRoundResult {
        val flatElves = elvesMap.values.flatten()

        val result: Map<Point?, List<Point>> = flatElves.groupBy { elf -> proposePosition(elf, elvesMap, roundNumber) }

        val notMoved = result[null] ?: emptyList()
        val newPositions = result.filter { it.value.size == 1 }.keys.filterNotNull()
        val invalidProposals = result.filter { it.key != null && it.value.size > 1 }.values.flatten()

        val newElves = notMoved + newPositions + invalidProposals
        check(newElves.size == flatElves.size)
        return ElfRoundResult(newElves.groupBy { it.y }, newPositions.isNotEmpty())
    }

    private fun proposePosition(elf: Point, elvesMap: Map<Int, List<Point>>, roundNumber: Int): Point? {
        val relevantOtherElves: List<Point> = (elf.y - 1..elf.y + 1)
            .flatMap { elvesMap[it] ?: emptyList() }
            .filter { it.x in (elf.x - 1..elf.x + 1) && it != elf }

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
}