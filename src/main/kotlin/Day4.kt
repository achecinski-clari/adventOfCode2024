package checinski.adam

import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import java.io.File

class PatterMatching2D(
    private val patterns: List<List<String>>,
    private val text: List<String>,
    private val wildCardCharacter: Char = '*'
) {
    private val textRows = text.size
    private val textCols = text[0].length

    private val mod = 1000000013599L
    private val base = 131L

    private val patternsHashes = patterns.map { it.map { line -> computePatternHash(line) } }

    fun findMatches() = patterns.indices.sumOf { findMatchesForPattern(it) }

    private fun findMatchesForPattern(patternIndex: Int): Int {
        val pattern = patterns[patternIndex]
        val patternHashes = patternsHashes[patternIndex]
        val patternRows = pattern.size
        val patternCols = pattern[0].length

        return (0..(textRows - patternRows)).sumOf { row ->
            (0..(textCols - patternCols)).count { col ->
                (0 until patternRows).all { i ->
                    doesLineMatch(patternHashes[i], text[row + i], col)
                }
            }
        }
    }

    private fun computeLineHashes(line: String): Pair<LongArray, LongArray> {
        val length = line.length
        val prefixHashes = LongArray(length + 1) { 0L }
        val powerValues = LongArray(length + 1) { 1L }
        for (i in line.indices) {
            prefixHashes[i + 1] = (prefixHashes[i] * base + line[i].code.toLong()) % mod
            powerValues[i + 1] = (powerValues[i] * base) % mod
        }
        return prefixHashes to powerValues
    }

    private fun computeHash(
        prefixHashes: LongArray,
        powerValues: LongArray,
        start: Int,
        end: Int
    ): Long {
        val len = end - start + 1
        val hashVal = prefixHashes[end + 1] - (prefixHashes[start] * powerValues[len] % mod)
        return (hashVal % mod + mod) % mod
    }

    private fun computePatternHash(patternLine: String): PatternHash {
        val nonWildcardChars = StringBuilder()
        val indices = mutableListOf<Int>()
        for ((i, ch) in patternLine.withIndex()) {
            if (ch != wildCardCharacter) {
                nonWildcardChars.append(ch)
                indices.add(i)
            }
        }

        val (prefixHashes, powerValues) = computeLineHashes(nonWildcardChars.toString())
        return PatternHash(prefixHashes, powerValues, indices, patternLine.length)
    }

    private fun doesLineMatch(
        patternHash: PatternHash,
        textLine: String,
        startCol: Int
    ): Boolean {
        val endCol = startCol + patternHash.length - 1
        if (endCol >= textLine.length) return false

        val extractedChars = StringBuilder()
        for (i in patternHash.nonWildcardIndices) {
            val textPos = startCol + i
            extractedChars.append(textLine[textPos])
        }

        if (extractedChars.isEmpty()) return true

        val (tempPrefix, tempPower) = computeLineHashes(extractedChars.toString())
        val extractedHash = computeHash(tempPrefix, tempPower, 0, extractedChars.length - 1)

        val hash = computeHash(patternHash.prefixHashes, patternHash.powerValues, 0, extractedChars.length - 1)
        return extractedHash == hash
    }

    private data class PatternHash(
        val prefixHashes: LongArray,
        val powerValues: LongArray,
        val nonWildcardIndices: List<Int>,
        val length: Int
    )
}

private fun day4part2(text: List<String>): Int {
    return PatterMatching2D(
        listOf(
            listOf(
                "M*M",
                "*A*",
                "S*S",
            ),
            listOf(
                "S*M",
                "*A*",
                "S*M",
            ),
            listOf(
                "S*S",
                "*A*",
                "M*M",
            ),
            listOf(
                "M*S",
                "*A*",
                "M*S",
            ),
        ),
        text
    ).findMatches()
}


private fun day4part1(text: List<String>): Int {
    return PatterMatching2D(
        listOf(
            listOf("XMAS"),
            listOf("SAMX"),
            listOf("X***",
                   "*M**",
                   "**A*",
                   "***S",
            ),
            listOf("S***",
                   "*A**",
                   "**M*",
                   "***X",
            ),
            listOf("***S",
                   "**A*",
                   "*M**",
                   "X***",
            ),
            listOf("***X",
                   "**M*",
                   "*A**",
                   "S***",
            ),
            listOf("X",
                   "M",
                   "A",
                   "S",
            ),
            listOf("S",
                   "A",
                   "M",
                   "X",
            ),
        ),
        text
    ).findMatches()
}

class Day4Test {
    private val inputFile = File(javaClass.classLoader.getResource("day4input").file)
    private val part1Answer = 2639
    private val part2Answer = 2005
    private val part1ExampleAnswer = 18

    @Test
    fun `part 1 answer check`() = runTest {
        val result = day4part1(inputFile.readLines())

        expectThat(result).isEqualTo(part1Answer)
    }

    @Test
    fun `part 2 answer check`() = runTest {
        val result = day4part2(inputFile.readLines())

        expectThat(result).isEqualTo(part2Answer)
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