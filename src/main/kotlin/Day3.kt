package checinski.adam

import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import java.io.File

enum class CommandType { MUL, DO, DONT }
data class Operation(val command: CommandType, val left: Int?, val right: Int?)

val pattern = Regex("""(don't|mul|do)(?:\((\d{1,3}),(\d{1,3})\))?""")

fun extractOperations(input: String): List<Operation> =
    pattern.findAll(input).map { result ->
        val (command, left, right) = result.destructured
        Operation(
            command.toCommandType(),
            left.takeIf { it.isNotBlank() }?.toInt(),
            right.takeIf { it.isNotBlank() }?.toInt()
        )
    }.toList()

private fun String.toCommandType(): CommandType {
    return when (this) {
        "mul" -> CommandType.MUL
        "do" -> CommandType.DO
        "don't" ->CommandType.DONT
        else -> throw IllegalArgumentException("Unknown command type")
    }
}

fun day3part1(input: String): Int {
    val operations = extractOperations(input).filter {
        it.command == CommandType.MUL && it.left != null && it.right != null
    }

    return processOperations(operations) { operation, sum -> handleMul(operation, sum) }
}

fun day3part2(input: String): Int {
    var multiplicationEnabled = true

    val operations = extractOperations(input).filter {
        it.command in setOf(CommandType.DO, CommandType.DONT) ||
        (it.command == CommandType.MUL && it.left != null && it.right != null)
    }

    return processOperations(operations) { operation, sum ->
        when (operation.command) {
            CommandType.DO -> { multiplicationEnabled = true; sum }
            CommandType.DONT -> { multiplicationEnabled = false; sum }
            CommandType.MUL -> if (multiplicationEnabled) handleMul(operation, sum) else sum
        }
    }
}

private fun handleMul(operation: Operation, sum: Int): Int {
    return sum + (operation.left!! * operation.right!!)
}

private fun processOperations(operations: List<Operation>, handler: (Operation, Int) -> Int): Int {
    return operations.fold(0) { sum, operation -> handler(operation, sum) }
}

class Day3Test {
    private val inputFile = File(javaClass.classLoader.getResource("day3input").file)
    private val part1Answer = 185797128
    private val part2Answer = 89798695

    @Test
    fun `part 1 answer check`() {
        val result = day3part1(inputFile.readText())

        expectThat(result).isEqualTo(part1Answer)
    }

    @Test
    fun `part 2 answer check`() {
        val result = day3part2(inputFile.readText())

        expectThat(result).isEqualTo(part2Answer)
    }
}