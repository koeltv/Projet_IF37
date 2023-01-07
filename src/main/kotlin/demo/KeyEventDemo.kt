package demo

import ConfigOption.MAIN_AXIS
import JoystickState
import Observable
import to
import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.Point
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.awt.event.KeyEvent
import java.awt.event.KeyListener
import java.beans.PropertyChangeSupport
import javax.swing.*

/**
 * Demo created to show how this software works without having the Joystick connected.
 * Can control the main axis using the arrow keys.
 */
class KeyEventDemo(name: String?) : JFrame(name), KeyListener, ActionListener, Observable {
    private var displayArea: JTextArea? = null
    private var typingArea: JTextField? = null
    override val changeSupport = PropertyChangeSupport(this)

    private fun addComponentsToPane() {
        val button = JButton("Clear")
        button.addActionListener(this)
        typingArea = JTextField(20)
        typingArea!!.addKeyListener(this)
        displayArea = JTextArea()
        displayArea!!.isEditable = false
        val scrollPane = JScrollPane(displayArea)
        scrollPane.preferredSize = Dimension(375, 125)
        contentPane.add(typingArea!!, BorderLayout.PAGE_START)
        contentPane.add(scrollPane, BorderLayout.CENTER)
        contentPane.add(button, BorderLayout.PAGE_END)
    }

    /** Handle the key typed event from the text field.  */
    override fun keyTyped(e: KeyEvent) {
//        displayInfo(e, "KEY TYPED: ")
    }

    /** Handle the key pressed event from the text field.  */
    override fun keyPressed(e: KeyEvent) {
        if (e.isActionKey) {
            displayInfo(e, "KEY PRESSED: ")
            val newCoordinates = when (e.keyCode) {
                KeyEvent.VK_UP -> 512 to 502
                KeyEvent.VK_DOWN -> 512 to 522
                KeyEvent.VK_LEFT -> 502 to 512
                KeyEvent.VK_RIGHT -> 522 to 512
                else -> error("Unknown action")
            }
            firePropertyChange(MAIN_AXIS(), new = Point(newCoordinates))
        }
    }

    /** Handle the key released event from the text field.  */
    override fun keyReleased(e: KeyEvent) {
        if (e.isActionKey) {
            displayInfo(e, "KEY RELEASED: ")
            firePropertyChange("KEY_TYPED", new = JoystickState.defaultState)
        }
    }

    /** Handle the button click.  */
    override fun actionPerformed(e: ActionEvent) {
        displayArea!!.text = ""
        typingArea!!.text = ""
        typingArea!!.requestFocusInWindow()
    }

    /**
     * Display key event in the display area.
     * @param e the concerned key
     * @param keyStatus the status of the key
     */
    private fun displayInfo(e: KeyEvent, keyStatus: String) {
        val id = e.id
        val keyString = if (id == KeyEvent.KEY_TYPED) {
            "key character = '${e.keyChar}'"
        } else {
            val keyCode = e.keyCode
            "key code = $keyCode (${KeyEvent.getKeyText(keyCode)})"
        }
        var actionString = "action key? "
        actionString += if (e.isActionKey) {
            "YES"
        } else {
            "NO"
        }

        displayArea!!.append("$keyStatus\n\t$keyString\n\t$actionString\n")
        displayArea!!.caretPosition = displayArea!!.document.length
    }

    companion object {
        fun createAndShowGUI(): KeyEventDemo {
            val frame = KeyEventDemo("KeyEventDemo")
            frame.defaultCloseOperation = EXIT_ON_CLOSE
            frame.addComponentsToPane()
            frame.pack()
            frame.isVisible = true
            return frame
        }
    }
}