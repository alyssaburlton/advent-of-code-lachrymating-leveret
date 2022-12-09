import kotlin.math.sign

data class RopeState(val positions: List<Point>, val tailPoints: Set<Point>, val tailCount: Int) {
    constructor(tailCount: Int) : this((0..tailCount).map { Point(0, 0) }, setOf(Point(0, 0)), tailCount)
}

class Day9 : Solver {
    override val day = 9

    private val input: List<String> = readStringList("9")

    override fun partA() = moveRope(1)

    override fun partB() = moveRope(9)

    private fun RopeState.moveHead(direction: String): RopeState {
        val newPositions = positions.replaceAt(0, moveHead(direction, positions[0]))
        return RopeState(newPositions, this.tailPoints, tailCount)
    }

    private fun RopeState.moveTail(tailIndex: Int): RopeState {
        val headIndex = tailIndex - 1
        if (positions[headIndex].neighboursWithDiagonals().contains(positions[tailIndex])) {
            return this
        }

        val newTailPosition = moveTail(positions[headIndex], positions[tailIndex])
        return RopeState(
            positions.replaceAt(tailIndex, newTailPosition),
            if (tailIndex == positions.size - 1) tailPoints + newTailPosition else tailPoints,
            tailCount
        )
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

        if (normalX > 1 || normalY > 1) {
            throw Error("Moving too much")
        }

        return Point(tailPos.x + normalX, tailPos.y + normalY)
    }

    private fun moveRope(tailCount: Int) = input.fold(RopeState(tailCount), ::processSingleInstruction).tailPoints.size

    private fun processSingleInstruction(initialState: RopeState, instruction: String): RopeState {
        val (dir, amount) = instruction.split(" ")
        return (1..amount.toInt()).fold(initialState) { ropeState, _ -> processSingleStep(ropeState, dir) }
    }

    private fun processSingleStep(initialState: RopeState, direction: String): RopeState {
        val movedHeadState = initialState.moveHead(direction)
        return (1..movedHeadState.tailCount).fold(movedHeadState) { ropeState, tailIndex ->
            ropeState.moveTail(tailIndex)
        }
    }
}