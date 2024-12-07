package checinski.adam

import kotlinx.coroutines.*
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import java.io.File

@JvmInline
value class Row(val row: Int)

@JvmInline
value class Column(val column: Int)

enum class Directions(val rowDirection: Int, val columnDirection: Int) {
    UP(-1, 0),
    DOWN(1, 0),
    LEFT(0, -1),
    RIGHT(0, 1),
    UP_LEFT(-1, -1),
    UP_RIGHT(-1, 1),
    DOWN_LEFT(1, -1),
    DOWN_RIGHT(1, 1),
    NOT_SPECIFIED(0, 0);

    operator fun component1(): Int = rowDirection
    operator fun component2(): Int = columnDirection
}

private const val WORD_TO_FIND = "XMAS"
private const val INITIAL_CHARS_FOUND = 1

data class Occurrence(
    val row: Row,
    val column: Column,
    val direction: Directions = Directions.NOT_SPECIFIED,
    val charsFound: Int = INITIAL_CHARS_FOUND,
    val wordToFind: String = WORD_TO_FIND,
) {
    fun isWordFound(): Boolean = charsFound == wordToFind.length
}

private sealed class FoundResult {
    data class Found(val occurrence: Occurrence) : FoundResult()
    data class Next(val occurrence: Occurrence) : FoundResult()
    object NotFound : FoundResult()
}

suspend fun day4part1(input: List<String>): Int = coroutineScope {
    val initialOccurrences = findXOccurrences(input)
    val directions = Directions.entries.filter { it != Directions.NOT_SPECIFIED }

    val occurrencesWithDirections = initialOccurrences.flatMap { occurrence ->
        directions.map { occurrence.copy(direction = it) }
    }

    val foundWords = mutableSetOf<Occurrence>()
    val queue = ArrayDeque(occurrencesWithDirections)

    while (queue.isNotEmpty()) {
        val currentBatch = queue.toList()
        queue.clear()

        val nextResults = currentBatch.map { occurrence ->
            async { processOccurrence(input, occurrence) }
        }.awaitAll()

        nextResults.forEach { result ->
            when (result) {
                is FoundResult.Found -> foundWords += result.occurrence
                is FoundResult.Next -> queue += result.occurrence
                FoundResult.NotFound -> Unit
            }
        }
    }

    foundWords.size
}

private fun processOccurrence(input: List<String>, occurrence: Occurrence): FoundResult {
    return when {
        occurrence.isWordFound() -> FoundResult.Found(occurrence)
        else -> {
            val (rowDir, colDir) = occurrence.direction
            val nextRow = occurrence.row.row + rowDir
            val nextColumn = occurrence.column.column + colDir

            if (nextRow !in input.indices || nextColumn !in input[nextRow].indices) {
                FoundResult.NotFound
            } else {
                val nextChar = input[nextRow][nextColumn]
                val nextExpectedChar = occurrence.wordToFind[occurrence.charsFound]
                if (nextChar == nextExpectedChar) {
                    FoundResult.Next(
                        occurrence.copy(
                            row = Row(nextRow),
                            column = Column(nextColumn),
                            charsFound = occurrence.charsFound + 1
                        )
                    )
                } else {
                    FoundResult.NotFound
                }
            }
        }
    }
}

fun findXOccurrences(input: List<String>): Set<Occurrence> {
    return input.flatMapIndexed { rowIndex, row ->
        row.mapIndexedNotNull { colIndex, char ->
            if (char == 'X') Occurrence(Row(rowIndex), Column(colIndex)) else null
        }
    }.toSet()
}


class Day4Test {
    private val inputFile = File(javaClass.classLoader.getResource("day4input").file)
    private val part1Answer = 2639
    private val part1ExampleAnswer = 18
    @Test
    fun `part 1 answer check`() = runTest {
        val result = day4part1(inputFile.readLines())

        expectThat(result).isEqualTo(part1Answer)
    }

    @Test
    fun `part 1 example answer check`() = runTest {
        val result = day4part1(listOf(
            "MMMSXXMASM",
            "MSAMXMSMSA",
            "AMXSXMAAMM",
            "MSAMASMSMX",
            "XMASAMXAMM",
            "XXAMMXXAMA",
            "SMSMSASXSS",
            "SAXAMASAAA",
            "MAMMMXMMMM",
            "MXMXAXMASX",
        ))

        expectThat(result).isEqualTo(part1ExampleAnswer)
    }
}