import kotlin.math.abs

private data class InputParameters(val filename: String, val yCoordPartA: Int, val maxX: Int, val maxY: Int)

class Day15 : Solver {
    override val day = 15

    // private val inputParams = InputParameters("15e", 10, 20, 20) // Example
    private val inputParams = InputParameters("15", 2000000, 4000000, 4000000) // real

    private val input = readStringList(inputParams.filename)
    private val pts = input.map(::parseInputLine)

    override fun partA(): Any {
        return getWhereBeaconCannotBe(inputParams.yCoordPartA).size - 1
    }

    override fun partB(): Any {
        return findBeacon(inputParams.maxX, inputParams.maxY)
    }

    private fun parseInputLine(line: String): Pair<Point, Point> {
        val match =
            Regex("Sensor at x=(-?\\d+), y=(-?\\d+): closest beacon is at x=(-?\\d+), y=(-?\\d+)").find(line)!!
        val (x1, y1, x2, y2) = match.destructured.toList().map(String::toInt)
        return Point(x1, y1) to Point(x2, y2)
    }

    private fun getWhereBeaconCannotBe(y: Int): List<Point> {
        return pts.flatMap { (sensorPt, beaconPt) ->
            val maxDist = sensorPt.stepDistance(beaconPt)
            val yDistToRow = abs(sensorPt.y - y)
            val remainingDistance = maxDist - yDistToRow
            val result = (sensorPt.x - remainingDistance..sensorPt.x + remainingDistance).map { Point(it, y) }
            result
        }.distinct()
    }

    private fun findBeacon(maxX: Int, maxY: Int): Long {
        val possiblePoints = pts.flatMap { (sensorPt, beaconPt) ->
            val dist = sensorPt.stepDistance(beaconPt) + 1
            // println(dist)
            val xRange = (0..dist)
            val yRange = (dist downTo 0)

            val offsets = xRange zip yRange
            offsets.flatMap { (xOffset, yOffset) ->
                val x = sensorPt.x
                val y = sensorPt.y

                setOf(
                    Point(x + xOffset, y + yOffset),
                    Point(x + xOffset, y - yOffset),
                    Point(x - xOffset, y + yOffset),
                    Point(x - xOffset, y - yOffset)
                ).filter { it.x in 0..maxX && it.y in 0..maxY }
            }.toSet()
        }.toSet()

        val result = possiblePoints.filter { pt ->
            pts.none { (sensorPt, beaconPt) ->
                sensorPt.stepDistance(pt) <= sensorPt.stepDistance(beaconPt)
            }
        }.only()

        return (result.x.toLong() * 4000000) + result.y
    }
}