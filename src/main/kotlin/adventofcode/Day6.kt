import adventofcode.getResourceLines

fun main() {
    val lines = getResourceLines("day_6_1.in")

    val orbitMap = lines
        .map { line -> line.split(")").let { Pair(it[1], it[0]) } }
        .fold(emptyMap<String, String>()) { acc, s -> acc.plus(s) }

    fun getPath(map: Map<String, String>, name: String): List<String> {
        return map[name]?.let { listOf(it).plus(getPath(map, it)) } ?: emptyList()
    }

    orbitMap.map { getPath(orbitMap, it.key).count() }.sum().let { println(it) }

    val sanPath = getPath(orbitMap, "SAN")
    val youPath = getPath(orbitMap, "YOU")
    val commonPath = sanPath.intersect(youPath)

    println((sanPath.count() + youPath.count() - commonPath.count() * 2))
}
