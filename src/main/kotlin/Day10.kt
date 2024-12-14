package checinski.adam

import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import java.io.File

data class PositionOnTopographicMap(val x: Int, val y: Int, val value: Int)
data class PositionOnTopographicMapWithTrail(val position: PositionOnTopographicMap, val trail: List<PositionOnTopographicMap>)

fun parseTopographicMap(input: List<String>): List<PositionOnTopographicMap> =
    input.flatMapIndexed { y, line ->
        line.mapIndexed { x, value ->
            PositionOnTopographicMap(x, y, value.toString().toInt())
        }
    }

fun findAdjacentPositions(
    position: PositionOnTopographicMap,
    input: List<String>
): List<PositionOnTopographicMap> {
    val (x, y, value) = position
    val adjacentCoordinates = listOf(
        x + 1 to y,
        x - 1 to y,
        x to y + 1,
        x to y - 1
    ).filter { (adjX, adjY) -> adjX in input[0].indices && adjY in input.indices }

    return adjacentCoordinates.mapNotNull { (adjX, adjY) ->
        val adjacentValue = input[adjY][adjX].toString().toInt()
        if (adjacentValue == value + 1) {
            PositionOnTopographicMap(adjX, adjY, adjacentValue)
        } else null
    }
}

fun findEndPositions(
    startPosition: PositionOnTopographicMap,
    adjacencyMap: Map<PositionOnTopographicMap, List<PositionOnTopographicMap>>
): Set<PositionOnTopographicMap> {
    val possibleEndPositions = mutableSetOf<PositionOnTopographicMap>()
    val deque = ArrayDeque<PositionOnTopographicMap>()
    deque.add(startPosition)

    while (deque.isNotEmpty()) {
        val currentPosition = deque.removeFirst()
        if (currentPosition.value == 9) {
            possibleEndPositions.add(currentPosition)
        }
        adjacencyMap[currentPosition]?.forEach { adjacent ->
            deque.addLast(adjacent)
        }
    }

    return possibleEndPositions
}

fun day10part1(input: List<String>): Int {
    val positions = parseTopographicMap(input)
    val adjacencyMap = positions.associateWith { position ->
        findAdjacentPositions(position, input)
    }

    val startPositions = positions.filter { it.value == 0 }

    val startPositionToEndPositions = startPositions.map { startPosition ->
        startPosition to findEndPositions(startPosition, adjacencyMap)
    }

    return startPositionToEndPositions.flatMap { it.second }.size
}

fun day10part2(input: List<String>): Int {
    return 0
}

class Day10Test {
    private val inputFile = File(javaClass.classLoader.getResource("day10input").file)
    private val exampleInputFile = File(javaClass.classLoader.getResource("day10exampleinput").file)
    private val part1ExampleAnswer = 36
    private val part2ExampleAnswer = 81
    private val part1Answer = 688
    private val part2Answer = 0

    @Test
    fun `part 1 example answer check`() = runTest {
        val result = day10part1(exampleInputFile.readLines())
        expectThat(result).isEqualTo(part1ExampleAnswer)
    }

    @Test
    fun `part 1 answer check`() = runTest {
        val result = day10part1(inputFile.readLines())
        expectThat(result).isEqualTo(part1Answer)
    }

    @Test
    fun `part 2 answer check`() = runTest {
        val result = day10part2(inputFile.readLines())
        expectThat(result).isEqualTo(part2Answer)
    }

    @Test
    fun `part 2 example answer check`() = runTest {
        val result = day10part2(exampleInputFile.readLines())
        expectThat(result).isEqualTo(part2ExampleAnswer)
    }
}