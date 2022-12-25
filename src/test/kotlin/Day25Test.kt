import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class Day25Test {
    @Test
    fun `Decimal to snafu`() {
        Day25().decimalToSnafu(1) shouldBe "1"
        Day25().decimalToSnafu(2) shouldBe "2"
        Day25().decimalToSnafu(3) shouldBe "1="
        Day25().decimalToSnafu(4) shouldBe "1-"
        Day25().decimalToSnafu(5) shouldBe "10"
        Day25().decimalToSnafu(6) shouldBe "11"
        Day25().decimalToSnafu(7) shouldBe "12"
        Day25().decimalToSnafu(8) shouldBe "2="
        Day25().decimalToSnafu(9) shouldBe "2-"
        Day25().decimalToSnafu(10) shouldBe "20"
        Day25().decimalToSnafu(15) shouldBe "1=0"
        Day25().decimalToSnafu(20) shouldBe "1-0"
        Day25().decimalToSnafu(2022) shouldBe "1=11-2"
        Day25().decimalToSnafu(12345) shouldBe "1-0---0"
        Day25().decimalToSnafu(314159265) shouldBe "1121-1110-1=0"
    }

    @Test
    fun `Round trip`() {
        val day = Day25()
        (1..10000L).forEach {
            day.snafuToDecimal(day.decimalToSnafu(it)) shouldBe it
        }
    }
}