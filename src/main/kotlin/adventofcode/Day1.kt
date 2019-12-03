package adventofcode

fun calculateFuel(mass: Int): Int = mass / 3 - 2

fun calculateFuelRecursive(mass: Int, result: Int = 0): Int {
    val tmpResult = calculateFuel(mass)
    return  if (tmpResult <= 0) result
            else calculateFuelRecursive(calculateFuel(mass), tmpResult + result)
}

fun calculateSum(masses: List<Int>): Int = masses.map { calculateFuelRecursive(it) }.sum()

fun main() {
    val lines = getResourceLines("day_1_2.in")
    val result = calculateSum(lines.map { it.toInt()})
    println(result)
}

