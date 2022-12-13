import java.awt.MouseInfo
import java.awt.Point
import java.beans.PropertyChangeEvent
import java.beans.PropertyChangeListener

class Joystick : PropertyChangeListener, Runnable {
    private var joystickState = JoystickState.defaultState

    override fun propertyChange(evt: PropertyChangeEvent) {
        when (val event = evt.newValue) {
            is JoystickState -> {
                joystickState = event
            }
            is Point -> {
                if (evt.propertyName == MAIN_AXIS) {
                    joystickState = JoystickState(event, joystickState.secondaryAxis, joystickState.buttons)
                } else if (evt.propertyName == SECONDARY_AXIS) {
                    joystickState = JoystickState(joystickState.mainAxis, event, joystickState.buttons)
                }
            }
            else -> error("Unknown event: $event")
        }
    }

    override fun run() {
        Thread(this::runButtons).start()
        while (!Thread.currentThread().isInterrupted) {
            if (config[JOYSTICK][MAIN_AXIS][CONTROL_MOUSE].asBoolean()) {
                if (joystickState.mainAxisWasMoved()) {
                    val newCoordinates =
                        convertToScreenCoordinates(joystickState.mainAxis).fixCoordinates(MouseInfo.getPointerInfo().location.x)
                    UserInput.mouseMove(newCoordinates)
                }
            }

            if (config[JOYSTICK][SECONDARY_AXIS][CONTROL_MOUSE].asBoolean()) {
                if (joystickState.secondaryAxisWasMoved()) {
                    val newCoordinates =
                        convertToScreenCoordinates(joystickState.secondaryAxis).fixCoordinates(MouseInfo.getPointerInfo().location.x)
                    UserInput.mouseMove(newCoordinates)
                }
            }
        }
    }

    private fun runButtons() {
        joystickState.buttons.forEachIndexed { index, buttonPressed ->
            val action = when (index) {
                JoystickState.MAIN_TRIGGER -> config[JOYSTICK][MAIN_AXIS][ON_CLICK].textValue()
                JoystickState.SECONDARY_TRIGGER -> config[JOYSTICK][SECONDARY_AXIS][ON_CLICK].textValue()
                else -> config[JOYSTICK][BUTTONS].toList()[index-2].textValue()
            }

            if (buttonPressed) UserInput.trigger(action)
            else UserInput.release(action)
        }
    }

    private fun convertToScreenCoordinates(point: Point): Point {
        val (x, y) = MouseInfo.getPointerInfo().location
        return x + (point.x - 512) to y + (point.y - 512)
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