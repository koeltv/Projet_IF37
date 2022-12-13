import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import java.io.File

const val JOYSTICK = "JOYSTICK"
const val VOICE = "VOICE"
const val EYE_TRACKING = "EYE_TRACKING"

const val ENABLED = "ENABLED"
const val ACTIONS = "ACTIONS"

const val MAIN_AXIS = "MAIN_AXIS"
const val SECONDARY_AXIS = "SECONDARY_AXIS"
const val CONTROL_MOUSE = "CONTROL_MOUSE"
const val ON_CLICK = "ON_CLICK"
const val BUTTONS = "BUTTONS"

val config: JsonNode = jacksonObjectMapper().readTree(File("src/main/resources/actions.json"))