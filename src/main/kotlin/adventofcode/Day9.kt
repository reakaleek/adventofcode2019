package adventofcode

import com.marcinmoskala.math.permutations
import java.lang.Integer.parseInt
import java.util.concurrent.LinkedBlockingQueue
import kotlin.concurrent.thread

fun main() {
    val operations: Map<Int, (a: Long, b: Long) -> Long> = mapOf(
        1 to { a, b -> a + b },
        2 to { a, b -> a * b }
    )

    fun getTerm(mode: Int, intCode: List<Long>, index: Int, relativeBase: Int): Long =
        when (mode) {
            0 -> intCode[intCode[index].toInt()]
            1 -> intCode[index]
            2 -> intCode[relativeBase + intCode[index].toInt()]
            else -> error("invalid mode: $mode")
        }

    fun execute(
        intCode: List<Long>,
        input: LinkedBlockingQueue<Long>,
        output: LinkedBlockingQueue<Long>,
        index: Int,
        relativeBase: Int
    ): Int? {

        val tmp = intCode.toMutableList().apply {
            addAll(List(500) { i -> 0L })
        }
        val code = intCode[index]
        val paddedCode = code.toString().padStart(5, '0')
        val opCode = paddedCode.drop(3).toInt()
        val parameter1Mode = parseInt(paddedCode[2] + "")
        val parameter2Mode = parseInt(paddedCode[1] + "")
        val parameter3Mode = 1 // parseInt(paddedCode[0] + "")

        fun hasChanged(instruction: Long, newInstraction: Long): Boolean {
            return instruction != newInstraction
        }

        if (opCode == 99) {
            return null
        }

        val term1 = getTerm(parameter1Mode, intCode, index + 1, relativeBase)

        return when (opCode) {
            0 -> {
                tmp[intCode[index + 1].toInt()] = 3
                execute(tmp, input, output, index + 2, relativeBase)
            }
            1, 2 -> {
                val term2 = getTerm(parameter2Mode, intCode, index + 2, relativeBase)
                val term3 = getTerm(parameter3Mode, intCode, index + 3, relativeBase)
                tmp[term3.toInt()] = operations[opCode]!!.invoke(term1, term2)
                if (hasChanged(intCode[index], tmp[index])) {
                    execute(tmp, input, output, index, relativeBase)
                } else {
                    execute(tmp, input, output, index + 4, relativeBase)
                }
            }
            3, 4 -> {
                when (opCode) {
                    3 -> {
                        tmp[intCode[index + 1].toInt()] = input.take()
                    }
                    4 -> {
                        output.put(term1)
                    }
                    else -> error("nope")
                }
                execute(tmp, input, output, index + 2, relativeBase)
            }
            5, 6 -> {
                val term2 = getTerm(parameter2Mode, intCode, index + 2, relativeBase)
                when {
                    opCode == 5 && term1 != 0L -> execute(tmp, input, output, term2.toInt(), relativeBase)
                    opCode == 6 && term1 == 0L -> execute(tmp, input, output, term2.toInt(), relativeBase)
                    else -> execute(tmp, input, output, index + 3, relativeBase)
                }
            }
            7, 8 -> {
                val term2 = getTerm(parameter2Mode, intCode, index + 2, relativeBase)
                val term3 = getTerm(parameter3Mode, intCode, index + 3, relativeBase)
                tmp[term3.toInt()] = when (opCode) {
                    7 -> if (term1 < term2) 1L else 0L
                    8 -> if (term1 == term2) 1L else 0L
                    else -> error("impossibru")
                }
                if (hasChanged(intCode[index], tmp[index])) {
                    execute(tmp, input, output, index, relativeBase)
                } else {
                    execute(tmp, input, output, index + 4, relativeBase)
                }
            }
            9 -> {
                execute(tmp, input, output, index + 2, (relativeBase + term1).toInt())
            }
            else -> error("impossibru: $opCode")
        }
    }

    val intCode = getResourceLines("day_9_1.in").first().split(",").map { it.toLong() }
    /*val intCode = listOf(
        109,1,204,-1,1001,100,1,100,1008,100,16,101,1006,101,0,99
    ).map { it.toLong() }*/


    (0..4)
        .toList()
        .permutations()
        .run {
            this.map { permutation ->
                val inputs = permutation.map {
                    LinkedBlockingQueue<Long>().apply { add(it.toLong()) }
                }
                inputs.first().add(1)
                execute(intCode, inputs[0], inputs[1], 0, 0)
                    .let { execute(intCode, inputs[1], inputs[2], 0, 0) }
                    .let { execute(intCode, inputs[2], inputs[3], 0, 0) }
                    .let { execute(intCode, inputs[3], inputs[4], 0, 0) }
                    .let { execute(intCode, inputs[4], inputs[0], 0, 0) }
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
                    LinkedBlockingQueue<Long>().apply { add(it.toLong()) }
                }
                inputs.first().add(1)
                inputs.zipWithNext().forEach { thread { execute(intCode, it.first, it.second, 0, 0) } }
                execute(intCode, inputs.last(), inputs.first(), 0, 0)
                inputs.first().last()
            }
        }.max().let {
            println(it)
        }
}
