import fr.dgac.ivy.Ivy
import fr.dgac.ivy.IvyException
import java.beans.PropertyChangeSupport
import java.io.File

/**
 * Handle voice recognition using SRA-5
 */
class VoiceRecognition : Observable, AutoCloseable {
    private val bus: Ivy = Ivy("Voice_recognition", "Voice_recognition is ready", null)

    override val changeSupport = PropertyChangeSupport(this)

    private val actions = config[VOICE][ACTIONS]

    private val voiceRecognition: Process

    init {
        setupVoiceRecognition()

        voiceRecognition = Runtime.getRuntime()
            .exec("src/main/resources/sra5 -b 127.255.255.255:2010 -g src/main/resources/grammar.grxml -p on")

        try {
            bus.start("127.255.255.255:2010")
            bus.bindMsg("^sra5 Parsed=(.*)=(.*) Confidence=(.*) NP=.*") { client, args ->
                println("received: ${args.joinToString(", ")}")
                val action = actions[args[1]].textValue()!!
                val confidence = args[2].replace(",", ".").toFloat()

                if (confidence > config[VOICE][CONFIDENCE].doubleValue()) {
                    println("$action passing !")
                    UserInput.triggerOnce(action)
                } else { // Reconnaissance trop faible
                    sendResponse("Je n'ai pas bien compris, veuillez répéter")
                }
            }

            // Si la parole n'est pas reconnue, on a reconnu qu'on n'a rien reconnu d'où un feedback
            bus.bindMsg("^sra5 Event=Speech_Rejected") { client, args ->
                sendResponse("J'ai été distrait : pourriez-vous répéter s'il vous plaît ?")
            }
        } catch (_: IvyException) {
        }
    }

    private fun setupVoiceRecognition() {
        val voiceActions = config[VOICE][ACTIONS].fields().asSequence().map { (key, value) -> key to value }.toMap()

        val grammarFilePath = "src/main/resources/grammar"
        val grammarFile = File("$grammarFilePath.grxml")
        val grammarFileTemp = File("${grammarFilePath}_temp.grxml")
        var openingEncountered = false
        var closingEncountered = false
        grammarFile.useLines {
            it.forEach { line ->
                if (openingEncountered && line.contains("</one-of>")) {
                    grammarFileTemp.appendText("        <one-of>")
                    voiceActions.forEach { conf ->
                        grammarFileTemp.appendText(
                            """
            <item>${conf.key.lowercase()}
                <tag>out="ordre=${conf.key.uppercase()}"</tag>
            </item>
"""
                        )
                    }
                    closingEncountered = true
                }

                if (!openingEncountered || closingEncountered) {
                    grammarFileTemp.appendText("$line\n")
                }
                if (!openingEncountered) openingEncountered = line.contains("<rule id=\"ordre\">")
            }
        }

        grammarFile.delete()
        grammarFileTemp.renameTo(grammarFile)
    }

    private fun sendResponse(message: String) {
        try {
            bus.sendMsg("ppilot5 Say=$message")
        } catch (_: IvyException) {
        }
    }

    override fun close() {
        voiceRecognition.destroy()
    }
}

fun main() {
    VoiceRecognition()
}