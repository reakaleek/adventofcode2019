package adventofcode

import com.marcinmoskala.math.times
import kotlin.math.roundToInt
import kotlin.math.sqrt

class Point(val x: Int, val y: Int) {
    private fun getLength(instruction: String): Int = instruction.drop(1).toInt()

    fun getNext(instruction: String): Point {
        val len = getLength(instruction)
        return when {
            instruction.startsWith("U") -> Point(x, y + len)
            instruction.startsWith("D") -> Point(x, y - len)
            instruction.startsWith("L") -> Point(x - len, y)
            instruction.startsWith("R") -> Point(x + len, y)
            else -> error("nope")
        }
    }

    fun getManhattanDistance(): Int = x + y

    fun getDist(point: Point = Point(0, 0)): Double =
        sqrt((((point.x - x) * (point.x - x) + (point.y - y) * (point.y - y)).toDouble()))

    override fun toString(): String = "P(x=$x, y=$y)"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as Point
        if (x != other.x) return false
        if (y != other.y) return false
        return true
    }

    override fun hashCode(): Int {
        var result = x
        result = 31 * result + y
        return result
    }
}

class Line(private val a: Point, val b: Point, val previous: Line?) {
    override fun toString(): String = "V(a=$a, b=$b)"
    private fun contains(c: Point): Boolean = (a.getDist(c) + b.getDist(c)) == a.getDist(b)
    fun getDist(): Int = a.getDist(b).roundToInt()
    fun getIntersectionPoint(other: Line): Point? {
        val a1: Int = (b.y - a.y)
        val b1: Int = (a.x - b.x)
        val c1: Int = a1 * a.x + b1 * a.y
        val a2: Int = (other.b.y - other.a.y)
        val b2: Int = (other.a.x - other.b.x)
        val c2: Int = a2 * other.a.x + b2 * other.a.y
        val determinant = a1 * b2 - a2 * b1
        if (determinant == 0) {
            return null
        }
        val x = (b2 * c1 - b1 * c2) / determinant
        val y = (a1 * c2 - a2 * c1) / determinant
        val point = Point(x, y)
        return when {
            contains(point) && other.contains(point) && point != a && point != b -> point
            else -> null
        }
    }
}

fun getLines(instructions: List<String>): List<Line> {
    val startingPoint = Point(0, 0)
    val points = instructions.fold(listOf(startingPoint)) { acc, curr ->
        acc.plus(acc.last().getNext(curr))
    }
    return points.zipWithNext().fold(Pair<Line?, List<Line>>(null, emptyList())) { acc, curr ->
        val line = Line(curr.first, curr.second, acc.first)
        Pair(line, acc.second.plus(line))
    }.second
}

fun getLengthFromSource(line: Line, result: Int = 0): Int =
    if (line.previous == null) result + line.getDist()
    else getLengthFromSource(line.previous, line.getDist() + result)

fun main() {
    val lines = getResourceLines("day_3_1.in").map { it.split(",") }
    val combinations = lines.map { getLines(it) }.let { it.first() * it.last() }

    val part1 = combinations
        .mapNotNull { it.first.getIntersectionPoint(it.second) }
        .minBy { it.getDist() }
        .let { it?.getManhattanDistance() }

    val part2 = combinations
        .mapNotNull {
            val intersectionPoint = it.first.getIntersectionPoint(it.second)
            if (intersectionPoint != null) Triple(it.first, it.second, intersectionPoint) else null
        }
        .map {
            getLengthFromSource(it.first) +
                    getLengthFromSource(it.second) -
                    (it.third).getDist(it.second.b) -
                    (it.third).getDist(it.first.b)
        }
        .min()
        .let { it?.roundToInt() }

    println(part1)
    println(part2)
}
