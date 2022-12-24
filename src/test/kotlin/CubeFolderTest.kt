import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class CubeFolderTest {
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
""", 3
        )
    }

    @Test
    fun `Should compute side length correctly - example input`() {
        val cubeStr = readGroupedList("22e")[0].joinToString("\n")
        testSideLength(cubeStr, 4)
    }

    @Test
    fun `Should compute side length correctly - real input`() {
        val cubeStr = readGroupedList("22")[0].joinToString("\n")
        testSideLength(cubeStr, 50)
    }

    private fun testSideLength(cubeNet: String, expected: Int) {
        val map = parsePointMap(cubeNet.split("\n"))
        getSideLength(map) shouldBe expected
    }
}