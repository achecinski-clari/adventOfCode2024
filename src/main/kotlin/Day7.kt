package checinski.adam

import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import java.io.File

fun day7part1(input: List<String>): Long {
    val operations: List<(Long, Long) -> Long> = listOf(
        { a, b -> a + b },
        { a, b -> a * b }
    )

    return recalibrateUsingOperations(input, operations)
}


fun day7part2(input: List<String>): Long {
    val operations: List<(Long, Long) -> Long> = listOf(
        { a, b -> a + b },
        { a, b -> a * b },
        { a, b -> (a.toString() + b.toString()).toLong() }
    )

    return recalibrateUsingOperations(input, operations)
}

private fun recalibrateUsingOperations(
    input: List<String>,
    operations: List<(Long, Long) -> Long>
): Long {
    val resultToNumbers = input.map { line ->
        val (value, numbers) = line.split(":").map { it.trim() }
        value.toLong() to numbers.split(" ").map { it.toLong() }
    }

    var sum = 0L
    for ((expectedResult, numbers) in resultToNumbers) {
        val result = calculateIterativeWithOps(numbers, expectedResult, operations)
        if (result == expectedResult) {
            sum += expectedResult
        }
    }
    return sum
}

fun calculateIterativeWithOps(numbers: List<Long>, expectedResult: Long, operations: List<(Long, Long) -> Long>): Long {
    if (numbers.size < 2) {
        return if (numbers.size == 1 && numbers[0] == expectedResult) expectedResult else 0
    }

    val first = numbers[0]
    val second = numbers[1]
    val remainder = numbers.drop(2)

    val stack = mutableListOf<Pair<Long, List<Long>>>()

    for (op in operations) {
        stack.add(op(first, second) to remainder)
    }

    while (stack.isNotEmpty()) {
        val (currentResult, remaining) = stack.removeAt(stack.size - 1)

        if (remaining.isEmpty()) {
            if (currentResult == expectedResult) {
                return expectedResult
            }
        } else {
            val next = remaining[0]
            val nextRemaining = remaining.drop(1)
            for (operation in operations) {
                stack.add(operation(currentResult, next) to nextRemaining)
            }
        }
    }

    return 0
}


class Day7Test {
    private val inputFile = File(javaClass.classLoader.getResource("day7input").file)
    private val exampleInputFile = File(javaClass.classLoader.getResource("day7exampleInput").file)
    private val part1ExampleAnswer = 3749L
    private val part2ExampleAnswer = 11387L
    private val part1Answer = 303876485655L
    private val part2Answer = 146111650210682L

    @Test
    fun `part 1 example answer check`() = runTest {
        val result = day7part1(exampleInputFile.readLines())
        expectThat(result).isEqualTo(part1ExampleAnswer)
    }

    @Test
    fun `part 1 answer check`() = runTest {
        val result = day7part1(inputFile.readLines())
        expectThat(result).isEqualTo(part1Answer)
    }

    @Test
    fun `part 2 answer check`() = runTest {
        val result = day7part2(inputFile.readLines())
        expectThat(result).isEqualTo(part2Answer)
    }

    @Test
    fun `part 2 example answer check`() = runTest {
        val result = day7part2(exampleInputFile.readLines())
        expectThat(result).isEqualTo(part2ExampleAnswer)
    }
}