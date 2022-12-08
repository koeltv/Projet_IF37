import fr.dgac.ivy.Ivy
import fr.dgac.ivy.IvyException
import java.beans.PropertyChangeSupport

class VoiceRecognition : Observable {
    val bus: Ivy = Ivy("Voice_recognition", "Voice_recognition is ready", null)

    override val changeSupport = PropertyChangeSupport(this)

    init {
        try {
            bus.start("127.255.255.255:2010")
            bus.bindMsg("^sra5 Parsed=(.*)=(.*) Confidence=(.*) NP=.*") { client, args ->
                println("received: ${args.joinToString(", ")}")

                if (args[2].replace(",", ".").toFloat() > 0.70) {
                    println("${args[1]} passing !")
                    robot.keyPress(keyMap[config[args[1]]!!]!!)
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

    private fun sendResponse(message: String) {
        try {
            bus.sendMsg("ppilot5 Say=$message")
        } catch (_: IvyException) {
        }
    }
}

fun main() {
    setupVoiceRecognition()
    VoiceRecognition()
}