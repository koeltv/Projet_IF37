import com.fazecast.jSerialComm.SerialPort
import com.fazecast.jSerialComm.SerialPortEvent
import com.fazecast.jSerialComm.SerialPortMessageListener
import java.beans.PropertyChangeListener
import java.beans.PropertyChangeSupport

class SerialConnection(portDescription: String = "COM3") {
    private val serialPort = SerialPort.getCommPort(portDescription)
    private val changeSupport = PropertyChangeSupport(this)

    fun addListener(changeListener: PropertyChangeListener) {
        changeSupport.addPropertyChangeListener(changeListener)
    }

    init {
        serialPort.openPort()
        serialPort.addDataListener(object : SerialPortMessageListener {
            override fun getListeningEvents() = SerialPort.LISTENING_EVENT_DATA_RECEIVED
            override fun getMessageDelimiter(): ByteArray = byteArrayOf('\n'.code.toByte())
            override fun delimiterIndicatesEndOfMessage() = true

            override fun serialEvent(event: SerialPortEvent) {
                val wrongChars = "ﾊﾩVﾆ\u0019ﾋQ|ﾘ\uFFC8\u0016b\u0010\uFFC1\u0001�".toCharArray()
                val delimitedMessage = event.receivedData.map { byte -> byte.toInt().toChar() }.joinToString("").removeSuffix("\n")
                if (delimitedMessage.containsAny(wrongChars)) return
                println("Message is: $delimitedMessage")
                changeSupport.firePropertyChange("", null, JoystickState.parseFrom(delimitedMessage))
            }
        })
    }
}

fun main() {
    SerialConnection()
}