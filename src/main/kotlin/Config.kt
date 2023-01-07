import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import java.io.File

/**
 * Import the configuration from the actions.json file
 */
val config: JsonNode = jacksonObjectMapper().readTree(File("src/main/resources/actions.json"))