class Day23(mode: SolverMode) : Solver(23, mode) {
    private val input = readStringGrid(filename)
    private val startingElves = input.map.filter { it.value == "#" }.keys.toList()
    private val startingThirds = computeMidpoints(startingElves)
    private val movementFns =
        listOf(Direction(0, -1), Direction(0, 1), Direction(-1, 0), Direction(1, 0)).map { direction ->
            { elf: Point, relevantElves: List<Point> ->
                if (relevantElves.none { if (direction.y != 0) it.y == elf.y + direction.y else it.x == elf.x + direction.x })
                    elf + direction
                else null
            }
        }
    private var bucketTime = 0L
    private var proposalTime = 0L

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
            println("Proposal time: $proposalTime")
            println("Bucket time: $bucketTime")
            return roundNumber + 1
        }

        return iterateElvesUntilDone(result.newElves, roundNumber + 1)
    }

    private fun iterateElves(elves: List<Point>, roundNumber: Int): ElfRoundResult {
        val bucketStart = System.currentTimeMillis()

        val thirds = computeMidpoints(elves)
        // val thirds = startingThirds
        val elvesByX =
            elves.groupBy { if (it.y < thirds.first) "${it.x}_1" else if (it.y < thirds.second) "${it.x}_2" else "${it.x}_3" }

        val xBuckets = (elves.minOf { it.x }..elves.maxOf { it.x }).associateWith { x ->
            val pair = Triple(mutableListOf<Point>(), mutableListOf<Point>(), mutableListOf<Point>())
            (x - 1..x + 1).forEach {
                elvesByX["${it}_1"]?.let(pair.first::addAll)
                elvesByX["${it}_2"]?.let(pair.second::addAll)
                elvesByX["${it}_3"]?.let(pair.third::addAll)
            }

            pair
        }
        // println(xBuckets.mapValues { (key, value) -> Triple(value.first.size, value.second.size, value.third.size) })

        bucketTime += (System.currentTimeMillis() - bucketStart)

        val indices = (roundNumber..roundNumber + 3).map { it.mod(4) }


        val result: Map<Point?, List<Point>> =
            elves.groupBy { elf -> proposePosition(elf, xBuckets, thirds, indices) }

        val newElves =
            result.flatMap { (newPos, elves) -> if (newPos == null || elves.size > 1) elves else listOf(newPos) }
        return ElfRoundResult(newElves, result.size > 1)
    }

    private fun proposePosition(
        elf: Point,
        xBuckets: Map<Int, Triple<List<Point>, List<Point>, List<Point>>>,
        thirds: Pair<Int, Int>,
        indices: List<Int>
    ): Point? {
        val buckets = xBuckets[elf.x]!!
        val bucket =
            if (elf.y < thirds.first - 1)
                buckets.first
            else if (elf.y <= thirds.first)
                buckets.first + buckets.second
            else if (elf.y < thirds.second - 1)
                buckets.second
            else if (elf.y <= thirds.second)
                buckets.second + buckets.third
            else
                buckets.third

        val propStart = System.currentTimeMillis()
        val relevantElves = bucket.filter { it.y in (elf.y - 1..elf.y + 1) }
        proposalTime += (System.currentTimeMillis() - propStart)

        // Just us, or definitely surrounded without needing to do slower filters
        if (relevantElves.size == 1 || relevantElves.size >= 7) {
            return null
        }

        return indices.firstNotNullOfOrNull { movementFns[it](elf, relevantElves) }
    }

    private fun computeMidpoints(elves: List<Point>): Pair<Int, Int> {
        val sum = elves.maxOf { it.y } + elves.minOf { it.y }
        return (sum / 3) to (2 * sum / 3)
    }
}