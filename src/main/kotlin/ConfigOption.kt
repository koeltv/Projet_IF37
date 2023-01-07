/**
 * Config options found in the actions.json config file.
 * Used to make config access more secure.
 * Invoke with "CONFIG_OPTION()".
 */
enum class ConfigOption {
    JOYSTICK,
    VOICE,
    EYE_TRACKING,

    ENABLED,
    ACTIONS,

    MAIN_AXIS,
    SECONDARY_AXIS,
    CONTROL_MOUSE,
    CONTROLS,
    ON_CLICK,
    BUTTONS,
    CONFIDENCE,

    DEFAULT_POSITION,
    MARGIN,
    X,
    Y,

    UP,
    DOWN,
    LEFT,
    RIGHT,

    AXIS;

    operator fun invoke() = toString()
}