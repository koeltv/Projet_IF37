import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
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

val config: JsonNode = jacksonObjectMapper().readTree(File("src/main/resources/actions.json"))