import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import java.io.File

fun day5part1(input: List<String>): Int {
    val delimiter = ""
    val index = input.indexOf(delimiter)

    val beforeRules = mutableMapOf<Int, List<Int>>()
    val afterRules = mutableMapOf<Int, List<Int>>()

    input.take(index).map { it.split('|') }.forEach {
        val before = it[0].toInt()
        val after = it[1].toInt()
        afterRules[before] = afterRules.getOrDefault(before, emptyList()) + after
        beforeRules[after] = beforeRules.getOrDefault(after, emptyList()) + before
    }

    val pagesToProduce = input.drop(index + 1).map { it.split(',').map { e -> e.toInt() }.toList() }

    var sum = 0

    pagesToProduce.forEach { page ->
        var pageRulesAreCorrect = true
        for (i in page.indices) {
            val beforeNumbers = page.take(i)
            val afterNumbers = page.drop(i + 1)
            val verifiedNumber = page[i]

            val beforeRulesForNumber = beforeRules.getOrDefault(verifiedNumber, emptyList())
            val afterRulesForNumber = afterRules.getOrDefault(verifiedNumber, emptyList())

            if (beforeRulesForNumber.any { afterNumbers.contains(it)} || afterRulesForNumber.any { beforeNumbers.contains(it)}) {
                pageRulesAreCorrect = false
                break
            }
        }
        if (pageRulesAreCorrect) {
            sum += page[page.size/2]
        }
    }

    return sum
}

class Day5Test {
    private val inputFile = File(javaClass.classLoader.getResource("day5input").file)
    private val exampleInputFile = File(javaClass.classLoader.getResource("day5exampleInput").file)
    private val part1ExampleAnswer = 143
    private val part1Answer = 4578

    @Test
    fun `part 1 answer check`() {
        val result = day5part1(inputFile.readLines())
        expectThat(result).isEqualTo(part1Answer)
    }

    @Test
    fun `part 1 example answer check`() {
        val result = day5part1(exampleInputFile.readLines())
        expectThat(result).isEqualTo(part1ExampleAnswer)
    }
}