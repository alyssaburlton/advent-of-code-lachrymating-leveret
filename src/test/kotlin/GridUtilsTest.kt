import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class GridUtilsTest {
    @Test
    fun `Should be able to parse a grid from a file`() {
        val grid = readStringGrid("example_grid")
        grid[Point(0, 0)] shouldBe "0"

        grid.xMin() shouldBe 0
        grid.xMax() shouldBe 5
        grid.yMin() shouldBe 0
        grid.yMax() shouldBe 3
    }

    @Test
    fun `Should be able to transform grid values`() {
        val grid = readStringGrid("example_grid")
        val intGrid = grid.transformValues { it.toInt() }
        intGrid[Point(0, 0)] shouldBe 0
    }

    @Test
    fun `Should be able to transform back to a string for logging`() {
        val grid = readStringGrid("example_grid")
        val str = grid.prettyString()
        str shouldBe readString("example_grid")
    }

    @Test
    fun `Should be able to find neighbours`() {
        val grid = readStringGrid("example_grid")
        grid.neighbours(Point(0, 0)).shouldContainExactlyInAnyOrder(Point(1, 0), Point(0, 1))

        grid.neighbours(Point(2, 2)).shouldContainExactlyInAnyOrder(
            Point(1, 2),
            Point(3, 2),
            Point(2, 1),
            Point(2, 3)
        )
    }

    @Test
    fun `Should transpose correctly`() {
        val grid = readStringGrid("example_grid")
        val transposed = grid.transpose()
        transposed.prettyString() shouldBe readString("example_grid_transposed")
    }

    @Test
    fun `Should get rows correctly`() {
        val grid = readStringGrid("example_grid").transformValues(String::toInt)
        val rows = grid.rows()

        rows.shouldContainExactly(
            listOf(0, 0, 0, 0, 1, 0),
            listOf(1, 1, 0, 0, 1, 0),
            listOf(1, 0, 0, 0, 1, 0),
            listOf(0, 1, 0, 1, 1, 0),
        )
    }

    @Test
    fun `Should get columns correctly`() {
        val grid = readStringGrid("example_grid").transformValues(String::toInt)
        val cols = grid.columns()

        cols.shouldContainExactly(
            listOf(0, 1, 1, 0),
            listOf(0, 1, 0, 1),
            listOf(0, 0, 0, 0),
            listOf(0, 0, 0, 1),
            listOf(1, 1, 1, 1),
            listOf(0, 0, 0, 0),
        )
    }
}