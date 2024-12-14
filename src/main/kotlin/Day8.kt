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
    fun findAntiNodesWith(position: Position): Pair<AntiNodePosition, AntiNodePosition> {
        val (deltaX, deltaY) = findPositionsDelta(position)
        val antiNode1 = AntiNodePosition(Position(x - deltaX, y - deltaY))
        val antiNode2 = AntiNodePosition(Position(position.x + deltaX, position.y + deltaY))
        return Pair(antiNode1, antiNode2)
    }
    fun findResonantHarmoniesAntiNodesWith(position: Position, indicesY: IntRange, indicesX: IntRange): Set<AntiNodePosition> {
        val antiNodes = mutableSetOf(AntiNodePosition(this))
        antiNodes += AntiNodePosition(position)

        var (deltaX, deltaY) = findPositionsDelta(position)
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

    private fun findPositionsDelta(position: Position): Pair<Int, Int> {
        return Pair(position.x - x, position.y - y)
    }
}

fun day8part1(input: List<String>): Int {
    val frequencyToPositions: Map<Frequency, List<Position>> = findPositionsOfAntennasWithSameFrequency(input)
    val uniqueAntiNodes = mutableSetOf<AntiNodePosition>()

    frequencyToPositions.forEach { (frequency, positions) ->
        val pairs = mutableListOf<Pair<Position, Position>>()

        for (i in positions.indices) {
            for (j in i + 1 until positions.size) {
                val position1 = positions[i]
                val position2 = positions[j]
                pairs.add(position1 to position2)
            }
        }

        pairs.forEach { (position1, position2) ->
            val (antiNode1, antiNode2) = position1.findAntiNodesWith(position2)
            uniqueAntiNodes += antiNode1
            uniqueAntiNodes += antiNode2
        }
    }
    return uniqueAntiNodes.filter { it.value.x in input[0].indices && it.value.y in input.indices }.size
}

fun day8part2(input: List<String>): Int {
    val frequencyToPositions: Map<Frequency, List<Position>> = findPositionsOfAntennasWithSameFrequency(input)
    val uniqueAntiNodes = mutableSetOf<AntiNodePosition>()

    frequencyToPositions.forEach { (frequency, positions) ->
        val pairs = mutableListOf<Pair<Position, Position>>()

        for (i in positions.indices) {
            for (j in i + 1 until positions.size) {
                val position1 = positions[i]
                val position2 = positions[j]
                pairs.add(position1 to position2)
            }
        }

        pairs.forEach { (position1, position2) ->
            val antiNodes = position1.findResonantHarmoniesAntiNodesWith(position2, input.indices, input[0].indices)
            uniqueAntiNodes += antiNodes
        }
    }
    return uniqueAntiNodes.filter { it.value.x in input[0].indices && it.value.y in input.indices }.size
}

fun findPositionsOfAntennasWithSameFrequency(input: List<String>): Map<Frequency, List<Position>> {
    val frequencyToPositions = mutableMapOf<Frequency, List<Position>>()
    for (y in input.indices) {
        for (x in input[y].indices) {
            if (input[y][x] in '0'..'9' ||
                input[y][x] in 'A'..'Z' ||
                input[y][x] in 'a'..'z') {
                val frequency = Frequency(input[y][x])
                val position = Position(x, y)
                frequencyToPositions[frequency] = frequencyToPositions.getOrDefault(frequency, emptyList()) + position
            }
        }
    }
    return frequencyToPositions
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