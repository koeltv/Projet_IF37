import java.awt.GraphicsEnvironment
import java.awt.Point

operator fun Point.component1() = x
operator fun Point.component2() = y

fun String.containsAny(wrongChars: CharArray) = any { c -> c in wrongChars }

private val deadZones: List<IntRange> = GraphicsEnvironment.getLocalGraphicsEnvironment().screenDevices
    .map { screen -> screen.configurations[0].bounds }.let { screenBounds ->
        val deadZones = mutableListOf<IntRange>()
        for (i in 1..screenBounds.lastIndex) {
            deadZones += screenBounds[i - 1].width..screenBounds[i].x
        }
        deadZones
    }

/**
 * If there is more than 1 screen, take into account the possible "dead zone" between them.
 */
fun Pair<Int, Int>.fixCoordinates(oldX: Int): Pair<Int, Int> {
    val deadZone = deadZones.find { deadZone -> first in deadZone }
    return if (deadZone != null) {
        val newX = if (oldX < deadZone.first) deadZone.last else deadZone.first - 1
        newX to this.second
    } else {
        this
    }
}