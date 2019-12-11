package adventofcode

fun main() {
    //val matrix = getResourceLines("day_10_1.in").map { it.map { c -> if (c == '#') 1 else 0}}

    val matrix: List<List<Int>> = listOf(
        listOf(0, 1, 0, 0, 1),
        listOf(0, 0, 0, 0, 0),
        listOf(1, 1, 1, 1, 1),
        listOf(0, 0, 0, 0, 1),
        listOf(0, 0, 0, 1, 1)
    )

    class Point(val x: Int, val y: Int) {
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

        override fun toString(): String {
            return "P(x=$x, y=$y)"
        }
    }

    val points = matrix.foldIndexed(emptyList<Point>()) { y, acc, list ->
        acc.plus(list.mapIndexed { x, i -> if (i == 1) Point(x, y) else null }.filterNotNull())
    }

    fun countPointsInSight(currentPoint: Point, otherPoints: List<Point>): Int =
        otherPoints.fold(emptySet<Pair<Int, Int>>()) { acc, p ->
            val dx = p.x - currentPoint.x
            val dy = p.y - currentPoint.y
            val ggt: Int by lazy { dx.toBigInteger().gcd(dy.toBigInteger()).toInt() }
            acc.plus(Pair(dy / ggt, dx / ggt))
        }.size

    points.map { point ->
        val otherPoints = points.filter { it != point }
        Pair(point, countPointsInSight(point, otherPoints))
    }
    .maxBy { it.second }
    .let { println(it) }

}
