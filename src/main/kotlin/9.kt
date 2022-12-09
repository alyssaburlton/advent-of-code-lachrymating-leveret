import kotlin.math.sign

class Day9 : Solver {
    override val day = 9

    private val input: List<String> = readStringList("9")

    override fun partA(): Any {
        var headPosition = Point(0, 0)
        var tailPosition = Point(0, 0)
        val positionsVisited = mutableSetOf<Point>()
        positionsVisited.add(tailPosition)

        input.forEach { instruction ->
            val (dir, amount) = instruction.split(" ")

            val increments = (1..amount.toInt())
            increments.forEach { _ ->
                headPosition = moveHead(dir, headPosition)

                if (!headPosition.neighboursWithDiagonals().contains(tailPosition)) {
                    tailPosition = moveTail(headPosition, tailPosition)
                    positionsVisited.add(tailPosition)
                }
            }
        }

        return positionsVisited.size
    }

    private fun moveHead(direction: String, pos: Point) = when (direction) {
        "U" -> Point(pos.x, pos.y - 1)
        "D" -> Point(pos.x, pos.y + 1)
        "R" -> Point(pos.x + 1, pos.y)
        "L" -> Point(pos.x - 1, pos.y)
        else -> throw Error("Nope")
    }

    private fun moveTail(headPos: Point, tailPos: Point): Point {
        val xVector = headPos.x - tailPos.x
        val yVector = headPos.y - tailPos.y

        val normalX = if (xVector != 0) xVector.sign * (xVector / xVector) else 0
        val normalY = if (yVector != 0) yVector.sign * (yVector / yVector) else 0

        // println("yVector: $yVector, Normal y: $normalY")

        if (normalX > 1 || normalY > 1) {
            throw Error("Moving too much")
        }

        return Point(tailPos.x + normalX, tailPos.y + normalY)
    }

    override fun partB(): Any {
        val positions = mutableListOf<Point>()
        (0..9).forEach { _ ->
            positions.add(Point(0, 0))
        }


        val positionsVisited = mutableSetOf<Point>()
        positionsVisited.add(Point(0, 0))

        input.forEach { instruction ->
            val (dir, amount) = instruction.split(" ")

            val increments = (1..amount.toInt())
            increments.forEach { _ ->

                positions[0] = moveHead(dir, positions[0])

                (1..9).forEach { tailIndex ->
                    val headIndex = tailIndex - 1
                    if (!positions[headIndex].neighboursWithDiagonals().contains(positions[tailIndex])) {
                        positions[tailIndex] = moveTail(positions[headIndex], positions[tailIndex])

                        if (tailIndex == 9) {
                            positionsVisited.add(positions[tailIndex])
                        }

                    }
                }

            }
        }

        return positionsVisited.size
    }
}