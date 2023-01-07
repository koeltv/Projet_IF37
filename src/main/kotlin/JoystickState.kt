import ConfigOption.*
import java.awt.Point

/**
 * Class used to carry the state of the Joystick.
 */
data class JoystickState(
    val mainAxis: Point,
    val secondaryAxis: Point,
    val buttons: List<Boolean>
) {
    companion object {
        private val mainXDefaultState = config[JOYSTICK()][MAIN_AXIS()][DEFAULT_POSITION()][X()].intValue()
        private val mainYDefaultState = config[JOYSTICK()][MAIN_AXIS()][DEFAULT_POSITION()][Y()].intValue()
        private val mainMargin = config[JOYSTICK()][MAIN_AXIS()][DEFAULT_POSITION()][MARGIN()].intValue()

        private val mainIdleXRange = mainXDefaultState byAbout mainMargin
        private val mainIdleYRange = mainYDefaultState byAbout mainMargin

        private val secondaryXDefaultState = config[JOYSTICK()][SECONDARY_AXIS()][DEFAULT_POSITION()][X()].intValue()
        private val secondaryYDefaultState = config[JOYSTICK()][SECONDARY_AXIS()][DEFAULT_POSITION()][Y()].intValue()
        private val secondaryMargin = config[JOYSTICK()][SECONDARY_AXIS()][DEFAULT_POSITION()][MARGIN()].intValue()

        private val secondaryIdleXRange = secondaryXDefaultState byAbout secondaryMargin
        private val secondaryIdleYRange = secondaryYDefaultState byAbout secondaryMargin

        /**
         * Regex used to assert the format of data and extract it from an incoming String.
         */
        private val joystickStateRegex = Regex("Main:(\\d+), (\\d+)\\|Secondary:(\\d+), (\\d+)\\|Buttons:(\\d(, \\d)+)")

        val defaultState = JoystickState(
            mainXDefaultState to mainYDefaultState,
            secondaryXDefaultState to secondaryYDefaultState,
            listOf(false, false, false, false, false, false)
        )

        const val MAIN_TRIGGER = 0
        const val SECONDARY_TRIGGER = 1

        /**
         * Receive an incoming String and format it to a JoystickState if it is valid.
         * @param string the incoming string
         * @return the formatted JoystickState or null if the String couldn't be parsed
         */
        fun parseFrom(string: String): JoystickState? {
            return joystickStateRegex.matchEntire(string)?.destructured
                ?.let { (mainX, mainY, secondX, secondY, buttons) ->
                    JoystickState(
                        mainX.toInt() to mainY.toInt(),
                        secondX.toInt() to secondY.toInt(),
                        buttons.split(", ").map { s -> s == "1" }
                    )
                }
        }
    }

    /**
     * Convert the JoystickState to a String to be displayed.
     */
    override fun toString(): String {
        return "Main:${mainAxis.x}, ${mainAxis.y}|Secondary:${secondaryAxis.x}, ${secondaryAxis.y}|Buttons:${
            buttons
                .joinToString(", ")
        }"
    }

    fun mainAxisWasMoved() = mainAxis.x !in mainIdleXRange || mainAxis.y !in mainIdleYRange
    fun secondaryAxisWasMoved() = secondaryAxis.x !in secondaryIdleXRange || secondaryAxis.y !in secondaryIdleYRange
}