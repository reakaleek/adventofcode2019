package adventofcode

import java.util.concurrent.BlockingQueue
import java.util.concurrent.LinkedBlockingQueue
import javax.xml.bind.DatatypeConverter.parseInt
import kotlin.concurrent.thread


private fun getIndex(mode: Int, intCode: List<Long>, index: Int, relativeBase: Int): Int =
    when (mode) {
        0 -> intCode[index].toInt()
        1 -> index
        2 -> relativeBase + intCode[index].toInt()
        else -> error("invalid mode: $mode")
    }

private tailrec fun execute(
    intCode: List<Long>,
    index: Int,
    relativeBase: Int,
    input: BlockingQueue<Long>,
    output: BlockingQueue<Long>
): Long? {
    val tmp = intCode.toMutableList()
    val code = intCode[index]
    val paddedCode = code.toString().padStart(5, '0')
    val opCode = paddedCode.drop(3).toInt()
    val parameter1Mode = parseInt(paddedCode[2] + "")
    val parameter2Mode = parseInt(paddedCode[1] + "")
    val parameter3Mode = parseInt(paddedCode[0] + "")

    fun hasChanged(instruction: Long, newInstraction: Long): Boolean {
        return instruction != newInstraction
    }

    if (opCode == 99) {
        return null
    }

    val param1Index = getIndex(parameter1Mode, intCode, index + 1, relativeBase)
    val param2Index = getIndex(parameter2Mode, intCode, index + 2, relativeBase)
    val param3Index = getIndex(parameter3Mode, intCode, index + 3, relativeBase)

    return when (opCode) {
        1, 2 -> {
            tmp[param3Index] = when (opCode) {
                1 -> intCode[param1Index] + intCode[param2Index]
                2 -> intCode[param1Index] * intCode[param2Index]
                else -> error("nope")
            }
            val nextIndex = when {
                hasChanged(intCode[index], tmp[index]) -> index
                else -> index + 4
            }
            execute(tmp, nextIndex, relativeBase, input, output)
        }
        3 -> {
            tmp[param1Index] = input.take()
            execute(tmp, index + 2, relativeBase, input, output)
        }
        4 -> {
            output.put(intCode[param1Index])
            execute(tmp, index + 2, relativeBase, input, output)
        }
        5 -> {
            val nextIndex = when {
                opCode == 5 && intCode[param1Index] != 0L -> intCode[param2Index].toInt()
                else -> index + 3
            }
            execute(tmp, nextIndex, relativeBase, input, output)
        }
        6 -> {
            val nextIndex = when {
                intCode[param1Index] == 0L -> intCode[param2Index].toInt()
                else -> index + 3
            }
            execute(tmp, nextIndex, relativeBase, input, output)
        }
        7 -> {
            tmp[param3Index] = if (intCode[param1Index] < intCode[param2Index]) 1 else 0
            val nextIndex = if (hasChanged(intCode[index], tmp[index])) index else index + 4
            execute(tmp, nextIndex, relativeBase, input, output)
        }
        8 -> {
            tmp[param3Index] = if (intCode[param1Index] == intCode[param2Index]) 1 else 0
            val nextIndex = if (hasChanged(intCode[index], tmp[index])) index else index + 4
            execute(tmp, nextIndex, relativeBase, input, output)
        }
        9 -> {
            execute(tmp, index + 2, relativeBase + intCode[param1Index].toInt(), input, output)
        }
        else -> error("impossibru: $opCode")
    }
}

class IntProgram(intCode: List<Long>) {

    private val inputQueue = LinkedBlockingQueue<Long>()
    private val outputQueue = LinkedBlockingQueue<Long>()

    init {
        thread { execute(intCode, 0, 0, inputQueue, outputQueue) }
    }

    fun get(input: Long): Pair<Long, Long> {
        inputQueue.put(input)
        return Pair(outputQueue.take(), outputQueue.take())
    }
}

data class Position(val x: Int, val y: Int)

enum class Color {
    BLACK,
    WHITE
}

enum class Direction(val position: Position) {
    UP(Position(0, -1)),
    RIGHT(Position(1, 0)),
    DOWN(Position(0, 1)),
    LEFT(Position(-1, 0))
}

enum class Turn(val value: Int) {
    LEFT(-1),
    RIGHT(1)
}


fun main() {

    val intCode = getResourceLines("day_11_1.in").first().split(",").map {
        it.toLong()
    }.plus(List(1000) { 0L })

    class Area {
        val map = emptyMap<Position, Pair<Color, Int>>().toMutableMap()

        private fun getValue(position: Position): Pair<Color, Int> = when {
            map.containsKey(position) -> map[position]!!
            else -> Pair(Color.BLACK, 0)
        }

        fun getColor(position: Position): Color = getValue(position).first

        fun getPaintedCount(position: Position): Int = getValue(position).second
        fun getPaintedAtLeastOnceCount(): Int = map.keys.toSet().size

        fun setColor(position: Position, color: Color) {
            if (map.containsKey(position)) {
                map[position] = Pair(color, map[position]!!.second + 1)
            } else {
                map[position] = Pair(color, 1)
            }
        }

        fun print() {

            val minX = map.keys.map { it.x }.min()!!
            val minY = map.keys.map { it.y }.min()!!
            val maxX = map.keys.map { it.x }.max()!!
            val maxY = map.keys.map { it.y }.max()!!

            for (y in minY-1..maxY) {
                for (x in minX-1..maxX) {
                    when(getColor(Position(x,y))) {
                        Color.BLACK -> print(" ")
                        Color.WHITE -> print("x")
                    }
                }
                println()
            }
        }

    }

    val intProgram = IntProgram(intCode)
    fun getColorAndTurn(color: Color): Pair<Color, Turn> = intProgram.get(color.ordinal.toLong()).also { println("intcode output: $it") }
        .let { (colorValue, turnValue) -> Pair(Color.values()[colorValue.toInt()], Turn.values()[turnValue.toInt()]) }

    var currentPosition = Position(0, 0)
    var currentDirection = Direction.UP
    val area = Area()


    for (i in 0..Int.MAX_VALUE) {
        val positionColor = if (i == 0) Color.WHITE else area.getColor(currentPosition)
        val (paintColor, turn) = getColorAndTurn(positionColor)
        area.setColor(currentPosition, paintColor)
        val nextDirectionOrdinal = (4 + currentDirection.ordinal + turn.value) % 4
        val nextDirection =  Direction.values()[nextDirectionOrdinal]
        val (dx, dy) = nextDirection.position
        currentPosition = Position(currentPosition.x + dx, currentPosition.y + dy)
        currentDirection = nextDirection
        area.print()
    }
}
