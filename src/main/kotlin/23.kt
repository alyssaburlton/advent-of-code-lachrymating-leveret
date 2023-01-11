class Day23(mode: SolverMode) : Solver(23, mode) {
    private val input = readStringGrid(filename)
    private val startingElves = input.map.filter { it.value == "#" }.keys.toList()

    override fun partA() = iterateElvesForCondition { _, rounds -> rounds == 10 }.let(::countEmptySpaces)

    override fun partB() =
        iterateElvesForCondition { result, _ -> result.elvesWhoMightMoveNextTurn.isEmpty() }.roundNumber

    private fun countEmptySpaces(result: FinalElfResult): Int {
        val elves = result.elves
        val xBounds = (elves.maxOf { it.x } - elves.minOf { it.x }) + 1
        val yBounds = (elves.maxOf { it.y } - elves.minOf { it.y }) + 1
        return (xBounds * yBounds) - elves.size
    }

    private data class ElfRoundResult(
        val newElves: List<Point>,
        val elvesWhoMightMoveNextTurn: List<Point>,
        val elvesWhoWontMoveNextTurn: List<Point>
    )

    private data class FinalElfResult(val elves: List<Point>, val roundNumber: Int)

    private fun iterateElvesForCondition(shouldStop: (result: ElfRoundResult, roundNumber: Int) -> Boolean) =
        iterateElvesUntilDone(ElfRoundResult(startingElves, startingElves, emptyList()), 1, shouldStop)

    private tailrec fun iterateElvesUntilDone(
        prevResult: ElfRoundResult,
        roundNumber: Int,
        shouldStop: (result: ElfRoundResult, roundNumber: Int) -> Boolean
    ): FinalElfResult {
        val result = iterateElves(prevResult, roundNumber)
        if (shouldStop(result, roundNumber)) {
            return FinalElfResult(result.newElves, roundNumber)
        }

        return iterateElvesUntilDone(result, roundNumber + 1, shouldStop)
    }

    private fun iterateElves(prevResult: ElfRoundResult, roundNumber: Int): ElfRoundResult {
        val elves = prevResult.newElves
        val elvesToTryMoving = prevResult.elvesWhoMightMoveNextTurn

        val elvesByX = elves.groupBy { it.x }
        val xBuckets = (elvesToTryMoving.minOf { it.x }..elvesToTryMoving.maxOf { it.x }).associateWith { x ->
            (x - 1..x + 1).flatMap { elvesByX[it] ?: emptyList() }
        }

        val result: Map<Point?, List<Point>> =
            elvesToTryMoving.groupBy { elf -> proposePosition(elf, xBuckets, roundNumber) }

        val pointsInvolvedInMoving = result.minus(null).flatMap { (newPos, elves) -> elves + newPos!! }
        val ptsThatMightMoveNextTime = pointsInvolvedInMoving.flatMap { it.neighboursWithDiagonals() }.toSet()

        val newElves =
            result.flatMap { (newPos, elves) -> if (newPos == null || elves.size > 1) elves else listOf(newPos) } + prevResult.elvesWhoWontMoveNextTurn

        val (elvesWhoMightMoveNextTime, elvesWhoWontMoveNextTime) = newElves.partition(ptsThatMightMoveNextTime::contains)

        return ElfRoundResult(newElves, elvesWhoMightMoveNextTime, elvesWhoWontMoveNextTime)
    }

    private fun proposePosition(elf: Point, xBuckets: Map<Int, List<Point>>, roundNumber: Int): Point? {
        val relevantElves = xBuckets[elf.x]!!.filter { it.y in (elf.y - 1..elf.y + 1) }

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

        val indices = (roundNumber - 1..roundNumber + 2).map { it.mod(4) }
        return indices.firstNotNullOfOrNull { movements[it] }
    }

    private fun move(elvesToCheck: List<Point>, newPoint: Point) = if (elvesToCheck.isEmpty()) newPoint else null
}