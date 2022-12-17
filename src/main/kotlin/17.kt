private data class Rock(val points: List<PointL>, val type: RockType, val stopped: Boolean)
private data class ChamberState(
    val currentRock: Rock,
    val rocksDropped: Long,
    val windTicker: Int,
    val occupiedSpaces: Map<Long, List<PointL>>,
    val statesEncountered: Map<ChamberStateHash, Pair<Long, Long>>,
    val skippedHeight: Long
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

    private val wind = readString("17").toCharArray().toList()

    override fun partA() = dropRocks(2022)
    override fun partB() = dropRocks(1000000000000L)

    private fun initialState() =
        ChamberState(createRock(rockOrder.first(), 0), 0, 0, emptyMap(), emptyMap(), 0)

    private tailrec fun dropRocks(totalRocksToDrop: Long, state: ChamberState = initialState()): Long {
        if (state.rocksDropped == totalRocksToDrop) {
            return state.totalHeight()
        }

        if (state.skippedHeight == 0L && state.foundCycle()) {
            return dropRocks(totalRocksToDrop, state.skipAhead(totalRocksToDrop))
        }

        return dropRocks(totalRocksToDrop, state.storeCurrentHash().dropRockUntilStopped())
    }

    private fun ChamberState.storeCurrentHash(): ChamberState {
        val newPair = toHash() to Pair(rocksDropped, height())
        return copy(statesEncountered = statesEncountered.plus(newPair))
    }

    private fun ChamberState.foundCycle() =
        statesEncountered.containsKey(toHash())

    private fun ChamberState.skipAhead(totalRocksToDrop: Long): ChamberState {
        val (prevRocksThrown, prevHeight) = statesEncountered.getValue(toHash())
        val heightGain = height() - prevHeight
        val cycleLength = rocksDropped - prevRocksThrown
        val multiples = (totalRocksToDrop / cycleLength) - 1
        return copy(
            rocksDropped = rocksDropped + (multiples * cycleLength),
            statesEncountered = emptyMap(),
            skippedHeight = multiples * heightGain
        )
    }

    private tailrec fun ChamberState.dropRockUntilStopped(): ChamberState {
        if (currentRock.stopped) {
            return finaliseRock().generateNextRock()
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

    private fun ChamberState.finaliseRock(): ChamberState {
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
    private fun ChamberState.totalHeight() = skippedHeight + height()

    private fun ChamberState.dropRock(rock: Rock): Rock {
        val neighbours = rock.getDownNeighbours()
        return if (invalidLocation(neighbours)) rock.copy(stopped = true) else rock.copy(points = neighbours)
    }

    private fun ChamberState.blowRock(): Rock {
        val neighbours =
            if (wind[windTicker] == '<') currentRock.getLeftNeighbours() else currentRock.getRightNeighbours()

        return if (invalidLocation(neighbours)) currentRock else currentRock.copy(points = neighbours)
    }

    private fun ChamberState.invalidLocation(pts: List<PointL>): Boolean =
        pts.any { it.x < 0 || it.x > 6 || it.y < 0 || (occupiedSpaces[it.y]?.contains(it) ?: false) }

    private fun Rock.getDownNeighbours(): List<PointL> = points.map { PointL(it.x, it.y - 1) }
    private fun Rock.getLeftNeighbours(): List<PointL> = points.map { PointL(it.x - 1, it.y) }
    private fun Rock.getRightNeighbours(): List<PointL> = points.map { PointL(it.x + 1, it.y) }

    private fun getTemplatePoints(type: RockType) =
        when (type) {
            RockType.HORIZONTAL_LINE -> listOf(PointL(2, 0), PointL(3, 0), PointL(4, 0), PointL(5, 0))
            RockType.PLUS -> listOf(PointL(3, 0), PointL(2, 1), PointL(3, 1), PointL(4, 1), PointL(3, 2))
            RockType.L -> listOf(PointL(4, 2), PointL(4, 1), PointL(2, 0), PointL(3, 0), PointL(4, 0))
            RockType.VERTICAL_LINE -> listOf(PointL(2, 0), PointL(2, 1), PointL(2, 2), PointL(2, 3))
            RockType.BOX -> listOf(PointL(2, 0), PointL(3, 0), PointL(2, 1), PointL(3, 1))
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