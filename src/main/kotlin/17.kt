private data class Rock(val PointLs: List<PointL>, val type: RockType, val stopped: Boolean)
private data class ChamberState(
    var rocksDropped: Int,
    var windTicker: Int,
    var occupiedSpaces: MutableMap<Long, MutableList<PointL>>,
    val wind: List<Char>
)

private enum class RockType {
    HORIZONTAL_LINE,
    PLUS,
    L,
    VERTICAL_LINE,
    BOX
}

private val rockOrder = RockType.values().toList()

class Day17 : Solver {
    override val day = 17

    private val input = readString("17").toCharArray().toList()

    override fun partA(): Any {
        val state = ChamberState(0, 0, mutableMapOf(), input)

        while (state.rocksDropped < 2022) {
            val nextRockType = rockOrder[state.rocksDropped % (rockOrder.size)]
            val rock = createRock(nextRockType, state.height())
            processTurn(rock, state)
            state.rocksDropped++
        }

        return state.height()
    }

    private fun processTurn(startingRock: Rock, state: ChamberState) {
        var rock = startingRock
        while (!rock.stopped) {
            rock = state.blowRock(rock)
            rock = state.dropRock(rock)
        }

        val yValues = rock.PointLs.groupBy { it.y }
        yValues.forEach { (y, pts) ->
            val list = state.occupiedSpaces.getOrPut(y, ::mutableListOf)
            list.addAll(pts)
        }

        state.clearLines(rock)
    }

    private fun createRock(rockType: RockType, currentHeight: Long): Rock {
        val pts = getPointLs(rockType).map { PointL(it.x, it.y + currentHeight + 3) }
        return Rock(pts, rockType, false)
    }

    override fun partB(): Any {
        return ""
    }

    private fun ChamberState.clearLines(restedRock: Rock) {
        val yValues = restedRock.PointLs.map { it.y }.toSet()
        val oldSize = occupiedSpaces.size

        val lowestLine = yValues.filter { occupiedSpaces[it]?.size == 7 }.minOfOrNull { it } ?: return

        occupiedSpaces = occupiedSpaces.filter { it.key >= lowestLine }.toMutableMap()

        println("Cleared ${lowestLine+1} lines, $oldSize -> ${occupiedSpaces.size}")
    }

    private fun ChamberState.height() = occupiedSpaces.keys.maxOfOrNull { it + 1 } ?: 0L

    private fun ChamberState.dropRock(rock: Rock): Rock {
        val neighbours = rock.getDownNeighbours()
        if (invalidLocation(neighbours)) {
            return rock.copy(stopped = true)
        }

        return rock.copy(PointLs = neighbours)
    }

    private fun ChamberState.blowRock(rock: Rock): Rock {
        val wind = wind[windTicker % wind.size]
        windTicker++

        // /println("Blowing $wind")

        val neighbours = if (wind == '<') rock.getLeftNeighbours() else rock.getRightNeighbours()
        if (invalidLocation(neighbours)) {
            return rock
        }

        return rock.copy(PointLs = neighbours)
    }

    private fun ChamberState.invalidLocation(pts: List<PointL>): Boolean =
        pts.any { it.x < 0 || it.x > 6 || it.y < 0 || (occupiedSpaces[it.y]?.contains(it) ?: false) }

    private fun Rock.getDownNeighbours(): List<PointL> = PointLs.map { PointL(it.x, it.y - 1) }
    private fun Rock.getLeftNeighbours(): List<PointL> = PointLs.map { PointL(it.x - 1, it.y) }
    private fun Rock.getRightNeighbours(): List<PointL> = PointLs.map { PointL(it.x + 1, it.y) }

    private fun getPointLs(type: RockType): List<PointL> {
        return when(type) {
            RockType.HORIZONTAL_LINE -> listOf(PointL(2, 0), PointL(3, 0), PointL(4, 0), PointL(5, 0))
            RockType.PLUS -> listOf(PointL(3, 0), PointL(2, 1), PointL(3, 1), PointL(4, 1), PointL(3, 2))
            RockType.L -> listOf(PointL(4, 2), PointL(4, 1), PointL(2, 0), PointL(3, 0), PointL(4, 0))
            RockType.VERTICAL_LINE -> listOf(PointL(2, 0), PointL(2, 1), PointL(2, 2), PointL(2, 3))
            RockType.BOX -> listOf(PointL(2, 0), PointL(3, 0), PointL(2, 1), PointL(3, 1))
        }
    }
}