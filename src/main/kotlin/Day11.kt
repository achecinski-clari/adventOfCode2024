import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import java.io.File

private val transformCache = mutableMapOf<Long, List<Long>>()

fun transformStone(stone: Long): List<Long> {
    if (stone == 0L) {
        return listOf(1)
    } else if (countDigits(stone) % 2 == 0) {
        val stringValue = stone.toString()
        try {
            return listOf(
                stringValue.take(stringValue.length / 2).toLong(),
                stringValue.takeLast(stringValue.length / 2).toLong()
            )
        } catch (e: Exception) {
            println("Error :((")
            throw e
        }
    } else {
        return listOf(stone * 2024)
    }
}

fun countDigits(stone: Long): Int {
    return stone.toString().length
}

fun day11part1(input: String): Long {
    val stoneValueToCount = input.split(" ")
        .map { it.toLong() }
        .groupingBy { it }
        .eachCount()
        .mapValues { it.value.toLong() }.toMutableMap()
    val stonesAfterBlinks = blink(stoneValueToCount, ::transformStone, 25)
    return stonesAfterBlinks.values.sum()
}

fun day11part2(input: String): Long {
    val stoneValueToCount = input.split(" ")
        .map { it.toLong() }
        .groupingBy { it }
        .eachCount()
        .mapValues { it.value.toLong() }.toMutableMap()
    val stonesAfterBlinks = blink(stoneValueToCount, ::transformStone, 75)
    return stonesAfterBlinks.values.sum()}

fun blink(stones: MutableMap<Long, Long>, transform: Long.() -> List<Long>, i: Int): MutableMap<Long, Long> {
    var tempStones = stones
    for (j in 0 until i) {
        val newTempStones = mutableMapOf<Long, Long>()
        tempStones.forEach {
            val cachedTransform = transformCache[it.key]
            val transformed = if (cachedTransform != null) {
                cachedTransform
            } else {
                val newTransform = it.key.transform()
                transformCache[it.key] = newTransform
                newTransform
            }

            transformed.forEach { stone ->
                newTempStones[stone] = newTempStones.getOrDefault(stone, 0) + it.value
            }
        }
        tempStones = newTempStones
    }
    return tempStones
}


class DayXTest {
    private val inputFile = File(javaClass.classLoader.getResource("day11input").file)
    private val exampleInputFile = File(javaClass.classLoader.getResource("day11exampleinput").file)
    private val part1ExampleAnswer = 55312L
    private val part2ExampleAnswer = 0L
    private val part1Answer = 194557L
    private val part2Answer = 231532558973909L

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