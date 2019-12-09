package adventofcode

import javax.xml.bind.DatatypeConverter.parseInt
import javax.xml.bind.DatatypeConverter.parseLong


fun main() {

    fun getIndex(mode: Int, intCode: List<Long>, index: Int, relativeBase: Int): Int =
        when (mode) {
            0 -> intCode[index].toInt()
            1 -> index
            2 -> relativeBase + intCode[index].toInt()
            else -> error("invalid mode: $mode")
        }

    tailrec fun execute(intCode: List<Long>, index: Int, relativeBase: Int): List<Long> {
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
            return intCode
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
                execute(tmp, nextIndex, relativeBase)
            }
            3 -> {
                tmp[param1Index] = 2
                execute(tmp, index + 2, relativeBase)
            }
            4 -> {
                print(intCode[param1Index])
                execute(tmp, index + 2, relativeBase)
            }
            5 -> {
                val nextIndex = when {
                    opCode == 5 && intCode[param1Index] != 0L -> intCode[param2Index].toInt()
                    else -> index + 3
                }
                execute(tmp, nextIndex, relativeBase)
            }
            6 -> {
                val nextIndex = when {
                    intCode[param1Index] == 0L -> intCode[param2Index].toInt()
                    else -> index + 3
                }
                execute(tmp, nextIndex, relativeBase)
            }
            7 -> {
                tmp[param3Index] = if (intCode[param1Index] < intCode[param2Index]) 1 else 0
                val nextIndex = if (hasChanged(intCode[index], tmp[index])) index else index + 4
                execute(tmp, nextIndex, relativeBase)
            }
            8 -> {
                tmp[param3Index] = if (intCode[param1Index] == intCode[param2Index]) 1 else 0
                val nextIndex = if (hasChanged(intCode[index], tmp[index])) index else index + 4
                execute(tmp, nextIndex, relativeBase)
            }
            9 -> {
                execute(tmp, index + 2, relativeBase + intCode[param1Index].toInt())
            }
            else -> error("impossibru: $opCode")
        }
    }

    val intCode = getResourceLines("day_9_1.in").first().split(",").map {
        parseLong(it.trim())
    }.plus(List(1000) { 0L })
    execute(intCode, 0, 0)
}
