import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import java.io.File

private const val STARTING_POSITION_CHAR = '^'
private const val OBSTACLE_CHAR = '#'
data class Position(val x: Int, val y: Int, val direction: Direction = Direction.UP) {
    fun move(): Position {
        return when (direction) {
            Direction.UP -> this.copy(y = y - 1)
            Direction.DOWN -> this.copy(y = y + 1)
            Direction.LEFT -> this.copy(x = x - 1)
            Direction.RIGHT -> this.copy(x = x + 1)
        }
    }

    fun turnRight(): Position {
        return when (direction) {
            Direction.UP -> this.copy(x = x + 1, direction = Direction.RIGHT)
            Direction.DOWN -> this.copy(x = x - 1, direction = Direction.LEFT)
            Direction.LEFT ->this.copy(y = y - 1, direction = Direction.UP)
            Direction.RIGHT -> this.copy(y = y + 1, direction = Direction.DOWN)
        }
    }

    fun isNotOutsideTheMap(input: List<String>): Boolean {
        return y >= 0 && y < input.size && x >= 0 && x < input[y].length
    }

    fun isOutsideTheMap(input: List<String>): Boolean {
        return !isNotOutsideTheMap(input)
    }

    enum class Direction {
        UP, DOWN, LEFT, RIGHT
    }
}

suspend fun day6part1(input: List<String>): Int = supervisorScope {
    val startingPosition = findStartingPosition(input)
    val visitedPositions = mutableSetOf(startingPosition)

    val isObstacle = { position: Position -> input[position.y][position.x] == OBSTACLE_CHAR }

    var currentPosition = startingPosition

    while (currentPosition.isNotOutsideTheMap(input)) {
        var nextPosition = currentPosition.move()
        if (nextPosition.isOutsideTheMap(input)) {
            break
        }
        if (isObstacle(nextPosition)) {
            nextPosition = currentPosition.turnRight()
        }
        visitedPositions.add(nextPosition)
        currentPosition = nextPosition
    }

    visitedPositions.distinctBy { it.x to it.y }.size
}

fun day6part2(input: List<String>): Int {
    val startingPosition = findStartingPosition(input)
    val visitedPositions = mutableSetOf(startingPosition)

    val isObstacle = { position: Position -> input[position.y][position.x] == OBSTACLE_CHAR }

    var currentPosition = startingPosition
    val obstacles = mutableSetOf<Position>()

    while (currentPosition.isNotOutsideTheMap(input)) {
        var nextPosition = currentPosition.move()
        if (nextPosition.isOutsideTheMap(input)) {
            break
        }
        if (isObstacle(nextPosition)) {
            obstacles.add(nextPosition)
            nextPosition = currentPosition.turnRight()
        }
        visitedPositions.add(nextPosition)
        currentPosition = nextPosition
    }

    val distinctVisitedPositions = visitedPositions.distinctBy { it.x to it.y } - startingPosition
    var loops = 0

    distinctVisitedPositions.forEach { position ->
        val inputWithObstacleAtPosition = input.toMutableList().also { newInput ->
            newInput[position.y] = newInput[position.y].toCharArray().also {
                it[position.x] = OBSTACLE_CHAR
            }.joinToString("")
        }

        val visitedPositionsIn = mutableSetOf(startingPosition)

        val isObstacleIn = { p: Position -> inputWithObstacleAtPosition[p.y][p.x] == OBSTACLE_CHAR }

        var currentPositionIn = startingPosition
        while (currentPositionIn.isNotOutsideTheMap(inputWithObstacleAtPosition)) {
            var nextPosition = currentPositionIn.move()
            if (nextPosition.isOutsideTheMap(inputWithObstacleAtPosition)) {
                break
            }
            if (isObstacleIn(nextPosition)) {
                nextPosition = currentPositionIn.turnRight()
            }
            if (!visitedPositionsIn.add(nextPosition)) {
                loops++
                break
            }
            currentPositionIn = nextPosition
        }
    }
    return loops
}

private fun findStartingPosition(input: List<String>): Position {
    input.forEachIndexed { y, row ->
        row.forEachIndexed { x, char ->
            if (char == STARTING_POSITION_CHAR) {
                return Position(x, y)
            }
        }
    }
    throw IllegalArgumentException("Starting position not found")
}

class Day6Test {
    private val inputFile = File(javaClass.classLoader.getResource("day6input").file)
    private val exampleInputFile = File(javaClass.classLoader.getResource("day6exampleInput").file)
    private val part1ExampleAnswer = 41
    private val part2ExampleAnswer = 6
    private val part1Answer = 5080
    private val part2Answer = 1938

    @Test
    fun `part 1 example answer check`() = runTest {
        val result = day6part1(exampleInputFile.readLines())
        expectThat(result).isEqualTo(part1ExampleAnswer)
    }

    @Test
    fun `part 1 answer check`() = runTest {
        val result = day6part1(inputFile.readLines())
        expectThat(result).isEqualTo(part1Answer)
    }

    @Test
    fun `part 2 answer check`() = runTest {
        val result = day6part2(inputFile.readLines())
        expectThat(result).isEqualTo(part2Answer)
    }

    @Test
    fun `part 2 example answer check`() = runTest {
        val result = day6part2(exampleInputFile.readLines())
        expectThat(result).isEqualTo(part2ExampleAnswer)
    }
}