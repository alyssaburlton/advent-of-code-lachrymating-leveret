import kotlin.math.abs

private data class InputParameters(val yCoordPartA: Int, val maxX: Int, val maxY: Int)
private data class Sensor(val sensorPoint: Point, val beaconPoint: Point) {
    val range = sensorPoint.stepDistance(beaconPoint)
}

class Day15(mode: SolverMode) : Solver(15, mode) {
    private val inputParams = when (mode) {
        SolverMode.EXAMPLE -> InputParameters(10, 20, 20)
        else -> InputParameters(2000000, 4000000, 4000000)
    }

    private val input = readStringList(filename)
    private val sensors = input.map(::parseInputLine)

    override fun partA() = getWhereBeaconCannotBe()

    override fun partB() = findBeacon()

    private fun parseInputLine(line: String): Sensor {
        val match =
            Regex("Sensor at x=(-?\\d+), y=(-?\\d+): closest beacon is at x=(-?\\d+), y=(-?\\d+)").find(line)!!
        val (x1, y1, x2, y2) = match.destructured.toList().map(String::toInt)
        return Sensor(Point(x1, y1), Point(x2, y2))
    }

    private fun getWhereBeaconCannotBe(): Int {
        val (_, total) = getSortedSensorRangesForRow().fold(
            Pair(Int.MIN_VALUE, 0)
        ) { (currentMax, pointsSoFar), (xMin, xMax) ->
            maxOf(xMax, currentMax) to pointsSoFar + countNewPoints(currentMax, xMin, xMax)
        }

        return total - getBeaconsForRow().size
    }

    private fun countNewPoints(currentMax: Int, xMin: Int, xMax: Int) =
        if (xMin > currentMax)
            xMax - xMin + 1
        else if (xMax <= currentMax)
            0
        else
            xMax - currentMax

    private fun getBeaconsForRow() =
        sensors.map(Sensor::beaconPoint).filter { it.y == inputParams.yCoordPartA }.distinct()

    private fun getSortedSensorRangesForRow() = sensors.mapNotNull { sensor ->
        val xRadius = sensor.range - abs(sensor.sensorPoint.y - inputParams.yCoordPartA)
        if (xRadius <= 0) null else
            sensor.sensorPoint.x - xRadius to sensor.sensorPoint.x + xRadius
    }.sortedBy { it.first }

    private fun findBeacon() = sensors.firstNotNullOf(::findValidBorderPoint).let(::tuningFrequency)

    private fun findValidBorderPoint(sensor: Sensor) =
        sensor.getBorderPoints().firstOrNull(::borderPointCouldHaveBeacon)

    private fun borderPointCouldHaveBeacon(borderPt: Point) =
        borderPt.isWithinBoundary() && sensors.none { sensor -> sensor.pointInRange(borderPt) }

    private fun Point.isWithinBoundary() = x in 0..inputParams.maxX && y in 0..inputParams.maxY
    private fun tuningFrequency(point: Point) = point.x.toLong() * 4000000 + point.y

    private fun Sensor.getBorderPoints(): List<Point> {
        val (x, y) = sensorPoint
        val offsets = (0..range + 1) zip (range + 1 downTo 0)
        return offsets.flatMap { (xOffset, yOffset) ->
            setOf(
                Point(x + xOffset, y + yOffset),
                Point(x + xOffset, y - yOffset),
                Point(x - xOffset, y + yOffset),
                Point(x - xOffset, y - yOffset)
            )
        }
    }

    private fun Sensor.pointInRange(pt: Point) = sensorPoint.stepDistance(pt) <= range
}