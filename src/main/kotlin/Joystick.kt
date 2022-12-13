import java.awt.MouseInfo
import java.awt.Robot
import java.beans.PropertyChangeEvent
import java.beans.PropertyChangeListener

val robot = Robot()

class Joystick : PropertyChangeListener, Runnable {
    private var joystickState = JoystickState.defaultState

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
            if (config[JOYSTICK]["MAIN_AXIS"]["CONTROL_MOUSE"].asBoolean()) {
                if (joystickState.mainAxis.x != 512 || joystickState.mainAxis.y != 512) {
                    val newCoordinates = (x + (joystickState.mainAxis.x - 512) to y + (joystickState.mainAxis.y - 512)).fixCoordinates(x)

                    robot.mouseMove(newCoordinates.first, newCoordinates.second)
                }
            }

            if (config[JOYSTICK]["SECONDARY_AXIS"]["CONTROL_MOUSE"].asBoolean()) {
                if (joystickState.mainAxis.x != 512 || joystickState.mainAxis.y != 512) {
                    val newCoordinates = (x + (joystickState.mainAxis.x - 512) to y + (joystickState.mainAxis.y - 512)).fixCoordinates(x)

                    robot.mouseMove(newCoordinates.first, newCoordinates.second)
                }
            }

            joystickState.buttons.forEachIndexed { index, buttonPressed ->
                val action = when (index) {
                    JoystickState.MAIN_TRIGGER -> config[JOYSTICK]["MAIN_AXIS"]["ON_CLICK"].textValue()
                    JoystickState.SECONDARY_TRIGGER -> config[JOYSTICK]["SECONDARY_AXIS"]["ON_CLICK"].textValue()
                    else -> config[JOYSTICK]["BUTTONS"].toList()[index].textValue()
                }

                if (action in keyMap) {
                    if (buttonPressed) robot.keyPress(keyMap[action]!!)
                    else robot.keyRelease(keyMap[action]!!)
                } else {
                    if (buttonPressed) robot.mousePress(mouseAction[action]!!)
                    else robot.mouseRelease(mouseAction[action]!!)
                }
            }
        }
    }
}

fun main() {
    val joystick = Joystick()
    Thread(joystick).start()

    //Demo key input
    KeyEventDemo.createAndShowGUI().addListener(joystick)

    //Serial link
    SerialConnection("COM3").addListener(joystick)

    //Voice recognition
    if (config[VOICE][ENABLED].asBoolean()) {
        VoiceRecognition().addListener(joystick)
    }

    //Eye tracking
    if (config[EYE_TRACKING][ENABLED].asBoolean()) {
        ScreenScaleConverter(EyeTracking()).addListener(joystick)
    }
}