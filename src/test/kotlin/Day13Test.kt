import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class Day13Test {
    @Test
    fun `Should parse items`() {
        Day13().parseItems("[1,1,3,1,1]") shouldBe listOf(1, 1, 3, 1, 1)
        Day13().parseItems("[[1],[2,3,4]]") shouldBe listOf(listOf(1), listOf(2, 3, 4))
        Day13().parseItems("[[8,7,6]]") shouldBe listOf(listOf(8, 7, 6))
        Day13().parseItems("[[[8,7,6]]]") shouldBe listOf(listOf(listOf(8, 7, 6)))

        Day13().parseItems("[1,[2,[3,[4,[5,6,7]]]],8,9]") shouldBe listOf(
            1,
            listOf(2, listOf(3, listOf(4, listOf(5, 6, 7)))),
            8,
            9
        )

        Day13().parseItems("[[4,4],4,4]") shouldBe listOf(listOf(4, 4), 4, 4)
        Day13().parseItems("[[4,4],4,4,4]") shouldBe listOf(listOf(4, 4), 4, 4, 4)

        Day13().parseItems("[]") shouldBe emptyList<Any>()
        Day13().parseItems("[[[]]]") shouldBe listOf(listOf(emptyList<Any>()))

        Day13().parseItems("[[2]]") shouldBe listOf(listOf(2))
        Day13().parseItems("[[[2]]]") shouldBe listOf(listOf(listOf(2)))
    }

    @Test
    fun `In correct order`() {
        Day13().inCorrectOrder(listOf("[1,1,3,1,1]", "[1,1,5,1,1]")) shouldBe true
        Day13().inCorrectOrder(listOf("[[1],[2,3,4]]", "[[1],4]")) shouldBe true
        Day13().inCorrectOrder(listOf("[9]", "[[8,7,6]]")) shouldBe false
        Day13().inCorrectOrder(listOf("[[4,4],4,4]", "[[4,4],4,4,4]")) shouldBe true
        Day13().inCorrectOrder(listOf("[7,7,7,7]", "[7,7,7]")) shouldBe false
        Day13().inCorrectOrder(listOf("[]", "[3]")) shouldBe true
        Day13().inCorrectOrder(listOf("[[[]]]", "[[]]")) shouldBe false
        Day13().inCorrectOrder(listOf("[1,[2,[3,[4,[5,6,7]]]],8,9]", "[1,[2,[3,[4,[5,6,0]]]],8,9]")) shouldBe false
    }
}