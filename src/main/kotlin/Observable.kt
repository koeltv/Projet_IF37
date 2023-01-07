import java.awt.Point
import java.beans.PropertyChangeListener
import java.beans.PropertyChangeSupport

/**
 * Interface used for modules to be linked to the Joystick.
 * Each module that affect the Joystick should implement this interface or use UserInput to trigger actions.
 * Change can be transmitted as JoystickState for a change on the whole Joystick or as a Point for 1 of the axis.
 * @see Joystick
 * @see UserInput
 */
interface Observable {
    val changeSupport: PropertyChangeSupport

    fun addListener(changeListener: PropertyChangeListener) {
        changeSupport.addPropertyChangeListener(changeListener)
    }

    fun firePropertyChange(name: String, old: JoystickState? = null, new: JoystickState?) {
        changeSupport.firePropertyChange(name, old, new)
    }

    fun firePropertyChange(name: String, old: Point? = null, new: Point?) {
        changeSupport.firePropertyChange(name, old, new)
    }
}