import java.awt.MouseInfo
import java.awt.Point
import java.awt.Robot
import java.awt.event.InputEvent
import java.awt.event.KeyEvent

/**
 * Handles all actions from the keyboard or mouse.
 */
object UserInput {
    private val robot = Robot()

    private val keyPressed = mutableSetOf<String>()

    private val keyMap = (0..1000000).mapNotNull { i ->
        val key = KeyEvent.getKeyText(i)
        if (key.contains("Unknown")) null
        else key to i
    }.toMap()

    private val mouseAction = mapOf(
        "BUTTON1" to InputEvent.BUTTON1_DOWN_MASK,
        "BUTTON2" to InputEvent.BUTTON2_DOWN_MASK,
        "BUTTON3" to InputEvent.BUTTON3_DOWN_MASK,
    )

    fun trigger(action: String) {
        if (action.isNotBlank() && action !in keyPressed) {
            if (action in keyMap) robot.keyPress(keyMap[action]!!)
            else robot.mousePress(mouseAction[action]!!)
            keyPressed.add(action)
        }
    }

    fun triggerOnce(action: String) {
        if (action.isNotBlank() && action in keyMap) {
            robot.keyPress(keyMap[action]!!)
            robot.keyRelease(keyMap[action]!!)
        } else {
            robot.mousePress(mouseAction[action]!!)
            robot.mouseRelease(mouseAction[action]!!)
        }
    }

    fun release(action: String) {
        if (action in keyMap) robot.keyRelease(keyMap[action]!!)
        else robot.mouseRelease(mouseAction[action]!!)
        keyPressed.remove(action)
    }

    private fun mouseMove(point: Point) {
        robot.mouseMove(point.x, point.y)
    }

    fun mouseMoveToScreenCoordinates(point: Point) {
        val newCoordinates = convertToScreenCoordinates(point).fixCoordinates(MouseInfo.getPointerInfo().location.x)
        mouseMove(newCoordinates)
    }

    private fun convertToScreenCoordinates(point: Point): Point {
        val (x, y) = MouseInfo.getPointerInfo().location
        return x + (point.x - 489) / 10 to y + (point.y - 517) / 10
    }
}