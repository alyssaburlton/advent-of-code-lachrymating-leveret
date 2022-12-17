private const val WIDTH = 7 //0,1,2,3,4,5,6
private data class Rock(val points: List<Point>, val type: RockType, val stopped: Boolean)
private data class ChamberState(var rocksDropped: Int, var windTicker: Int, var linesCleared: Int, var occupiedSpaces: MutableSet<Point>, val wind: List<Char>)

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
        val state = ChamberState(0, 0, 0, mutableSetOf(), input)

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

        state.occupiedSpaces.addAll(rock.points)

        state.clearLines()
    }

    private fun createRock(rockType: RockType, currentHeight: Int): Rock {
        val pts = getPoints(rockType).map { Point(it.x, it.y + currentHeight + 3) }
        return Rock(pts, rockType, false)
    }

    override fun partB(): Any {
        return ""
    }

    private fun ChamberState.clearLines() {
        val oldSize = occupiedSpaces.size
        val highestLine = occupiedSpaces.groupBy { it.y }.filter { it.value.size == 7 }.maxOfOrNull { it.key } ?: return
        linesCleared += highestLine + 1

        occupiedSpaces = occupiedSpaces.filter { it.y > highestLine }.toMutableSet()

        println("Cleared ${highestLine+1} lines, $oldSize -> ${occupiedSpaces.size}")
    }

    private fun ChamberState.height() = linesCleared + (occupiedSpaces.maxOfOrNull { it.y + 1 } ?: 0)

    private fun ChamberState.dropRock(rock: Rock): Rock {
        val neighbours = rock.getDownNeighbours()
        if (invalidLocation(neighbours)) {
            return rock.copy(stopped = true)
        }

        return rock.copy(points = neighbours)
    }

    private fun ChamberState.blowRock(rock: Rock): Rock {
        val wind = wind[windTicker % wind.size]
        windTicker++

        // /println("Blowing $wind")

        val neighbours = if (wind == '<') rock.getLeftNeighbours() else rock.getRightNeighbours()
        if (invalidLocation(neighbours)) {
            return rock
        }

        return rock.copy(points = neighbours)
    }

    private fun ChamberState.invalidLocation(pts: List<Point>): Boolean =
        pts.any { it.x < 0 || it.x > 6 || it.y < 0 || occupiedSpaces.contains(it) }

    private fun Rock.getDownNeighbours(): List<Point> = points.map { Point(it.x, it.y - 1) }
    private fun Rock.getLeftNeighbours(): List<Point> = points.map { Point(it.x - 1, it.y) }
    private fun Rock.getRightNeighbours(): List<Point> = points.map { Point(it.x + 1, it.y) }

    private fun getPoints(type: RockType): List<Point> {
        return when(type) {
            RockType.HORIZONTAL_LINE -> listOf(Point(2, 0), Point(3, 0), Point(4, 0), Point(5, 0))
            RockType.PLUS -> listOf(Point(3, 0), Point(2, 1), Point(3, 1), Point(4, 1), Point(3, 2))
            RockType.L -> listOf(Point(4, 2), Point(4, 1), Point(2, 0), Point(3, 0), Point(4, 0))
            RockType.VERTICAL_LINE -> listOf(Point(2, 0), Point(2, 1), Point(2, 2), Point(2, 3))
            RockType.BOX -> listOf(Point(2, 0), Point(3, 0), Point(2, 1), Point(3, 1))
        }
    }
}