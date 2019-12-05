package adventofcode

fun hasAllIncreasingDigits(code: Int) =
    code.toString()
        .map { it.toInt() }
        .zipWithNext()
        .all { it.first <= it.second }

fun hasDouble(code: Int) = code.toString().toSet().size < 6

fun hasAtLeast1Double(code: Int) =
    code.toString()
        .groupBy { it }
        .map { it.value.size }
        .any { it == 2 }

fun main() {
    val range = (152085..670283)
    val part1 = range.filter { hasAllIncreasingDigits(it) && hasDouble(it) }
                          .count()
    val part2 = range.filter { hasAllIncreasingDigits(it) &&  hasAtLeast1Double(it) }
                          .count()
    println(part1)
    println(part2)
}
