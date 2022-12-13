import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class Day13Test {
    @Test
    fun `Should parse items`() {
        Day13().parsePacket("[1,1,3,1,1]") shouldBe listOf(1, 1, 3, 1, 1)
        Day13().parsePacket("[[1],[2,3,4]]") shouldBe listOf(listOf(1), listOf(2, 3, 4))
        Day13().parsePacket("[[8,7,6]]") shouldBe listOf(listOf(8, 7, 6))
        Day13().parsePacket("[[[8,7,6]]]") shouldBe listOf(listOf(listOf(8, 7, 6)))

        Day13().parsePacket("[1,[2,[3,[4,[5,6,7]]]],8,9]") shouldBe listOf(
            1,
            listOf(2, listOf(3, listOf(4, listOf(5, 6, 7)))),
            8,
            9
        )

        Day13().parsePacket("[[4,4],4,4]") shouldBe listOf(listOf(4, 4), 4, 4)
        Day13().parsePacket("[[4,4],4,4,4]") shouldBe listOf(listOf(4, 4), 4, 4, 4)

        Day13().parsePacket("[]") shouldBe emptyList<Any>()
        Day13().parsePacket("[[[]]]") shouldBe listOf(listOf(emptyList<Any>()))

        Day13().parsePacket("[[2]]") shouldBe listOf(listOf(2))
        Day13().parsePacket("[[[2]]]") shouldBe listOf(listOf(listOf(2)))
    }

    @Test
    fun `Should successfully parse all input lines`() {
        val items = readStringList("13").filter { it.isNotBlank() }
        items.forEach { item ->
            val parsed = Day13().parsePacket(item)
            parsed.toString().replace(" ", "") shouldBe item
        }
    }

    @Test
    fun `In correct order`() {
        inCorrectOrder("[1,1,3,1,1]", "[1,1,5,1,1]") shouldBe true
        inCorrectOrder("[[1],[2,3,4]]", "[[1],4]") shouldBe true
        inCorrectOrder("[9]", "[[8,7,6]]") shouldBe false
        inCorrectOrder("[[4,4],4,4]", "[[4,4],4,4,4]") shouldBe true
        inCorrectOrder("[7,7,7,7]", "[7,7,7]") shouldBe false
        inCorrectOrder("[]", "[3]") shouldBe true
        inCorrectOrder("[[[]]]", "[[]]") shouldBe false
        inCorrectOrder("[1,[2,[3,[4,[5,6,7]]]],8,9]", "[1,[2,[3,[4,[5,6,0]]]],8,9]") shouldBe false
    }

    private fun inCorrectOrder(packetStringOne: String, packetStringTwo: String): Boolean {
        val packetList = listOf(packetStringOne, packetStringTwo).map(Day13()::parsePacket)
        return Day13().inCorrectOrder(packetList)
    }
}