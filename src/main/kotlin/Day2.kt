package checinski.adam

import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import java.io.File
import kotlin.math.abs

fun day2part1(input: List<List<Int>>): Int = input.count { isSafe(it, allowedErrors = 0) }

fun day2part2(input: List<List<Int>>): Int = input.count { isSafe(it) }

private fun isSafe(input: List<Int>, allowedErrors: Int = 1): Boolean {
    return isSafeWithCondition(input, allowedErrors, ::areDecreasingNumbers) ||
           isSafeWithCondition(input, allowedErrors, ::areIncreasingNumbers)
}

private fun isSafeWithCondition(input: List<Int>, allowedErrors: Int, condition: (Int, Int) -> Boolean): Boolean {
    if (allowedErrors == 0) {
        return input.zipWithNext().all { (a, b) -> condition(a, b) }
    }

    val indexOfError = input.zipWithNext().indexOfFirst { (a, b) -> !condition(a, b) }
    if (indexOfError == -1) return true

    val lists = listOf(
        input.toMutableList().apply { removeAt(indexOfError) },
        input.toMutableList().apply { removeAt(indexOfError + 1) }
    )

    return lists.any { isSafeWithCondition(it, allowedErrors - 1, condition) }
}

fun areIncreasingNumbers(a: Int, b: Int) = a < b && abs(a - b) in 1..3
fun areDecreasingNumbers(a: Int, b: Int) = a > b && abs(a - b) in 1..3

class Day2Test {
    private val inputFile = File(javaClass.classLoader.getResource("day2input")?.file ?: error("Input file not found"))
    private val part1Answer = 236
    private val part2Answer = 308
    private val part2exampleAnswer = 4

    @Test
    fun `part 1 answer check`() {
        val result = day2part1(inputFile.readLines().map { it.split("\\s+".toRegex()).map(String::toInt)})

        expectThat(result).isEqualTo(part1Answer)
    }

    @Test
    fun `part 2 answer check`() {
        val result = day2part2(inputFile.readLines().map { it.split("\\s+".toRegex()).map(String::toInt)})

        expectThat(result).isEqualTo(part2Answer)
    }

    @Test
    fun `part 2 example answer check`() {
        val result = day2part2(
            listOf(
                listOf(7, 6, 4, 2, 1),
                listOf(1, 2, 7, 8, 9),
                listOf(9, 7, 6, 2, 1),
                listOf(1, 3, 2, 4, 5),
                listOf(8, 6, 4, 4, 1),
                listOf(1, 3, 6, 7, 9),
            )
        )

        expectThat(result).isEqualTo(part2exampleAnswer)
    }
}
