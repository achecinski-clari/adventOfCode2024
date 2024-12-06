package checinski.adam

import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import java.io.File
import kotlin.math.abs

fun day1Part1(input: List<String>): Int {
    val (leftList, rightList) = input.map {
        val (left, right) = it.split("\\s+".toRegex()).map(String::toInt)
        Pair(left, right)
    }.unzip()

    val sortedLeftList = leftList.sorted()
    val sortedRightList = rightList.sorted()

    return sortedLeftList.zip(sortedRightList).sumOf { (left, right) -> abs(left - right) }
}

fun day1part2(input: List<String>): Int {
    val leftList = mutableListOf<Int>()
    val rightNumberToCount = mutableMapOf<Int, Int>()

    input.forEach {
        val (left, right) = it.split("\\s+".toRegex()).map(String::toInt)
        leftList.add(left)
        rightNumberToCount[right] = rightNumberToCount.getOrDefault(right, 0) + 1
    }

    return leftList.sumOf { left -> left * rightNumberToCount.getOrDefault(left, 0) }
}


class Day1Test {
    private val inputFile = File(javaClass.classLoader.getResource("day1input").file)
    private val part1Answer = 2031679
    private val part2Answer = 19678534
    @Test
    fun `part 1 answer check`() {
        val result = day1Part1(inputFile.readLines())

        expectThat(result).isEqualTo(part1Answer)
    }
    @Test
    fun `part 2 answer check`() {
        val result = day1part2(inputFile.readLines())

        expectThat(result).isEqualTo(part2Answer)
    }
}