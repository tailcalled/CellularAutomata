package ca

import javax.swing.JFrame
import java.awt.Frame
import ca.rules.GoL
import ca.rules.Electronics

object Main {
  
  def main(args: Array[String]) {
    val frame = new JFrame("CA")
    frame.setExtendedState(Frame.MAXIMIZED_BOTH)
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)
    frame.add(new CellularAutomata(GoL))
    frame.setVisible(true)
  }
  
}