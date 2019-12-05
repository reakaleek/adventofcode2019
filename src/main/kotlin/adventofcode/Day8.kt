package adventofcode

import javax.xml.bind.DatatypeConverter.parseInt

private const val WIDTH = 25
private const val HEIGHT = 6

fun main() {

    val pixels = getResourceLines("day_8_1.in").first().map { parseInt(it + "") }

    pixels.chunked(WIDTH * HEIGHT)
        .groupBy { it.count { p -> p == 0 } }
        .let { it[it.keys.min()] }
        .let { it!!.first() }
        .let { it.count{ x -> x == 1 } * it.count { x -> x == 2 } }
        .let(::println)

    pixels.chunked(WIDTH * HEIGHT)
        .reduce { acc, list ->
            acc.zip(list).map { (first, second) ->
                when(first) {
                    2 -> second
                    else -> first
                }
            }
        }
        .let {
            for (y in 0 until HEIGHT) {
                for (x in 0 until WIDTH) {
                    val value = it[x+WIDTH * y]
                    print(if (value == 1) "X" else " ")
                }
                println()
            }
        }
}
