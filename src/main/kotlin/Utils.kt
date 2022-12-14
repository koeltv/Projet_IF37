import org.opencv.core.Mat
import org.opencv.core.MatOfByte
import org.opencv.imgcodecs.Imgcodecs
import java.awt.Image
import java.awt.Point
import java.awt.Rectangle
import java.io.ByteArrayInputStream
import javax.imageio.ImageIO

operator fun Point.component1() = x
operator fun Point.component2() = y

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

infix fun Int.to(int: Int) = Point(this, int)

infix fun Int.byAbout(margin: Int) = this-margin..this+margin