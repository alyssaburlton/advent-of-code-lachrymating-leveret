fun foldUpNet(points: Map<Point, String>): Map<Point, Point3D> {
    val foldLines = findFoldLines(points)

    val originalCubePoints = points.cubePoints()
    val cubeMap = originalCubePoints.associateWith { Point3D(it.x, it.y, 0) }

    return foldLines.fold(cubeMap) { currentPoints, foldLine ->
        val allPointsToFold = computePointsToMove(originalCubePoints, foldLine)
        val foldPointsIn3Space = foldLine.points.map { currentPoints[it]!! }

        // Actually need to do some folding here...

        currentPoints
    }
}

private fun computePointsToMove(cubePoints: Set<Point>, foldLine: FoldLine): List<Point> {
    return if (foldLine.type == FoldType.VERTICAL) {
        val y = foldLine.points.first().y
        cubePoints.filter { it.y <= y }
    } else {
        val x = foldLine.points.first().x
        cubePoints.filter { it.x <= x }
    }
}

fun getSideLength(points: Map<Point, String>): Int {
    val cubePts = points.cubePoints()
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

data class Face(val xMin: Int, val xMax: Int, val yMin: Int, val yMax: Int)

enum class FoldType {
    VERTICAL,
    HORIZONTAL
}

data class FoldLine(val type: FoldType, val points: List<Point>)

fun findTopLeftsOfFaces(points: Map<Point, String>): Set<Point> {
    val sideLength = getSideLength(points)
    val cubePoints = points.cubePoints()
    val yMin = cubePoints.minOf { it.y }
    val topLeftPoint = points.cubePoints().filter { it.y == yMin }.minBy { it.x }
    return findJoiningFaces(topLeftPoint, cubePoints, sideLength, emptySet())
}

fun findFoldLines(points: Map<Point, String>): Set<FoldLine> {
    val sideLength = getSideLength(points)
    val faces = findTopLeftsOfFaces(points).map { it.toFace(sideLength) }

    check(faces.size == 6)

    return findFoldLines(faces).toSet()
}

/**
 * For VERTICAL folds, return the bottom line of the top face. We'll fold everything from here upwards
 *
 *   XX
 *   XX <- Return this line
 *   --
 *   XX
 *   XX
 *
 * For HORIZONTAL folds, return the right line of the left face. We'll fold everything from here leftwards
 *
 *   Return this line
 *   |
 *   V
 *  xx|xx
 *  xx|xx
 */
private fun findFoldLines(faces: List<Face>) =
    faces.flatMap { face ->
        val list = mutableListOf<FoldLine>()

        val verticallyAligned = faces.filter { it.xMin == face.xMin }
        val above = verticallyAligned.find { it.yMax == face.yMin - 1 }
        if (above != null) {
            val line = (face.xMin..face.xMax).map { Point(it, above.yMax) }
            list.add(FoldLine(FoldType.VERTICAL, line))
        }
        val below = verticallyAligned.find { it.yMin == face.yMax + 1 }
        if (below != null) {
            val line = (face.xMin..face.xMax).map { Point(it, face.yMax) }
            list.add(FoldLine(FoldType.VERTICAL, line))
        }

        val horizontallyAligned = faces.filter { it.yMin == face.yMin }
        val left = horizontallyAligned.find { it.xMax == face.xMin - 1 }
        if (left != null) {
            val line = (face.yMin..face.yMax).map { Point(left.xMax, it) }
            list.add(FoldLine(FoldType.HORIZONTAL, line))
        }

        val right = horizontallyAligned.find { it.xMin == face.xMax + 1 }
        if (right != null) {
            val line = (face.yMin..face.yMax).map { Point(face.xMax, it) }
            list.add(FoldLine(FoldType.HORIZONTAL, line))
        }

        list
    }.distinct()

private fun Point.toFace(sideLength: Int): Face = Face(x, x + sideLength - 1, y, y + sideLength - 1)

private fun findJoiningFaces(
    currentPoint: Point,
    cubePoints: Set<Point>,
    sideLength: Int,
    found: Set<Point>
): Set<Point> {
    val new = currentPoint.neighbours(sideLength).filter { cubePoints.contains(it) && !found.contains(it) }
    if (new.isEmpty()) {
        return found
    }

    val newFound = found + currentPoint + new
    return new.flatMap { findJoiningFaces(it, cubePoints, sideLength, newFound) }.toSet()
}


private fun Map<Point, String>.cubePoints() = filter { it.value != " " }.keys