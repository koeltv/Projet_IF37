import java.awt.Point

operator fun Point.component1() = x
operator fun Point.component2() = y

fun String.containsAny(wrongChars: CharArray) = any { c -> c in wrongChars }