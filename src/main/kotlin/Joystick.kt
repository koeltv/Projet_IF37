import java.awt.MouseInfo
import java.awt.Robot
import java.beans.PropertyChangeEvent
import java.beans.PropertyChangeListener

class Joystick: PropertyChangeListener, Runnable {
    private var joystickState = JoystickState.defaultState

    private val robot = Robot()

    override fun propertyChange(evt: PropertyChangeEvent) {
        val event = evt.newValue
        if (event is JoystickState) {
            joystickState = event
            println(event)
        }
    }

    override fun run() {
        while (!Thread.currentThread().isInterrupted) {
            val (x, y) = MouseInfo.getPointerInfo().location
            if (joystickState.mainAxis.x != 512 || joystickState.mainAxis.y != 512) {
                robot.mouseMove(x + (joystickState.mainAxis.x - 512), y + (joystickState.mainAxis.y - 512))
            }
        }
    }
}

fun main() {
    val joystick = Joystick()
    Thread(joystick).start()

    KeyEventDemo.createAndShowGUI().addListener(joystick)

    SerialConnection("COM3").addListener(joystick)
}