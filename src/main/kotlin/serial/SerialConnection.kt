package serial

import JoystickState
import Observable
import com.fazecast.jSerialComm.SerialPort
import com.fazecast.jSerialComm.SerialPortDataListener
import com.fazecast.jSerialComm.SerialPortEvent
import java.beans.PropertyChangeSupport

/**
 * Handle connection with the Joystick via a serial connection.
 */
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

                /**
                 * Only listen to event concerning received data
                 */
                override fun getListeningEvents() = SerialPort.LISTENING_EVENT_DATA_RECEIVED

                /**
                 * Store each received byte in the buffer until a line return is received,
                 * then remove the message from the buffer and fire a property change.
                 */
                override fun serialEvent(event: SerialPortEvent) {
                    event.receivedData.forEach { byte -> buffer.add(byte) }
                    if ('\n'.code.toByte() in buffer) {
                        val message = buffer.takeWhile { it != '\r'.code.toByte() }
                        message.forEach { _ -> buffer.removeFirst() }
                        buffer.removeFirst()
                        buffer.removeFirst()

                        val properMessage = message.joinToString("") { byte -> byte.toInt().toChar().toString() }
                        println(properMessage)
                        firePropertyChange("", new = JoystickState.parseFrom(properMessage))
                    }

                }
            })
        }
    }
}