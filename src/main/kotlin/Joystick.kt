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
        }
    }

    override fun run() {
        Thread(this::runButtons).start()
        runJoysticks()
    }

    private fun runJoysticks() {
        while (!Thread.currentThread().isInterrupted) {
            if (config[JOYSTICK][MAIN_AXIS][CONTROL_MOUSE].asBoolean()) {
                if (joystickState.mainAxisWasMoved()) {
                    val newCoordinates =
                        convertToScreenCoordinates(joystickState.mainAxis).fixCoordinates(MouseInfo.getPointerInfo().location.x)
                    UserInput.mouseMove(newCoordinates)
                }
            } else {
                triggerJoystickActions(MAIN_AXIS)
            }

            if (config[JOYSTICK][SECONDARY_AXIS][CONTROL_MOUSE].asBoolean()) {
                if (joystickState.secondaryAxisWasMoved()) {
                    val newCoordinates =
                        convertToScreenCoordinates(joystickState.secondaryAxis).fixCoordinates(MouseInfo.getPointerInfo().location.x)
                    UserInput.mouseMove(newCoordinates)
                }
            } else {
                triggerJoystickActions(SECONDARY_AXIS)
            }
            Thread.sleep(10)
        }
    }

    //Functions used to make zones for the joystick
    val f1 = fun(axis: String, x: Int) = 0.5 * x + config[JOYSTICK][axis][DEFAULT_POSITION][X].intValue()
    val f2 = fun(axis: String, x: Int) = -0.5 * x + config[JOYSTICK][axis][DEFAULT_POSITION][X].intValue()
    val f3 = fun(axis: String, x: Int) = 2 * x + config[JOYSTICK][axis][DEFAULT_POSITION][X].intValue()
    val f4 = fun(axis: String, x: Int) = -2 * x + config[JOYSTICK][axis][DEFAULT_POSITION][X].intValue()

    private fun triggerJoystickActions(axis: String) {
        val unusedMovements = mutableListOf("UP", "DOWN", "LEFT", "RIGHT")
        val usedMovements = mutableListOf<String>()

        val (x, y) = if (axis == MAIN_AXIS) joystickState.mainAxis else joystickState.secondaryAxis
        if (y <= f1(axis, x) && y <= f2(axis, x)) {
            unusedMovements.remove("UP")
            usedMovements.add("UP")
        }
        if (y >= f1(axis, x) && y >= f2(axis, x)) {
            unusedMovements.remove("DOWN")
            usedMovements.add("DOWN")
        }
        if (y <= f3(axis, x) && y <= f4(axis, x)) {
            unusedMovements.remove("LEFT")
            usedMovements.add("LEFT")
        }
        if (y >= f3(axis, x) && y >= f4(axis, x)) {
            unusedMovements.remove("RIGHT")
            usedMovements.add("RIGHT")
        }

        for (movement in usedMovements) {
            UserInput.trigger(config[JOYSTICK][axis][CONTROLS][movement].textValue())
        }
        for (movement in unusedMovements) {
            UserInput.release(config[JOYSTICK][axis][CONTROLS][movement].textValue())
        }
    }

    private fun runButtons() {
        while (!Thread.currentThread().isInterrupted) {
            joystickState.buttons.forEachIndexed { index, buttonPressed ->
                val action = when (index) {
                    JoystickState.MAIN_TRIGGER -> config[JOYSTICK][MAIN_AXIS][ON_CLICK].textValue()
                    JoystickState.SECONDARY_TRIGGER -> config[JOYSTICK][SECONDARY_AXIS][ON_CLICK].textValue()
                    else -> config[JOYSTICK][BUTTONS].toList()[index - 2].textValue()
                }

                if (buttonPressed) UserInput.trigger(action)
                else UserInput.release(action)
            }
            Thread.sleep(10)
        }
    }

    private fun convertToScreenCoordinates(point: Point): Point {
        val (x, y) = MouseInfo.getPointerInfo().location
        return x + (point.x - 489) / 10 to y + (point.y - 517) / 10
    }
}

fun main() {
    val joystick = Joystick()
    Thread(joystick).start()

    //Demo key input
    try {
        val enableDemo = System.getenv("demo").toBoolean()
        if (enableDemo) {
            KeyEventDemo.createAndShowGUI().addListener(joystick)
        }
    } catch (ignored: NullPointerException) {
    }

    //Serial link
    SerialConnection().addListener(joystick)

    //Voice recognition
    val voiceRecognition = VoiceRecognition()
    if (config[VOICE][ENABLED].asBoolean()) {
        voiceRecognition.addListener(joystick)
    }

    //Eye tracking
    if (config[EYE_TRACKING][ENABLED].asBoolean()) {
        ScreenScaleConverter(EyeTracking()).addListener(joystick)
    }

    //When exiting
    Runtime.getRuntime().addShutdownHook(Thread {
        voiceRecognition.close()
    })
}