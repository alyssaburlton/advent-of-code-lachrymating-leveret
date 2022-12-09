import kotlin.math.sign

class Day9 : Solver {
    override val day = 9

    private val input: List<String> = readStringList("9")

    override fun partA() = moveRope(1)

    override fun partB() = moveRope(9)

    private fun initialState(tailCount: Int) = (0..tailCount).map { Point(0, 0) }

    private fun List<Point>.moveHead(direction: String) = this.replaceAt(0, moveHead(direction, this[0]))

    private fun List<Point>.moveTail(tailIndex: Int): List<Point> {
        val headIndex = tailIndex - 1
        if (this[headIndex].neighboursWithDiagonals().contains(this[tailIndex])) {
            return this
        }

        val newTailPosition = moveTail(this[headIndex], this[tailIndex])
        return this.replaceAt(tailIndex, newTailPosition)
    }

    private fun moveHead(direction: String, pos: Point) = when (direction) {
        "U" -> Point(pos.x, pos.y - 1)
        "D" -> Point(pos.x, pos.y + 1)
        "R" -> Point(pos.x + 1, pos.y)
        "L" -> Point(pos.x - 1, pos.y)
        else -> throw Error("Nope")
    }

    private fun moveTail(headPos: Point, tailPos: Point): Point {
        val xNormal = (headPos.x - tailPos.x).sign
        val yNormal = (headPos.y - tailPos.y).sign

        return Point(tailPos.x + xNormal, tailPos.y + yNormal)
    }

    private fun moveRope(tailCount: Int) =
        input
            .runningFold(listOf(initialState(tailCount)), ::processSingleInstruction)
            .flatten()
            .map { it.last() }.distinct().size

    private fun processSingleInstruction(initialState: List<List<Point>>, instruction: String): List<List<Point>> {
        val (dir, amount) = instruction.split(" ")
        return (1..amount.toInt()).runningFold(initialState.last()) { ropeState, _ ->
            processSingleStep(ropeState, dir)
        }
    }

    private fun processSingleStep(initialState: List<Point>, direction: String): List<Point> {
        val movedHeadState = initialState.moveHead(direction)
        return (1 until movedHeadState.size).fold(movedHeadState) { ropeState, tailIndex ->
            ropeState.moveTail(tailIndex)
        }
    }
}