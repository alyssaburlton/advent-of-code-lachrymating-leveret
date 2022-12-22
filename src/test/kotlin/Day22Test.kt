import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class Day22Test {
    @Test
    fun `Should wrap around correctly`() {
        // Pencil line
        Day22().wrapAroundB(Point(149, 0), Point(1, 0)) shouldBe Pair(Point(99, 149), Point(-1, 0))
        Day22().wrapAroundB(Point(99, 100), Point(1, 0)) shouldBe Pair(Point(149, 49), Point(-1, 0))

        // Green squiggle
        Day22().wrapAroundB(Point(50, 50), Point(-1, 0)) shouldBe Pair(Point(0, 100), Point(0, 1))
        Day22().wrapAroundB(Point(49, 100), Point(0, -1)) shouldBe Pair(Point(50, 99), Point(1, 0))
    }
}