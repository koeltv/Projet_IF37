import java.awt.Point
import java.beans.PropertyChangeListener
import java.beans.PropertyChangeSupport

interface Observable {
    val changeSupport: PropertyChangeSupport

    fun addListener(changeListener: PropertyChangeListener) {
        changeSupport.addPropertyChangeListener(changeListener)
    }

    fun firePropertyChange(name: String, value: JoystickState?) {
        changeSupport.firePropertyChange(name, null, value)
    }

    fun firePropertyChange(name: String, value: Point?) {
        changeSupport.firePropertyChange(name, null, value)
    }
}