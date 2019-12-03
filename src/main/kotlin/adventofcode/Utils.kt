package adventofcode

import java.io.File

fun getResourceLines(path: String): List<String> = File(ClassLoader.getSystemResource(path).path).readLines(Charsets.UTF_8)
