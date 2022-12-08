import java.awt.event.KeyEvent
import java.io.File

val keyMap = (0..1000000).mapNotNull { i ->
    val key = KeyEvent.getKeyText(i)
    if (key.contains("Unknown")) null
    else key to i
}.toMap()

val config = File("src/main/resources/actions.json").readLines().let { lines ->
    lines.subList(1, lines.lastIndex).associate { entry ->
        val (key, value) = Regex("\"(.+)\" *: *\"(.+)\"").find(entry)!!.destructured
        key to value
    }
}

fun setupVoiceRecognition() {
    var notAddedConfigs = config
    val grammarFilePath = "src/main/resources/grammar"
    val grammarFile = File("$grammarFilePath.grxml")
    val grammarFileTemp = File("${grammarFilePath}_temp.grxml")
    var openingEncountered = false
    grammarFile.useLines { lineSequence ->
        lineSequence.forEach { line ->
            notAddedConfigs = notAddedConfigs.filter { conf -> !line.contains(conf.key.uppercase()) }

            if (openingEncountered && line.contains("</one-of>")) {
                notAddedConfigs.forEach { conf ->
                    grammarFileTemp.appendText(
                        """
            <item>${conf.key.lowercase()}
                <tag>out="ordre=${conf.key.uppercase()}"</tag>
            </item>
"""
                    )
                }
            }
            grammarFileTemp.appendText("$line\n")
            if (!openingEncountered) openingEncountered = line.contains("<rule id=\"ordre\">")
        }
    }

    grammarFile.delete()
    grammarFileTemp.renameTo(grammarFile)

    Runtime.getRuntime()
        .exec("src/main/resources/sra5 -b 127.255.255.255:2010 -g src/main/resources/grammar.grxml -p on")
}