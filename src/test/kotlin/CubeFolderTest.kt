import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class CubeFolderTest {
    private val exampleCubeStr = readGroupedList("22e")[0].joinToString("\n")
    private val inputCubeStr = readGroupedList("22")[0].joinToString("\n")

    @Test
    fun `Should compute the side length correctly`() {
        testSideLength(
            """
    xx
    xx
xxxxxx
xxxxxx
    xxxx
    xxxx
""", 2
        )
    }

    @Test
    fun `Should compute the side length correctly - 3`() {
        testSideLength(
            """
   xxx
   xxx
   xxx
xxxxxxxxx
xxxxxxxxx
xxxxxxxxx
   xxx
   xxx
   xxx
   xxx
   xxx
   xxx
""", 3
        )
    }

    @Test
    fun `Should compute side length correctly - example input`() {
        testSideLength(exampleCubeStr, 4)
    }

    @Test
    fun `Should compute side length correctly - real input`() {
        testSideLength(inputCubeStr, 50)
    }

    private fun testSideLength(cubeNet: String, expected: Int) {
        val map = parsePointMap(cubeNet.split("\n"))
        getSideLength(map) shouldBe expected
    }

    @Test
    fun `Should find top left corners of faces correctly`() {
        testTopLeftCorners(
            """
    xx
    xx
xxxxxx
xxxxxx
    xxxx
    xxxx
""", setOf(Point(4, 1), Point(0, 3), Point(2, 3), Point(4, 3), Point(4, 5), Point(6, 5))
        )

        testTopLeftCorners(
            """
  xxxx
  xxxx
  xx
  xx
xxxx
xxxx
xx
xx
""", setOf(Point(2, 1), Point(4, 1), Point(2, 3), Point(0, 5), Point(2, 5), Point(0, 7))
        )

        testTopLeftCorners(
            inputCubeStr,
            setOf(Point(50, 0), Point(100, 0), Point(50, 50), Point(50, 100), Point(0, 100), Point(0, 150))
        )
    }

    private fun testTopLeftCorners(cubeNet: String, expected: Set<Point>) {
        val map = parsePointMap(cubeNet.split("\n"))
        findTopLeftsOfFaces(map) shouldBe expected
    }

    @Test
    fun `Should find fold lines correctly - example input`() {
        val map = parsePointMap(exampleCubeStr.split("\n"))
        val foldLines = findFoldLines(map)
        foldLines.size shouldBe 5

        foldLines.shouldContain(FoldLine(FoldType.VERTICAL, (8..11).map { Point(it, 3) }))
        foldLines.shouldContain(FoldLine(FoldType.HORIZONTAL, (4..7).map { Point(3, it) }))
        foldLines.shouldContain(FoldLine(FoldType.HORIZONTAL, (4..7).map { Point(7, it) }))
        foldLines.shouldContain(FoldLine(FoldType.VERTICAL, (8..11).map { Point(it, 7) }))
        foldLines.shouldContain(FoldLine(FoldType.HORIZONTAL, (8..11).map { Point(11, it) }))
    }

    @Test
    fun `Should find fold lines correctly - real input`() {
        val map = parsePointMap(inputCubeStr.split("\n"))
        val foldLines = findFoldLines(map)
        foldLines.size shouldBe 5

        foldLines.shouldContain(FoldLine(FoldType.HORIZONTAL, (0..49).map { Point(99, it) }))
        foldLines.shouldContain(FoldLine(FoldType.VERTICAL, (50..99).map { Point(it, 49) }))
        foldLines.shouldContain(FoldLine(FoldType.VERTICAL, (50..99).map { Point(it, 99) }))
        foldLines.shouldContain(FoldLine(FoldType.HORIZONTAL, (100..149).map { Point(49, it) }))
        foldLines.shouldContain(FoldLine(FoldType.VERTICAL, (0..49).map { Point(it, 149) }))
    }
}