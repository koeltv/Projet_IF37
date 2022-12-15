import com.fazecast.jSerialComm.SerialPort
import com.fazecast.jSerialComm.SerialPortDataListener
import com.fazecast.jSerialComm.SerialPortEvent
import java.beans.PropertyChangeSupport

class SerialConnection(portDescription: String? = null) : Observable {
    private val serialPort: SerialPort?

    override val changeSupport = PropertyChangeSupport(this)

    init {
        println("Available serial ports:")
        SerialPort.getCommPorts().forEach { port -> println(port.descriptivePortName) }

        serialPort = if (portDescription != null) {
            SerialPort.getCommPort(portDescription)
        } else {
            SerialPort.getCommPorts().getOrNull(0)
        }

        if (serialPort != null) {
            serialPort.openPort()
            serialPort.addDataListener(object : SerialPortDataListener {
                val buffer = mutableListOf<Byte>()

                override fun getListeningEvents() = SerialPort.LISTENING_EVENT_DATA_RECEIVED

                override fun serialEvent(event: SerialPortEvent) {
                    event.receivedData.forEach { byte -> buffer.add(byte) }
                    if ('\n'.code.toByte() in buffer) {
                        val message = buffer.takeWhile { it != '\r'.code.toByte() }
                        message.forEach { _ -> buffer.removeFirst() }
                        buffer.removeFirst()
                        buffer.removeFirst()

                        val properMessage = message.joinToString("") { byte -> byte.toInt().toChar().toString() }
                        println(properMessage)
                        firePropertyChange("", JoystickState.parseFrom(properMessage))
                    }

                }
            })
        }
    }
}