import java.io.File

fun setupVoiceRecognition() {
    var notAddedConfigs = config["VOICE"]["ACTIONS"].fields().asSequence().map { (key, value) -> key to value }.toMap()

    val grammarFilePath = "src/main/resources/grammar"
    val grammarFile = File("$grammarFilePath.grxml")
    val grammarFileTemp = File("${grammarFilePath}_temp.grxml")
    var openingEncountered = false
    grammarFile.useLines {
        it.forEach { line ->
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