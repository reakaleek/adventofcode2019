package adventofcode

import com.google.common.math.IntMath
import javax.xml.bind.DatatypeConverter.parseInt


fun main() {
    val intCode = getResourceLines("day_5_1.in").first().split(",").map { it.toInt() }

    val operations: Map<Int, (a: Int, b: Int) -> Int> = mapOf(
        1 to { a, b -> a + b },
        2 to { a, b -> a * b }
    )

    fun getTerm(mode: Int, intCode: List<Int>, index: Int): Int =
        when (mode) {
            0 -> intCode[intCode[index]]
            1 -> intCode[index]
            else -> error("invalid mode: $mode")
        }

    fun getIndex(mode: Int, intCode: List<Int>, index: Int): Int =
        when (mode) {
            0 -> intCode[index]
            1 -> index
            else -> error("invalid mode: $mode")
        }

    fun execute(intCode: List<Int>, index: Int = 0): List<Int> {
        val tmp = ArrayList(intCode)
        val code = intCode[index]
        val paddedCode = code.toString().padStart(5, '0')
        val opCode = paddedCode.drop(3).toInt()

        val parameter1Mode = parseInt(paddedCode[2] + "")
        val parameter2Mode = parseInt(paddedCode[1] + "")
        val parameter3Mode = 1 // parseInt(paddedCode[0] + "")

        fun hasChanged(instruction: Int, newInstraction: Int): Boolean {
            return instruction != newInstraction
        }

        if (opCode == 99) {
            return intCode
        }

        val param1Index = getIndex(parameter1Mode, intCode, index + 1)
        val param2Index = getIndex(parameter2Mode, intCode, index + 2)
        val param3Index = getIndex(parameter3Mode, intCode, index + 3)

        val term1 = getTerm(parameter1Mode, intCode, index + 1)
        val term2 = getTerm(parameter2Mode, intCode, index + 2)
        val term3 = getTerm(parameter3Mode, intCode, index + 3)

        return when (opCode) {
            1, 2 -> {
                tmp[intCode[param3Index]] = operations[opCode]!!.invoke(intCode[param1Index], intCode[param2Index])
                if (hasChanged(intCode[index], tmp[index])) {
                    execute(intCode, index)
                } else {
                    execute(tmp, index + 4)
                }
            }
            3 -> {
                tmp[param1Index] = 5
                execute(tmp.toList(), index + 2)
            }
            4 -> {
                print(intCode[param1Index])
                execute(intCode, index + 2)
            }
            5 -> {
                if (intCode[param1Index] != 0) {
                    execute(tmp.toList(), intCode[param2Index])
                } else {
                    execute(tmp.toList(), index + 3)
                }
            }
            6 -> {
                if (intCode[param1Index] == 0) {
                    execute(tmp.toList(), intCode[param2Index])
                } else {
                    execute(tmp.toList(), index + 3)
                }
            }
            7 -> {
                tmp[intCode[param3Index]] = if (intCode[param1Index] < intCode[param2Index]) 1 else 0
                if (hasChanged(intCode[index], tmp[index])) {
                    execute(tmp.toList(), index)
                } else {
                    execute(tmp.toList(), index + 4)
                }
            }
            8 -> {
                tmp[intCode[param3Index]] = if (intCode[param1Index] == intCode[param2Index]) 1 else 0
                if (hasChanged(intCode[index], tmp[index])) {
                    execute(tmp.toList(), index)
                } else {
                    execute(tmp.toList(), index + 4)
                }
            }
            else -> error("impossibru: $opCode")
        }
    }

    execute(intCode)
}
