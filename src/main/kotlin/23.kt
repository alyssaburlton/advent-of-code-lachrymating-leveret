/**
 * Naive would have done 2932900. We considered 1370383, only 926909 moved. Wastage is 443474
 * Naive would have done 2932900. We considered 1305306, only 926909 moved. Wastage is 378397
 */
class Day23(mode: SolverMode) : Solver(23, mode) {
    private val input = readStringGrid(filename)
    private val startingElves = input.map.filter { it.value == "#" }.keys.toList()
    private var allElves = 0
    private var elvesConsidered = 0
    private var elvesActuallyMoved = 0
    private var proposalTime = 0L
    private var surroundedTime = 0L
    private var isolatedTime = 0L
    private var finaliseTime = 0L

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
        val elvesWhoWereSurrounded: List<Point>,
        val elvesWhoWereIsolated: List<Point>,
    )

    private data class FinalElfResult(val elves: List<Point>, val roundNumber: Int)

    private fun iterateElvesForCondition(shouldStop: (result: ElfRoundResult, roundNumber: Int) -> Boolean) =
        iterateElvesUntilDone(ElfRoundResult(startingElves, startingElves, emptyList(), emptyList()), 1, shouldStop)

    private tailrec fun iterateElvesUntilDone(
        prevResult: ElfRoundResult,
        roundNumber: Int,
        shouldStop: (result: ElfRoundResult, roundNumber: Int) -> Boolean
    ): FinalElfResult {
        val result = iterateElves(prevResult, roundNumber)
        if (shouldStop(result, roundNumber)) {
            if (roundNumber < 12) {
                elvesActuallyMoved = 0
                elvesConsidered = 0
                allElves = 0
                proposalTime = 0
                surroundedTime = 0
                isolatedTime = 0
                finaliseTime = 0
            } else {
                println("Naive would have done $allElves. We considered $elvesConsidered, only $elvesActuallyMoved moved. Wastage is ${elvesConsidered - elvesActuallyMoved}")
                println("Proposal time: $proposalTime")
                println("Surrounded time: $surroundedTime")
                println("Isolated time: $isolatedTime")
                println("Finalise time: $finaliseTime")
            }
            return FinalElfResult(result.newElves, roundNumber)
        }

        return iterateElvesUntilDone(result, roundNumber + 1, shouldStop)
    }

    private fun iterateElves(prevResult: ElfRoundResult, roundNumber: Int): ElfRoundResult {
        val elves = prevResult.newElves
        val elvesToTryMoving = prevResult.elvesWhoMightMoveNextTurn

        allElves += elves.size
        elvesConsidered += elvesToTryMoving.size

        val propStart = System.currentTimeMillis()
        val elvesByX = elves.groupBy { it.x }
        val xBuckets = (elvesToTryMoving.minOf { it.x }..elvesToTryMoving.maxOf { it.x }).associateWith { x ->
            (x - 1..x + 1).flatMap { elvesByX[it] ?: emptyList() }
        }

        val result: Map<Any, List<Point>> =
            elvesToTryMoving.groupBy { elf -> proposePosition(elf, xBuckets, roundNumber) }
        proposalTime += (System.currentTimeMillis() - propStart)

        val elvesWhoTriedToMove =
            result.minus("isolated").minus("surrounded") as Map<Point, List<Point>>

        val elvesMoved =
            elvesWhoTriedToMove.filter { (_, elves) -> elves.size == 1 }.mapValues { (_, elves) -> elves.only() }
        elvesActuallyMoved += elvesMoved.size

        //Do old naive way
        if (elvesMoved.size > 1) {
            val newElves =
                result.flatMap { (newPos, elves) -> if (newPos !is Point || elves.size > 1) elves else listOf(newPos) } + prevResult.elvesWhoWereIsolated + prevResult.elvesWhoWereSurrounded
            return ElfRoundResult(newElves, newElves, emptyList(), emptyList())
        }

        // Any elves who did not move because they were surrounded might move next turn if a neighbouring elf moved
        val surroundedStart = System.currentTimeMillis()
        val allSurroundedElves = prevResult.elvesWhoWereSurrounded + result.getOrDefault("surrounded", emptyList())
        val surroundedPtsThatMightMoveNext =
            elvesMoved.values.flatMap { it.neighboursWithDiagonals() }
                .toSet() // This line and the equivalent for isolated are the crux. How can I do this faster?
        val (surroundedElvesWhoMightMoveNext, surroundedElvesWhoWontMoveNext) = allSurroundedElves.partition(
            surroundedPtsThatMightMoveNext::contains
        )
        surroundedTime += (System.currentTimeMillis() - surroundedStart)

        // Any elves who did not move because they were isolated might move next turn if an elf moved next to them
        val isolatedStart = System.currentTimeMillis()
        val allIsolatedElves = prevResult.elvesWhoWereIsolated + result.getOrDefault("isolated", emptyList())
        val isolatedPtsThatMightMoveNext =
            elvesMoved.keys.flatMap { it.neighboursWithDiagonals() }.toSet()
        val (isolatedElvesWhoMightMoveNext, isolatedElvesWhoWontMoveNext) = allIsolatedElves.partition(
            isolatedPtsThatMightMoveNext::contains
        )
        isolatedTime += (System.currentTimeMillis() - isolatedStart)


        val finaliseStart = System.currentTimeMillis()
        val elvesClashed = elvesWhoTriedToMove.filter { (_, elves) -> elves.size > 1 }.values.flatten()
        val elvesWhoMightMoveNext =
            surroundedElvesWhoMightMoveNext + isolatedElvesWhoMightMoveNext + elvesMoved.keys + elvesClashed

        val newElves = elvesWhoMightMoveNext + surroundedElvesWhoWontMoveNext + isolatedElvesWhoWontMoveNext
        check(newElves.size == startingElves.size)
        finaliseTime += (System.currentTimeMillis() - finaliseStart)

        return ElfRoundResult(
            newElves,
            elvesWhoMightMoveNext,
            surroundedElvesWhoWontMoveNext,
            isolatedElvesWhoWontMoveNext
        )
    }

    private fun proposePosition(elf: Point, xBuckets: Map<Int, List<Point>>, roundNumber: Int): Any {
        val relevantElves = xBuckets[elf.x]!!.filter { it.y in (elf.y - 1..elf.y + 1) }

        // Just us
        if (relevantElves.size == 1) {
            return "isolated"
        }

        if (relevantElves.size >= 7) {
            return "surrounded"
        }

        val movements = listOf(
            move(relevantElves, Point(elf.x, elf.y - 1)) { it.y < elf.y },
            move(relevantElves, Point(elf.x, elf.y + 1)) { it.y > elf.y },
            move(relevantElves, Point(elf.x - 1, elf.y)) { it.x < elf.x },
            move(relevantElves, Point(elf.x + 1, elf.y)) { it.x > elf.x }
        )

        val indices = (roundNumber - 1..roundNumber + 2).map { it.mod(4) }
        return indices.firstNotNullOfOrNull { movements[it]() } ?: "surrounded"
    }

    private fun move(relevantElves: List<Point>, newPoint: Point, filterFn: (elf: Point) -> Boolean) = { ->
        if (relevantElves.none { filterFn(it) }) newPoint else null
    }
}