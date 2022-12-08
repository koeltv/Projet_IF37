import com.fazecast.jSerialComm.SerialPort
import com.fazecast.jSerialComm.SerialPortEvent
import com.fazecast.jSerialComm.SerialPortMessageListener

fun main() {
    val comPort = SerialPort.getCommPort("COM3")
    comPort.openPort()

    comPort.addDataListener(object : SerialPortMessageListener {
        override fun getListeningEvents() = SerialPort.LISTENING_EVENT_DATA_RECEIVED

        override fun getMessageDelimiter(): ByteArray = byteArrayOf('\n'.code.toByte())

        override fun delimiterIndicatesEndOfMessage() = true

        override fun serialEvent(event: SerialPortEvent) {
            val wrongChars = "ﾩVﾆ\u0019ﾋQ|ﾘ\uFFC8\u0016b\u0010\uFFC1\u0001�".toCharArray()
            val delimitedMessage = event.receivedData.map { byte -> byte.toInt().toChar() }.joinToString("").removeSuffix("\n")
            if (delimitedMessage.containsAny(wrongChars)) return
            println("Message is: $delimitedMessage")
        }
    })
}

private fun String.containsAny(wrongChars: CharArray) = any { c -> c in wrongChars }
