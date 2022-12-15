import java.awt.Point

data class JoystickState(
    val mainAxis: Point,
    val secondaryAxis: Point,
    val buttons: List<Boolean>
) {
    companion object {
        private val idleX = 489 - 10..489 + 10
        private val idleY = 517 - 10..517 + 10

        private val joystickStateRegex = Regex("Main:(\\d+), (\\d+)\\|Secondary:(\\d+), (\\d+)\\|Buttons:(\\d(, \\d)+)")

        val defaultState = JoystickState(
            512 to 512,
            512 to 512,
            listOf(false, false, false, false, false, false)
        )

        const val MAIN_TRIGGER = 0
        const val SECONDARY_TRIGGER = 1

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

    override fun toString(): String {
        return "Main:${mainAxis.x}, ${mainAxis.y}|Secondary:${secondaryAxis.x}, ${secondaryAxis.y}|Buttons:${
            buttons
                .joinToString(", ")
        }"
    }

    fun mainAxisWasMoved() = mainAxis.x !in idleX || mainAxis.y !in idleY
    fun secondaryAxisWasMoved() = secondaryAxis.x !in idleX || secondaryAxis.y !in idleY
}