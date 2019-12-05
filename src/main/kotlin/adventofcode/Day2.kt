
package adventofcode

import com.marcinmoskala.math.times



fun main() {
    val intCode = getResourceLines("day_2_1.in").first().split(",").map { it.toInt() }

    fun intCodeWithNounAndVerb(intCode: List<Int>, noun: Int, verb: Int): List<Int> {
        val tmp = ArrayList(intCode)
        tmp[1] = noun
        tmp[2] = verb
        return tmp
    }

    val operations: Map<Int, (a: Int, b: Int) -> Int> = mapOf(
        1 to { a, b -> a + b },
        2 to { a, b -> a * b }
    )

    fun getTerm(intCode: List<Int>, index: Int): Int = intCode[intCode[index]]

    fun execute(intCode: List<Int>, index: Int = 0): List<Int> {
        val tmp = ArrayList(intCode)
        val destIndex = intCode[index + 3]
        return when (val opCode = intCode[index]) {
            99 -> intCode
            1, 2 -> {
                tmp[destIndex] = operations[opCode]!!.invoke(
                    getTerm(intCode, index + 1),
                    getTerm(intCode, index + 2)
                )
                execute(tmp, index + 4)
            }
            else -> error("impossibru")
        }
    }

    fun findResult(intCode: List<Int>, combinations: List<Pair<Int, Int>>): Int {
        val pair = combinations.first()
        val result = intCode.run {
            intCodeWithNounAndVerb(
                this,
                pair.second,
                pair.first
            )
        }
            .run { execute(this) }
            .run { first() }
        return when (result) {
            19690720 -> 100 * pair.second + pair.first
            else -> findResult(intCode, combinations.drop(1))
        }
    }
    val possibleValues: List<Int> = (0..99).toList()
    val result = findResult(
        intCode,
        possibleValues * possibleValues
    )
    println(result)
}
