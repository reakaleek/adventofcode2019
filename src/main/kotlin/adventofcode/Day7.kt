package adventofcode

import com.marcinmoskala.math.permutations
import java.lang.Integer.parseInt
import java.util.concurrent.LinkedBlockingQueue
import kotlin.concurrent.thread

fun main() {
    val operations: Map<Int, (a: Int, b: Int) -> Int> = mapOf(
        1 to { a, b -> a + b },
        2 to { a, b -> a * b }
    )

    fun getIndex(mode: Int, intCode: List<Int>, index: Int): Int =
        when (mode) {
            0 -> intCode[index]
            1 -> index
            else -> error("invalid mode: $mode")
        }

    fun execute(
        intCode: List<Int>,
        input: LinkedBlockingQueue<Int>,
        output: LinkedBlockingQueue<Int>,
        index: Int = 0
    ): Int? {

        val tmp = ArrayList(intCode)
        val code = intCode[index]
        val paddedCode = code.toString().padStart(5, '0')
        val opCode = paddedCode.drop(3).toInt()
        val parameter1Mode = parseInt(paddedCode[2] + "")
        val parameter2Mode = parseInt(paddedCode[1] + "")
        val parameter3Mode = parseInt(paddedCode[0] + "")

        fun hasChanged(instruction: Int, newInstraction: Int): Boolean {
            return instruction != newInstraction
        }

        if (opCode == 99) {
            return null
        }

        val param1Index = getIndex(parameter1Mode, intCode, index + 1)

        return when (opCode) {
            0 -> {
                tmp[intCode[index + 1]] = 3
                execute(tmp, input, output, index + 2)
            }
            1, 2 -> {
                val param2Index = getIndex(parameter2Mode, intCode, index + 2)
                val param3Index = getIndex(parameter3Mode, intCode, index + 3)
                tmp[param3Index] = operations[opCode]!!.invoke(intCode[param1Index], intCode[param2Index])
                if (hasChanged(intCode[index], tmp[index])) {
                    execute(tmp, input, output, index)
                } else {
                    execute(tmp, input, output, index + 4)
                }
            }
            3 -> {
                tmp[param1Index] = input.take()
                execute(tmp, input, output, index + 2)
            }
            4 -> {
                output.put(intCode[param1Index])
                execute(tmp, input, output, index + 2)
            }
            5, 6 -> {
                val param2Index = getIndex(parameter2Mode, intCode, index + 2)
                when {
                    opCode == 5 && intCode[param1Index] != 0 -> execute(tmp, input, output, intCode[param2Index])
                    opCode == 6 && intCode[param1Index] == 0 -> execute(tmp, input, output, intCode[param2Index])
                    else -> execute(tmp, input, output, index + 3)
                }
            }
            7, 8 -> {
                val term2 = intCode[getIndex(parameter2Mode, intCode, index + 2)]
                val term3 = intCode[getIndex(1, intCode, index + 3)]
                tmp[term3] = when (opCode) {
                    7 -> if (intCode[param1Index] < term2) 1 else 0
                    8 -> if (intCode[param1Index] == term2) 1 else 0
                    else -> error("impossibru")
                }
                if (hasChanged(intCode[index], tmp[index])) {
                    execute(tmp, input, output, index)
                } else {
                    execute(tmp, input, output, index + 4)
                }
            }
            else -> error("impossibru: $opCode")
        }
    }

    val intCode = getResourceLines("day_7_1.in").first().split(",").map { it.toInt() }

    (0..4)
        .toList()
        .permutations()
        .run {
            this.map { permutation ->
                val inputs = permutation.map {
                    LinkedBlockingQueue<Int>().apply { add(it) }
                }
                inputs.first().add(0)
                execute(intCode, inputs[0], inputs[1])
                    .let { execute(intCode, inputs[1], inputs[2]) }
                    .let { execute(intCode, inputs[2], inputs[3]) }
                    .let { execute(intCode, inputs[3], inputs[4]) }
                    .let { execute(intCode, inputs[4], inputs[0]) }
                    .let { inputs.first().last() }
            }
        }
        .filterNotNull()
        .max().let {
            println(it)
        }

    (5..9)
        .toList()
        .permutations()
        .run {
            this.map { permutation ->
                val inputs = permutation.map {
                    LinkedBlockingQueue<Int>().apply { add(it) }
                }
                inputs.first().add(0)
                inputs.zipWithNext().forEach { thread { execute(intCode, it.first, it.second) } }
                execute(intCode, inputs.last(), inputs.first())
                inputs.first().last()
            }
        }.max().let {
            println(it)
        }
}
