import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import java.awt.event.InputEvent
import java.awt.event.KeyEvent
import java.io.File

const val JOYSTICK = "JOYSTICK"
const val VOICE = "VOICE"
const val EYE_TRACKING = "EYE_TRACKING"

const val ENABLED = "ENABLED"
const val ACTIONS = "ACTIONS"

val keyMap = (0..1000000).mapNotNull { i ->
    val key = KeyEvent.getKeyText(i)
    if (key.contains("Unknown")) null
    else key to i
}.toMap()

val mouseAction = mapOf(
    "BUTTON1" to InputEvent.BUTTON1_DOWN_MASK,
    "BUTTON2" to InputEvent.BUTTON2_DOWN_MASK,
    "BUTTON3" to InputEvent.BUTTON3_DOWN_MASK,
)

val config: JsonNode = jacksonObjectMapper().readTree(File("src/main/resources/actions.json"))