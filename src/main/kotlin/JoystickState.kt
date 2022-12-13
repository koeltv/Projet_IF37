import java.awt.Point

data class JoystickState(
    val mainAxis: Point,
    val secondaryAxis: Point,
    val buttons: List<Boolean>
) {
    constructor(mainAxis: Pair<Int, Int>, secondaryAxis: Pair<Int, Int>, buttons: List<Boolean>) : this(
        Point(mainAxis.first, mainAxis.second),
        Point(secondaryAxis.first, secondaryAxis.second),
        buttons
    )

    companion object {
        private val joystickStateRegex = Regex("Main:(\\d)+, (\\d)+\\|Secondary:(\\d)+, (\\d)+\\|Buttons:(.+)")

        val defaultState = JoystickState(
            512 to 512,
            512 to 512,
            listOf(false, false, false, false, false, false)
        )

        fun parseFrom(string: String): JoystickState? {
            return joystickStateRegex.matchEntire(string)?.destructured
                ?.let { (mainX, mainY, secondX, secondY, buttons) ->
                    JoystickState(
                        mainX.toInt() to mainY.toInt(),
                        secondX.toInt() to secondY.toInt(),
                        buttons.split(", ").map { s -> s.toBooleanStrict() }
                    )
                }
        }
    }

    override fun toString(): String {
        return "Main:${mainAxis.x}, ${mainAxis.y}|Secondary:${secondaryAxis.x}, ${secondaryAxis.y}|Buttons:${
            buttons.joinToString(
                ", "
            )
        }"
    }
}