import ConfigOption.*
import java.awt.GraphicsEnvironment
import java.awt.MouseInfo
import java.awt.Point
import java.awt.Robot
import java.awt.event.InputEvent
import java.awt.event.KeyEvent

/**
 * Handles all actions from the keyboard or mouse.
 * Each module that affect the Joystick should use UserInput or implement Observable to trigger actions.
 * @see Joystick
 * @see Observable
 */
object UserInput {
    private val robot = Robot()

    private val keyPressed = mutableSetOf<String>()

    /**
     * Map linking key text to the corresponding KeyEvent index
     */
    private val keyMap = (0..1000000).mapNotNull { i ->
        val key = KeyEvent.getKeyText(i)
        if (key.contains("Unknown")) null
        else key to i
    }.toMap()

    /**
     * Map of the possible mouse actions
     */
    private val mouseAction = mapOf(
        "BUTTON1" to InputEvent.BUTTON1_DOWN_MASK,
        "BUTTON2" to InputEvent.BUTTON2_DOWN_MASK,
        "BUTTON3" to InputEvent.BUTTON3_DOWN_MASK,
    )

    /**
     * Trigger the given action and keep it pressed
     */
    fun trigger(action: String) {
        if (action.isNotBlank() && action !in keyPressed) {
            if (action in keyMap) robot.keyPress(keyMap[action]!!)
            else robot.mousePress(mouseAction[action]!!)
            keyPressed.add(action)
        }
    }

    /**
     * Trigger the given action only once.
     */
    fun triggerOnce(action: String) {
        if (action.isNotBlank() && action in keyMap) {
            robot.keyPress(keyMap[action]!!)
            robot.keyRelease(keyMap[action]!!)
        } else {
            robot.mousePress(mouseAction[action]!!)
            robot.mouseRelease(mouseAction[action]!!)
        }
    }

    /**
     * Release the given action.
     */
    fun release(action: String) {
        if (action in keyMap) robot.keyRelease(keyMap[action]!!)
        else robot.mouseRelease(mouseAction[action]!!)
        keyPressed.remove(action)
    }

    /**
     * Move the mouse to the given point on screen.
     */
    private fun mouseMove(point: Point) {
        robot.mouseMove(point.x, point.y)
    }

    /**
     * Dead zones are an ensemble of coordinates between screen that are not used because of a difference between
     * the expected resolution (ex: 1920x1080) and the actual resolution.
     * We only consider those on the X axis here.
     */
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
    private fun Point.fixCoordinates(oldX: Int): Point {
        val deadZone = deadZones.find { deadZone -> x in deadZone }
        return if (deadZone != null) {
            val newX = if (oldX < deadZone.first) deadZone.last else deadZone.first - 1
            newX to y
        } else {
            this
        }
    }

    /**
     * Convert the given joystick movement to screen coordinates.
     * @param axis the axis to consider
     * @param point the position of the axis
     * @return the screen coordinates to move to
     */
    private fun convertToScreenCoordinates(axis: String, point: Point): Point {
        val (x, y) = MouseInfo.getPointerInfo().location
        return x + (point.x - config[JOYSTICK()][axis][DEFAULT_POSITION()][X()].intValue()) / 10 to y + (point.y - config[JOYSTICK()][axis][DEFAULT_POSITION()][Y()].intValue()) / 10
    }

    /**
     * Mouse the mouse to the screen coordinates corresponding to the axis movement.
     * @param axis the axis to consider
     * @param point the position of the axis
     */
    fun mouseMoveToScreenCoordinates(axis: String, point: Point) {
        val newCoordinates = convertToScreenCoordinates(axis, point).fixCoordinates(MouseInfo.getPointerInfo().location.x)
        mouseMove(newCoordinates)
    }
}