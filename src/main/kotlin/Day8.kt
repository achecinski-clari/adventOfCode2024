package checinski.adam

import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import java.io.File

@JvmInline
value class Frequency(val value: Char)
@JvmInline
value class AntiNodePosition(val value: Position)

data class Position(val x: Int, val y: Int) {
    fun findAntiNodesWith(other: Position): Pair<AntiNodePosition, AntiNodePosition> {
        val (deltaX, deltaY) = deltaTo(other)
        val antiNode1 = AntiNodePosition(Position(x - deltaX, y - deltaY))
        val antiNode2 = AntiNodePosition(Position(other.x + deltaX, other.y + deltaY))
        return antiNode1 to antiNode2
    }

    fun findResonantHarmoniesAntiNodesWith(
        other: Position,
        indicesY: IntRange,
        indicesX: IntRange
    ): Set<AntiNodePosition> {
        val antiNodes = mutableSetOf(AntiNodePosition(this), AntiNodePosition(other))
        val (deltaX, deltaY) = deltaTo(other)

        var tempX = x
        var tempY = y
        while (tempX in indicesX && tempY in indicesY) {
            antiNodes += AntiNodePosition(Position(tempX, tempY))
            tempX -= deltaX
            tempY -= deltaY
        }

        tempX = x
        tempY = y
        while (tempX in indicesX && tempY in indicesY) {
            antiNodes += AntiNodePosition(Position(tempX, tempY))
            tempX += deltaX
            tempY += deltaY
        }

        return antiNodes
    }

    private fun deltaTo(other: Position): Pair<Int, Int> = (other.x - x) to (other.y - y)
}

fun day8part1(input: List<String>): Int {
    return solveDay8(input) { pos1, pos2, _, _ ->
        val (antiNode1, antiNode2) = pos1.findAntiNodesWith(pos2)
        setOf(antiNode1, antiNode2)
    }
}

fun day8part2(input: List<String>): Int {
    return solveDay8(input) { pos1, pos2, yRange, xRange ->
        pos1.findResonantHarmoniesAntiNodesWith(pos2, yRange, xRange)
    }
}

fun solveDay8(
    input: List<String>,
    antiNodeCalculator: (Position, Position, IntRange, IntRange) -> Set<AntiNodePosition>
): Int {
    val frequencyToPositions = findPositionsOfAntennasWithSameFrequency(input)
    val xRange = input[0].indices
    val yRange = input.indices

    val uniqueAntiNodes = frequencyToPositions.values
        .asSequence()
        .flatMap { it.allPairs() }
        .flatMap { (pos1, pos2) ->
            antiNodeCalculator(pos1, pos2, yRange, xRange)
        }
        .filter { it.value.x in xRange && it.value.y in yRange }
        .toSet()

    return uniqueAntiNodes.size
}

fun findPositionsOfAntennasWithSameFrequency(input: List<String>): Map<Frequency, List<Position>> {
    return input.flatMapIndexed { y, row ->
        row.mapIndexedNotNull { x, ch ->
            if (ch.isLetterOrDigit()) Frequency(ch) to Position(x, y) else null
        }
    }.groupBy({ it.first }, { it.second })
}

private fun List<Position>.allPairs(): Sequence<Pair<Position, Position>> = sequence {
    for (i in indices) {
        for (j in i + 1 until size) {
            yield(get(i) to get(j))
        }
    }
}

class Day8Test {
    private val inputFile = File(javaClass.classLoader.getResource("day8input").file)
    private val exampleInputFile = File(javaClass.classLoader.getResource("day8exampleInput").file)
    private val part1ExampleAnswer = 14
    private val part2ExampleAnswer = 34
    private val part1Answer = 361
    private val part2Answer = 1249

    @Test
    fun `part 1 example answer check`() = runTest {
        val result = day8part1(exampleInputFile.readLines())
        expectThat(result).isEqualTo(part1ExampleAnswer)
    }

    @Test
    fun `part 1 answer check`() = runTest {
        val result = day8part1(inputFile.readLines())
        expectThat(result).isEqualTo(part1Answer)
    }

    @Test
    fun `part 2 answer check`() = runTest {
        val result = day8part2(inputFile.readLines())
        expectThat(result).isEqualTo(part2Answer)
    }

    @Test
    fun `part 2 example answer check`() = runTest {
        val result = day8part2(exampleInputFile.readLines())
        expectThat(result).isEqualTo(part2ExampleAnswer)
    }

    @Test
    fun `when findAntiNodesWith given two positions then antinodes found`() {
        // given
        val position1 = Position(4, 3)
        val position2 = Position(5, 5)

        // when
        val (antiNode1, antiNode2) = position1.findAntiNodesWith(position2)

        // then
        expectThat(antiNode1).isEqualTo(AntiNodePosition(Position(3, 1)))
        expectThat(antiNode2).isEqualTo(AntiNodePosition(Position(6, 7)))

    }
}