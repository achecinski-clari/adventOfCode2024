import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import java.io.File

enum class PageStatus { CORRECT, INCORRECT, UNKNOWN }

data class Page(val pageNumbers: List<Int>, val isCorrect: PageStatus = PageStatus.UNKNOWN) {
    val indices = pageNumbers.indices
}

data class RuleEngine(val input: List<String>) {
    private val beforeRules = mutableMapOf<Int, List<Int>>()
    private val afterRules = mutableMapOf<Int, List<Int>>()

    init {
        val index = input.indexOf("")

        input.take(index).map { it.split('|') }.forEach {
            val before = it[0].toInt()
            val after = it[1].toInt()
            afterRules[before] = afterRules.getOrDefault(before, emptyList()) + after
            beforeRules[after] = beforeRules.getOrDefault(after, emptyList()) + before
        }
    }

    fun isCorrect(page: Page): Boolean {
        return !page.indices.any { i ->
            val beforeNumbers = page.pageNumbers.take(i)
            val afterNumbers = page.pageNumbers.drop(i + 1)
            val verifiedNumber = page.pageNumbers[i]

            val beforeRulesForNumber = beforeRules.getOrDefault(verifiedNumber, emptyList())
            val afterRulesForNumber = afterRules.getOrDefault(verifiedNumber, emptyList())

            beforeRulesForNumber.any { afterNumbers.contains(it)} || afterRulesForNumber.any { beforeNumbers.contains(it)}
        }
    }

    fun correctPage(page: Page): Page {
        return page.copy(
            pageNumbers = correctPageNumbersUsingRules(page.pageNumbers),
            isCorrect = PageStatus.CORRECT
        )
    }

    private fun correctPageNumbersUsingRules(pageNumbers: List<Int>): List<Int> {
        val adjacency = mutableMapOf<Int, MutableList<Int>>()
        val inDegree = pageNumbers.associateWithTo(mutableMapOf()) { 0 }

        afterRules.forEach { (before, afterList) ->
            if (before in pageNumbers) {
                afterList.filter { it in pageNumbers }.forEach { after ->
                    adjacency.getOrPut(before) { mutableListOf() }.add(after)
                    inDegree[after] = inDegree.getValue(after) + 1
                }
            }
        }

        val queue = ArrayDeque(pageNumbers.filter { inDegree[it] == 0 })
        val result = mutableListOf<Int>()

        while (queue.isNotEmpty()) {
            val current = queue.removeFirst()
            result.add(current)
            adjacency[current]?.forEach { neighbor ->
                inDegree[neighbor] = inDegree.getValue(neighbor) - 1
                if (inDegree[neighbor] == 0) queue.add(neighbor)
            }
        }

        require(result.size == pageNumbers.size) {
            "No valid ordering found for the given rules and page numbers"
        }

        return result
    }
}

data class PagesProducer(private val ruleEngine: RuleEngine) {
    fun producePages(input: List<String>): List<Page> {
        val index = input.indexOf("")

        return input.drop(index + 1)
            .map { it.split(',').map { e -> e.toInt() }.toPage() }
            .map { it.copy(isCorrect = if (ruleEngine.isCorrect(it)) PageStatus.CORRECT else PageStatus.INCORRECT ) }
    }
}


fun day5part1(input: List<String>): Int {
    val ruleEngine = RuleEngine(input)
    val pagesProducer = PagesProducer(ruleEngine)

    return pagesProducer.producePages(input)
        .filter { it.isCorrect == PageStatus.CORRECT }
        .sumOf { it.pageNumbers[it.pageNumbers.size/2] }
}

fun day5part2(input: List<String>): Int {
    val ruleEngine = RuleEngine(input)
    val pagesProducer = PagesProducer(ruleEngine)

    val incorrectPages = pagesProducer.producePages(input).filter { it.isCorrect == PageStatus.INCORRECT }

    val correctedPages = incorrectPages.map { ruleEngine.correctPage(it) }

    return correctedPages.sumOf { it.pageNumbers[it.pageNumbers.size/2] }
}

private fun List<Int>.toPage(): Page {
    return Page(this)
}

class Day5Test {
    private val inputFile = File(javaClass.classLoader.getResource("day5input").file)
    private val exampleInputFile = File(javaClass.classLoader.getResource("day5exampleInput").file)
    private val part1ExampleAnswer = 143
    private val part1Answer = 4578
    private val part2Answer = 6179

    @Test
    fun `part 1 answer check`() {
        val result = day5part1(inputFile.readLines())
        expectThat(result).isEqualTo(part1Answer)
    }

    @Test
    fun `part 2 answer check`() {
        val result = day5part2(inputFile.readLines())
        expectThat(result).isEqualTo(part2Answer)
    }

    @Test
    fun `part 1 example answer check`() {
        val result = day5part1(exampleInputFile.readLines())
        expectThat(result).isEqualTo(part1ExampleAnswer)
    }

    @Test
    fun `rule engine test`() {
        val result = RuleEngine(exampleInputFile.readLines())

        val page = Page(listOf(75, 29, 13))

        expectThat(result.isCorrect(page)).isEqualTo(true)
    }
}