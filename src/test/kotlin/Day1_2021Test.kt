import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class Day1_2021Test {
    @Test
    fun `Should count decreases correctly`() {
        val solver = Day1_2021()
        val testList = listOf(5, 4, 4, 7, 3, 5, 6, 5)

        solver.countConsecutiveDecreases(testList) shouldBe 3
    }
}