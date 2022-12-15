import kotlin.math.abs

private data class InputParameters(val filename: String, val yCoordPartA: Int, val maxX: Int, val maxY: Int)
private data class Sensor(val sensorPoint: Point, val beaconPoint: Point) {
    val range = sensorPoint.stepDistance(beaconPoint)
}

class Day15 : Solver {
    override val day = 15

    // private val inputParams = InputParameters("15e", 10, 20, 20) // Example
    private val inputParams = InputParameters("15", 2000000, 4000000, 4000000) // real

    private val input = readStringList(inputParams.filename)
    private val sensors = input.map(::parseInputLine)

    override fun partA() = getWhereBeaconCannotBe(inputParams.yCoordPartA)

    override fun partB() = findBeacon(inputParams.maxX, inputParams.maxY)

    private fun parseInputLine(line: String): Sensor {
        val match =
            Regex("Sensor at x=(-?\\d+), y=(-?\\d+): closest beacon is at x=(-?\\d+), y=(-?\\d+)").find(line)!!
        val (x1, y1, x2, y2) = match.destructured.toList().map(String::toInt)
        return Sensor(Point(x1, y1), Point(x2, y2))
    }

    private fun getWhereBeaconCannotBe(y: Int): Int {
        val beacons = sensors.map(Sensor::beaconPoint).filter { it.y == y }.map { it.x }

        val sensorRanges = sensors.map { sensor ->
            val xRadius = sensor.range - abs(sensor.sensorPoint.y - y)
            sensor.sensorPoint.x - xRadius to sensor.sensorPoint.x + xRadius
        }.sortedBy { it.first }

        val (total, _) = sensorRanges.fold(Pair(0, Int.MIN_VALUE)) { (pointsSoFar, currentMax), (xMin, xMax) ->
            val newMax = maxOf(xMax, currentMax)
            if (xMin > currentMax) {
                pointsSoFar + (xMax - xMin + 1) to newMax
            } else if (xMax <= currentMax) {
                // Fully contained, do nothing
                pointsSoFar to currentMax
            } else {
                pointsSoFar + xMax - currentMax + 1 to newMax
            }
        }

        return total - beacons.size
    }


    private fun findBeacon(maxX: Int, maxY: Int): Long {
        val result = sensors.firstNotNullOf { sensorToCheck ->
            val borderDistance = sensorToCheck.range + 1
            val offsets = (0..borderDistance) zip (borderDistance downTo 0)

            val borderPts = offsets.flatMap { (xOffset, yOffset) ->
                val x = sensorToCheck.sensorPoint.x
                val y = sensorToCheck.sensorPoint.y

                setOf(
                    Point(x + xOffset, y + yOffset),
                    Point(x + xOffset, y - yOffset),
                    Point(x - xOffset, y + yOffset),
                    Point(x - xOffset, y - yOffset)
                ).filter { it.x in 0..maxX && it.y in 0..maxY }
            }

            borderPts.firstOrNull { pt ->
                sensors.none { sensor -> sensor.pointInRange(pt) }
            }
        }

        return result.x.toLong() * 4000000 + result.y
    }

    private fun Sensor.pointInRange(pt: Point) = sensorPoint.stepDistance(pt) <= range
}