import org.opencv.core.Mat
import org.opencv.core.MatOfByte
import org.opencv.imgcodecs.Imgcodecs
import java.awt.GraphicsEnvironment
import java.awt.Image
import java.awt.Point
import java.awt.Rectangle
import java.io.ByteArrayInputStream
import javax.imageio.ImageIO

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

fun Mat.toImage(): Image {
    val bytes = MatOfByte()
    Imgcodecs.imencode(".jpg", this, bytes)
    return ImageIO.read(ByteArrayInputStream(bytes.toArray()))
}

operator fun org.opencv.core.Point.minus(point: org.opencv.core.Point): org.opencv.core.Point {
    return org.opencv.core.Point(x - point.x, y - point.y)
}

fun Array<org.opencv.core.Point>.average() = reduce { acc, point ->
    acc + point
}.let { org.opencv.core.Point(it.x / size, it.y / size) }

operator fun org.opencv.core.Point.plus(point: org.opencv.core.Point): org.opencv.core.Point {
    return org.opencv.core.Point(x + point.x, y + point.y)
}

fun Rectangle.horizontalRange() = x..(width - x)

fun Rectangle.verticalRange() = y..(height - y)

fun Double.coerceIn(range: IntRange): Double {
    return coerceIn(range.first.toDouble()..range.last.toDouble())
}