private data class Rock(val points: List<PointL>, val type: RockType, val stopped: Boolean)
private data class ChamberState(
    val currentRock: Rock,
    val rocksDropped: Long,
    val windTicker: Int,
    val occupiedSpaces: Map<Long, List<PointL>>,
    val wind: List<Char>
)

private data class ChamberStateHash(val windTicker: Int, val nextRockType: RockType, val normalisedHeights: String)

private enum class RockType {
    HORIZONTAL_LINE,
    PLUS,
    L,
    VERTICAL_LINE,
    BOX
}

private val rockOrder = RockType.values()

class Day17 : Solver {
    override val day = 17

    private val input = readString("17").toCharArray().toList()

    override fun partA() = computeHeightAfter(2022)
    override fun partB() = computeHeightAfter(1000000000000L)

    private fun initialState() =
        ChamberState(createRock(rockOrder.first(), 0), 0, 0, emptyMap(), input)

    private fun computeHeightAfter(rockCount: Long): Long {
        var state = initialState()
        val hashesSeen = mutableMapOf<ChamberStateHash, Pair<Long, Long>>()
        var heightFudgeFactor = 0L

        while (state.rocksDropped < rockCount) {
            if (heightFudgeFactor == 0L) {
                val hash = state.toHash()
                if (hashesSeen.containsKey(hash)) {
                    val (prevRocksThrown, prevHeight) = hashesSeen.getValue(hash)
                    val heightGain = state.height() - prevHeight
                    val cycleLength = state.rocksDropped - prevRocksThrown
                    val multiples = (rockCount / cycleLength) - 1
                    heightFudgeFactor = multiples * heightGain
                    state = state.copy(rocksDropped = state.rocksDropped + (multiples * cycleLength))
                } else {
                    hashesSeen[hash] = Pair(state.rocksDropped, state.height())
                }
            }

            state = processTurn(state)
        }

        return state.height() + heightFudgeFactor
    }

    private fun processTurn(initialState: ChamberState) =
        initialState.dropRockUntilStopped().generateNextRock()

    private tailrec fun ChamberState.dropRockUntilStopped(): ChamberState {
        if (currentRock.stopped) {
            return updateOccupiedSpaces()
        }

        return copy(
            currentRock = dropRock(blowRock()),
            windTicker = (windTicker + 1) % wind.size
        ).dropRockUntilStopped()
    }

    private fun ChamberState.generateNextRock(): ChamberState {
        val nextRockType = rockOrder[(rocksDropped % (rockOrder.size)).toInt()]
        return this.copy(currentRock = createRock(nextRockType, height()))
    }

    private fun createRock(rockType: RockType, currentHeight: Long): Rock {
        val pts = getTemplatePoints(rockType).map { PointL(it.x, it.y + currentHeight + 3) }
        return Rock(pts, rockType, false)
    }

    private fun ChamberState.updateOccupiedSpaces(): ChamberState {
        val rockOccupation = currentRock.points.groupBy { it.y }
        val updatedYValues = rockOccupation.map { (y, pts) ->
            val existingPoints: List<PointL> = occupiedSpaces.getOrDefault(y, emptyList())
            y to existingPoints + pts
        }

        val newlyOccupied = occupiedSpaces.plus(updatedYValues)
        val lowestLine = rockOccupation.keys.filter { newlyOccupied[it]?.size == 7 }.minOfOrNull { it } ?: 0
        return copy(rocksDropped = rocksDropped + 1, occupiedSpaces = newlyOccupied.filter { it.key >= lowestLine })
    }

    private fun ChamberState.height() = occupiedSpaces.keys.maxOfOrNull { it + 1 } ?: 0L

    private fun ChamberState.dropRock(rock: Rock): Rock {
        val neighbours = rock.getDownNeighbours()
        if (invalidLocation(neighbours)) {
            return rock.copy(stopped = true)
        }

        return rock.copy(points = neighbours)
    }

    private fun ChamberState.blowRock(): Rock {
        val windDir = wind[windTicker]

        val neighbours = if (windDir == '<') currentRock.getLeftNeighbours() else currentRock.getRightNeighbours()
        if (invalidLocation(neighbours)) {
            return currentRock
        }

        return currentRock.copy(points = neighbours)
    }

    private fun ChamberState.invalidLocation(pts: List<PointL>): Boolean =
        pts.any { it.x < 0 || it.x > 6 || it.y < 0 || (occupiedSpaces[it.y]?.contains(it) ?: false) }

    private fun Rock.getDownNeighbours(): List<PointL> = points.map { PointL(it.x, it.y - 1) }
    private fun Rock.getLeftNeighbours(): List<PointL> = points.map { PointL(it.x - 1, it.y) }
    private fun Rock.getRightNeighbours(): List<PointL> = points.map { PointL(it.x + 1, it.y) }

    private fun getTemplatePoints(type: RockType): List<PointL> {
        return when (type) {
            RockType.HORIZONTAL_LINE -> listOf(PointL(2, 0), PointL(3, 0), PointL(4, 0), PointL(5, 0))
            RockType.PLUS -> listOf(PointL(3, 0), PointL(2, 1), PointL(3, 1), PointL(4, 1), PointL(3, 2))
            RockType.L -> listOf(PointL(4, 2), PointL(4, 1), PointL(2, 0), PointL(3, 0), PointL(4, 0))
            RockType.VERTICAL_LINE -> listOf(PointL(2, 0), PointL(2, 1), PointL(2, 2), PointL(2, 3))
            RockType.BOX -> listOf(PointL(2, 0), PointL(3, 0), PointL(2, 1), PointL(3, 1))
        }
    }

    private fun ChamberState.getNormalisedHeights(): List<Long> {
        val xToHighestPoint: Map<Long, Long> =
            occupiedSpaces.values.flatten().groupBy { it.x }.mapValues { it.value.maxOf(PointL::y) }
        val orderedHeights = xToHighestPoint.entries.sortedBy { it.key }.map { it.value }
        return orderedHeights.map { it - orderedHeights.min() }
    }

    private fun ChamberState.toHash() =
        ChamberStateHash(windTicker, currentRock.type, getNormalisedHeights().toString())
}