package adventofcode

fun hasAllIncreasingDigits(it: Int) =
    it.toString()
        .map { it.toInt() }
        .zipWithNext()
        .all { it.first <= it.second }

fun hasDouble(it: Int) = it.toString().toSet().size < 6

fun hasAtLeast1Double(it: Int) =
    it.toString()
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
