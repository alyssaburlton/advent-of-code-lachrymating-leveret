private data class Rock(val points: List<PointL>, val type: RockType, val stopped: Boolean)
private data class ChamberState(
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

    private fun computeHeightAfter(rockCount: Long): Long {
        var state = ChamberState(0, 0, mutableMapOf(), input)
        val hashesSeen = mutableMapOf<ChamberStateHash, Pair<Long, Long>>()
        var heightFudgeFactor = 0L

        while (state.rocksDropped < rockCount) {
            val nextRockType = rockOrder[(state.rocksDropped % (rockOrder.size)).toInt()]
            if (heightFudgeFactor == 0L) {
                val hash = state.toHash(nextRockType)
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

            val rock = createRock(nextRockType, state.height())
            state = processTurn(rock, state)
        }

        return state.height() + heightFudgeFactor
    }

    private fun processTurn(startingRock: Rock, initialState: ChamberState): ChamberState {
        var state = initialState
        var rock = startingRock
        while (!rock.stopped) {
            rock = state.blowRock(rock)
            state = state.copy(windTicker = (state.windTicker + 1) % state.wind.size)
            rock = state.dropRock(rock)
        }

        val yValues = rock.points.groupBy { it.y }
        val updatedYValues = yValues.map { (y, pts) ->
            val existingPoints: List<PointL> = state.occupiedSpaces.getOrDefault(y, emptyList())
            y to existingPoints + pts
        }

        return state.clearLines(rock)
            .copy(occupiedSpaces = state.occupiedSpaces.plus(updatedYValues), rocksDropped = state.rocksDropped + 1)
    }

    private fun createRock(rockType: RockType, currentHeight: Long): Rock {
        val pts = getTemplatePoints(rockType).map { PointL(it.x, it.y + currentHeight + 3) }
        return Rock(pts, rockType, false)
    }

    private fun ChamberState.clearLines(restedRock: Rock): ChamberState {
        val yValues = restedRock.points.map { it.y }.toSet()
        val lowestLine = yValues.filter { occupiedSpaces[it]?.size == 7 }.minOfOrNull { it } ?: return this
        return copy(occupiedSpaces = occupiedSpaces.filter { it.key >= lowestLine })
    }

    private fun ChamberState.height() = occupiedSpaces.keys.maxOfOrNull { it + 1 } ?: 0L

    private fun ChamberState.dropRock(rock: Rock): Rock {
        val neighbours = rock.getDownNeighbours()
        if (invalidLocation(neighbours)) {
            return rock.copy(stopped = true)
        }

        return rock.copy(points = neighbours)
    }

    private fun ChamberState.blowRock(rock: Rock): Rock {
        val windDir = wind[windTicker]

        val neighbours = if (windDir == '<') rock.getLeftNeighbours() else rock.getRightNeighbours()
        if (invalidLocation(neighbours)) {
            return rock
        }

        return rock.copy(points = neighbours)
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

    private fun ChamberState.toHash(nextRockType: RockType): ChamberStateHash {
        return ChamberStateHash(windTicker, nextRockType, getNormalisedHeights().toString())
    }
}