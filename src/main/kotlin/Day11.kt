import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import java.io.File

fun transformStone(stone: Stone): List<Stone> {
    if (stone.value == 0L) {
        return listOf(Stone(1))
    } else if (countDigits(stone) % 2 == 0) {
        val stringValue = stone.value.toString()
        try {
            return listOf(
                Stone(stringValue.take(stringValue.length / 2).toLong()),
                Stone(stringValue.takeLast(stringValue.length / 2).toLong())
            )
        } catch (e: Exception) {
            println("Error :((")
            throw e
        }
    } else {
        return listOf(Stone(stone.value * 2024))
    }
}

fun countDigits(stone: Stone): Int {
    return stone.value.toString().length
}

data class Stone(val value: Long)

fun day11part1(input: String): Int {
    val stones = input.split(" ").map { Stone(it.toLong()) }
    stones.forEach { println(it) }
    val stonesAfterBlinks = blink(stones, ::transformStone, 25)
    return stonesAfterBlinks.size
}

fun day11part2(input: String): Int {
    val stones = input.split(" ").map { Stone(it.toLong()) }
    stones.forEach { println(it) }
    val stonesAfterBlinks = blink(stones, ::transformStone, 75)
    return stonesAfterBlinks.size
}

fun blink(stones: List<Stone>, transform: Stone.() -> List<Stone>, i: Int): List<Stone> {
    var tempStones = stones
    for (j in 0 until i) {
        val newTempStones = mutableListOf<Stone>()
        tempStones.forEach {
            newTempStones.addAll(it.transform())
        }
        tempStones = newTempStones
    }
    return tempStones
}


class DayXTest {
    private val inputFile = File(javaClass.classLoader.getResource("day11input").file)
    private val exampleInputFile = File(javaClass.classLoader.getResource("day11exampleinput").file)
    private val part1ExampleAnswer = 55312
    private val part2ExampleAnswer = 0
    private val part1Answer = 194557
    private val part2Answer = 0

    @Test
    fun `part 1 example answer check`() = runTest {
        val result = day11part1(exampleInputFile.readText())
        expectThat(result).isEqualTo(part1ExampleAnswer)
    }

    @Test
    fun `part 1 answer check`() = runTest {
        val result = day11part1(inputFile.readText())
        expectThat(result).isEqualTo(part1Answer)
    }

    @Test
    fun `part 2 answer check`() = runTest {
        val result = day11part2(inputFile.readText())
        expectThat(result).isEqualTo(part2Answer)
    }

    @Test
    fun `part 2 example answer check`() = runTest {
        val result = day11part2(exampleInputFile.readText())
        expectThat(result).isEqualTo(part2ExampleAnswer)
    }
}