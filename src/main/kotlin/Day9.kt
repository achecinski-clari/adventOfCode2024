package checinski.adam

import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import java.io.File



data class Block(val value: Long?, val repeat: Int = 0)

fun day9part1(input: String): Long {
    var individualBlocks = mutableListOf<Block>()
    var i = 0L
    input.forEachIndexed { index, c ->
        val blocks = c.code - 48

        if (index % 2 == 0) {
            repeat(blocks) {
                individualBlocks += Block(i)
            }
            i++
        } else {
            repeat(blocks) {
                individualBlocks += Block(null)
            }
        }
    }

    var rightIndex = individualBlocks.lastIndex
    var leftIndex = 0

    while (leftIndex < rightIndex) {
        while (individualBlocks[leftIndex].value != null) {
            leftIndex++
        }
        while (individualBlocks[rightIndex].value == null) {
            rightIndex--
        }
        if (leftIndex < rightIndex) {
            individualBlocks = swap(individualBlocks, leftIndex, rightIndex)
            leftIndex++
            rightIndex--
        }
    }
    return individualBlocks.takeWhile { it.value != null }.mapIndexed { index, c -> index * (c.value ?: 0) }.sum()
}


fun day9part2(input: String): Long {
    var individualBlocks = mutableListOf<Block>()
    var i = 0L
    input.forEachIndexed { index, c ->
        val blocks = c.code - 48

        if (index % 2 == 0) {
            individualBlocks += Block(i, blocks)
            i++
        } else {
            if (blocks != 0) {
                individualBlocks += Block(null, blocks)
            }
        }
    }

    var rightIndex = individualBlocks.lastIndex
    var leftIndex = 0

    while (leftIndex < rightIndex) {
        while (individualBlocks[leftIndex].value != null) {
            leftIndex++
        }
        while (individualBlocks[rightIndex].value == null) {
            rightIndex--
        }
        if (leftIndex < rightIndex) {
            if (individualBlocks[leftIndex].repeat == individualBlocks[rightIndex].repeat) {
                individualBlocks = swap(individualBlocks, leftIndex, rightIndex)
                leftIndex = 0
                rightIndex--

            } else if (individualBlocks[leftIndex].repeat < individualBlocks[rightIndex].repeat) {
                leftIndex++
            } else {
                val diff = individualBlocks[leftIndex].repeat - individualBlocks[rightIndex].repeat
                individualBlocks[leftIndex] = individualBlocks[rightIndex]
                individualBlocks[rightIndex] = Block(null, individualBlocks[rightIndex].repeat)
                individualBlocks.add(leftIndex + 1, Block(null, diff))
                leftIndex = 0
                rightIndex++
            }
        }

        if (leftIndex >= rightIndex) {
            rightIndex--
            leftIndex = 0
        }
    }
    return individualBlocks.unpair().mapIndexed { index, c -> index * (c.value ?: 0) }.sum()
}

private fun List<Block>.unpair(): List<Block> {
    val result = mutableListOf<Block>()
    this.forEach { block ->
        if (block.repeat != 0) {
            repeat(block.repeat) {
                result.add(Block(block.value))
            }
        }
    }
    return result

}

fun swap(individualBlocks: MutableList<Block>, leftIndex: Int, rightIndex: Int): MutableList<Block> {
    val temp = individualBlocks[leftIndex]
    individualBlocks[leftIndex] = individualBlocks[rightIndex]
    individualBlocks[rightIndex] = temp
    return individualBlocks
}

class Day9Test {
    private val inputFile = File(javaClass.classLoader.getResource("day9input").file)
    private val exampleInputFile = File(javaClass.classLoader.getResource("day9exampleinput").file)
    private val part1ExampleAnswer = 1928L
    private val part2ExampleAnswer = 2858L
    private val part1Answer = 88217448737L //too low
    private val part2Answer = 0L

    @Test
    fun `part 1 example answer check`() = runTest {
        val result = day9part1(exampleInputFile.readText())
        expectThat(result).isEqualTo(part1ExampleAnswer)
    }

    @Test
    fun `part 1 answer check`() = runTest {
        val result = day9part1(inputFile.readText())
        expectThat(result).isEqualTo(part1Answer)
    }

    @Test
    fun `part 2 answer check`() = runTest {
        val result = day9part2(inputFile.readText())
        expectThat(result).isEqualTo(part2Answer)
    }

    @Test
    fun `part 2 example answer check`() = runTest {
        val result = day9part2(exampleInputFile.readText())
        expectThat(result).isEqualTo(part2ExampleAnswer)
    }
}