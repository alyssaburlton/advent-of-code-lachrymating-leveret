fun getSideLength(points: Map<Point, String>): Int {
    val cubePts = points.filter { it.value != " " }.keys
    val xMin = cubePts.minOf { it.x }
    val xMax = cubePts.maxOf { it.x }
    val yMin = cubePts.minOf { it.y }
    val yMax = cubePts.maxOf { it.y }

    return minOf(
        cubePts.count { it.x == xMin },
        cubePts.count { it.x == xMax },
        cubePts.count { it.y == yMin },
        cubePts.count { it.y == yMax })
}

typealias Line = List<Point>
typealias Face = List<Point>

//fun findFolds(points: Map<Point, String>): List<Pair<Line, Face>> {
//
//}