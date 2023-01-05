import org.opencv.core.Point
import java.awt.GraphicsEnvironment
import java.awt.Rectangle
import java.beans.PropertyChangeEvent
import java.beans.PropertyChangeListener
import java.beans.PropertyChangeSupport
import java.util.*
import kotlin.math.abs

/**
 * Restrict the eye tracking input to the screen and scale it accordingly.
 */
class ScreenScaleConverter(private val eyeTracking: EyeTracking): PropertyChangeListener {
    private val pcs = PropertyChangeSupport(this)

    private val screenDimensions = GraphicsEnvironment.getLocalGraphicsEnvironment().defaultScreenDevice.defaultConfiguration.bounds

    private var relativeDimensions = screenDimensions

    private var horizontalRatio = 0
    private var verticalRatio = 0

    init {
        eyeTracking.addListener(this)
        initialise()
    }

    fun addListener(changeListener: PropertyChangeListener) {
        pcs.addPropertyChangeListener(changeListener)
    }

    private fun initialise() {
        val scanner = Scanner(System.`in`)
        println("Look at the upper left corner and press enter")
        scanner.nextLine()
        val upperLeft = eyeTracking.getEyesPosition().average()
        println(upperLeft)
        println("Look at the lower right corner and press enter")
        scanner.nextLine()
        val lowerRight = eyeTracking.getEyesPosition().average()
        println(lowerRight)
        initialise(upperLeft, lowerRight)
    }

    private fun initialise(upperLeft: Point, lowerRight: Point) {
        relativeDimensions = Rectangle(
            upperLeft.x.toInt(),
            upperLeft.y.toInt(),
            (lowerRight.x - upperLeft.x).toInt(),
            (lowerRight.y - upperLeft.y).toInt()
        )

        horizontalRatio = screenDimensions.width / relativeDimensions.width
        verticalRatio = screenDimensions.height / relativeDimensions.height
    }

    private fun convert(point: Point): java.awt.Point {
        val x = ((point.x - relativeDimensions.x) * horizontalRatio).coerceIn(screenDimensions.horizontalRange())
        val reversedX = abs(x - screenDimensions.width)
        val y = ((point.y - relativeDimensions.y) * verticalRatio).coerceIn(screenDimensions.verticalRange())
        return java.awt.Point(reversedX.toInt(), y.toInt())
    }

    override fun propertyChange(evt: PropertyChangeEvent) {
        val oldFocusPoint = evt.oldValue
        val focusPoint = evt.newValue
        if (focusPoint is Point && oldFocusPoint is Point) {
            if (abs(oldFocusPoint.x - focusPoint.x) > 0.5 && abs(oldFocusPoint.y - focusPoint.y) > 0.5) {
                val convertedPoint = convert(focusPoint)
                println("$focusPoint, $convertedPoint")
                pcs.firePropertyChange("Eye Tracking", null, JoystickState(convertedPoint, java.awt.Point(0, 0), emptyList()))
            }
        }
    }
}