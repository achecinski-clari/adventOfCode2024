package checinski.adam

import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import java.io.File

class Plot(val label: Char, val x: Int, val y: Int, var adjacentPlots: List<Plot> = emptyList()) {
    fun addAdjacentPlot(plot: Plot) {
        adjacentPlots += plot
    }
}


fun day12part1(input: List<String>): Int {
    val plots = mutableListOf<MutableList<Plot>>()
    input.forEachIndexed { y, line ->
        val list = mutableListOf<Plot>().also { plots.add(it) }
        line.forEachIndexed { x, c ->
            val currentPlot = Plot(c, x, y)
            list.getOrNull(x - 1)?.let {
                if (it.label == c) {
                    currentPlot.addAdjacentPlot(it)
                    it.addAdjacentPlot(currentPlot)
                }
            }
            plots.getOrNull(y - 1)?.getOrNull(x)?.let {
                if (it.label == c) {
                    currentPlot.addAdjacentPlot(it)
                    it.addAdjacentPlot(currentPlot)
                }
            }
            list.add(currentPlot)
        }
    }




    val visited = mutableSetOf<Plot>()
    var sum = 0

    plots.forEach { row ->
        row.forEach { plot ->
            if (visited.add(plot)) {
//                println("Visiting plot ${plot.x}, ${plot.y}")
                var area = 0
                var perimeter = 0
                val queue = ArrayDeque<Plot>().also { it.addLast(plot) }

                while (queue.isNotEmpty()) {
                    val current = queue.removeFirst()
                    area++
                    perimeter += 4 - current.adjacentPlots.size
                    current.adjacentPlots.forEach {
                        if (visited.add(it)) {
                            queue.addLast(it)
                        }
                    }
                }

//                println("Area: $area, Perimeter: $perimeter")

                sum += area * perimeter
            }

        }
    }



    return sum
}
fun day12part2(input: List<String>): Int {
    return 0
}

class Day12Test {
    private val inputFile = File(javaClass.classLoader.getResource("day12input").file)
    private val exampleInputFile = File(javaClass.classLoader.getResource("day12exampleinput").file)
    private val part1ExampleAnswer = 1930
    private val part2ExampleAnswer = 0
    private val part1Answer = 1550156
    private val part2Answer = 0

    @Test
    fun `part 1 example answer check`() = runTest {
        val result = day12part1(exampleInputFile.readLines())
        expectThat(result).isEqualTo(part1ExampleAnswer)
    }

    @Test
    fun `part 1 answer check`() = runTest {
        val result = day12part1(inputFile.readLines())
        expectThat(result).isEqualTo(part1Answer)
    }

    @Test
    fun `part 2 answer check`() = runTest {
        val result = day12part2(inputFile.readLines())
        expectThat(result).isEqualTo(part2Answer)
    }

    @Test
    fun `part 2 example answer check`() = runTest {
        val result = day12part2(exampleInputFile.readLines())
        expectThat(result).isEqualTo(part2ExampleAnswer)
    }
}