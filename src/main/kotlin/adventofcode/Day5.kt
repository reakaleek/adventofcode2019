package adventofcode

import javax.xml.bind.DatatypeConverter.parseInt


fun main() {
    val intCode = getResourceLines("day_5_1.in").first().split(",").map { it.toInt() }

    val operations: Map<Int, (a: Int, b: Int) -> Int> = mapOf(
        1 to { a, b -> a + b },
        2 to { a, b -> a * b },
        3 to { a, _ -> a }
    )

    fun getTerm(mode: Int, intCode: List<Int>, index: Int): Int =
        when (mode) {
            0 -> intCode[intCode[index]]
            1 -> intCode[index]
            else -> error("invalid mode: $mode")
        }

    fun execute(intCode: List<Int>, index: Int = 0): List<Int> {
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
            return intCode
        }

        val term1 = getTerm(parameter1Mode, intCode, index + 1)
        val term2 = getTerm(parameter2Mode, intCode, index + 2)
        val term3 = getTerm(1, intCode, index + 3)

        return when (opCode) {
            1, 2 -> {
                tmp[term3] = operations[opCode]!!.invoke(term1, term2)
                if (hasChanged(intCode[index], tmp[index])) {
                    execute(tmp, index)
                } else {
                    execute(tmp, index + 4)
                }
            }
            3 -> {
                tmp[intCode[index + 1]] = 5
                execute(tmp, index + 2)
            }
            4 -> {
                print(term1)
                execute(tmp, index + 2)
            }
            5 -> {
                if (term1 != 0) {
                    execute(tmp, term2)
                } else {
                    execute(tmp, index + 3)
                }
            }
            6 -> {
                if (term1 == 0) {
                    execute(tmp, term2)
                } else {
                    execute(tmp, index + 3)
                }
            }
            7 -> {
                tmp[term3] = if (term1 < term2) 1 else 0
                if (hasChanged(intCode[index], tmp[index])) {
                    execute(tmp, index)
                } else {
                    execute(tmp, index + 4)
                }
            }
            8 -> {
                tmp[term3] = if (term1 == term2) 1 else 0
                if (hasChanged(intCode[index], tmp[index])) {
                    execute(tmp, index)
                } else {
                    execute(tmp, index + 4)
                }
            }
            else -> error("impossibru: $opCode")
        }
    }

    execute(intCode)
}
