package ca

import javax.swing.JPanel
import java.awt.BorderLayout
import javax.swing.JComponent
import java.awt.Graphics
import java.awt.Color
import javax.swing.BoxLayout
import javax.swing.JList
import javax.swing.event.ListSelectionListener
import javax.swing.event.ListSelectionEvent
import java.awt.event.MouseListener
import java.awt.event.MouseEvent
import java.awt.event.MouseMotionListener
import java.awt.GridLayout
import javax.swing.JButton
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.awt.Dimension
import javax.swing.JLabel

class CellularAutomata(rules: Rules) extends JPanel {
  
  setLayout(new BorderLayout())
  
  private var state = Vector.fill(100, 100)(rules.types(0)._2)
  private def w = state.length
  private def h = state(0).length
  private val z = 8
  private var selected: Option[rules.Cell] = None
  private var speed = 0
  private var desc = new JLabel()
  
  private val comp = new JComponent {
    
    setPreferredSize(new Dimension(w * z, h * z))
    def gw = w * z
    def gh = h * z
    def ox = (getWidth - gw) / 2
    def oy = (getHeight - gh) / 2
    
    override def paintComponent(gfx: Graphics) = {
      gfx.setColor(Color.GRAY)
      gfx.fillRect(0, 0, getWidth, getHeight)
      for (x <- 0 until w; y <- 0 until h) {
        gfx.setColor(rules.color(state(x)(y)))
        gfx.fillRect(x * z + ox, y * z + oy, z, z)
      }
    }
    
    addMouseListener(new MouseListener() {
      def mouseClicked(ev: MouseEvent) = {}
      def mousePressed(ev: MouseEvent): Unit = {
        click((ev.getX() - ox) / z, (ev.getY() - oy) / z)
      }
      def mouseReleased(ev: MouseEvent) = {}
      def mouseEntered(ev: MouseEvent) = {}
      def mouseExited(ev: MouseEvent) = {}
    })
    addMouseMotionListener(new MouseMotionListener() {
      def mouseMoved(ev: MouseEvent) = {
        desc.setText(state((ev.getX() - ox)/ z)((ev.getY() - oy)/ z).toString)
      }
      def mouseDragged(ev: MouseEvent) = {
        click((ev.getX() - ox)/ z, (ev.getY() - oy)/ z)
      }
    })
    
  }
  
  add(comp, BorderLayout.CENTER)
  
  private val menu = new JPanel {
    
    setLayout(new BoxLayout(this, BoxLayout.Y_AXIS))
    
    val options = new JList(rules.types.map(_._1).toArray)
    
    options.addListSelectionListener(new ListSelectionListener() {
      def valueChanged(ev: ListSelectionEvent) = {
        selected = Some(rules.types(options.getSelectedIndex)._2)
      }
    })
    
    add(options)
    
    val speeds = new JPanel(new GridLayout(1, 4))
    
    def speedBtn(label: String, s: Int) = {
      val btn = new JButton(label)
      btn.addActionListener(new ActionListener() {
        def actionPerformed(ev: ActionEvent) = {
          speed = s
        }
      })
      speeds.add(btn)
    }
    
    speedBtn("||", 0)
    val stepBtn = new JButton("|>")
    stepBtn.addActionListener(new ActionListener() {
      def actionPerformed(ev: ActionEvent) = {
        speed = 0
        step()
      }
    })
    speeds.add(stepBtn)
    speedBtn(">", 1)
    speedBtn(">>", 4)
    speedBtn(">>>", 32)
    
    add(speeds)
    
  }
  
  val sidebar = new JPanel(new BorderLayout())
  sidebar.add(menu, BorderLayout.NORTH)
  sidebar.add(desc, BorderLayout.SOUTH)
  add(sidebar, BorderLayout.EAST)

  def click(x: Int, y: Int): Unit = {
    if (x >= 0 && y >= 0 && x < w && y < h) {
      for (s <- selected) {
        state = state.updated(x, state(x).updated(y, s))
      }
    }
    comp.repaint()
  }
  def step() = {
    def locally(x: Int, y: Int) = {
      def res(xl: Int, yl: Int): rules.Cell = {
        if (x + xl < 0) res(xl + w, yl)
        else if (y + yl < 0) res(xl, yl + h)
        else if (x + xl >= w) res(xl - w, yl)
        else if (y + yl >= h) res(xl, yl - h)
        else state(x + xl)(y + yl)
      }
      res _
    }
    state = (0 until w).par.map(x => (0 until h).map(y => rules.step(locally(x, y))).toVector).toVector
    comp.repaint()
  }
  
  val thread = new Thread() {
    var time = 0
    override def run() = {
      while (true) {
        time += speed
        while (time > 2) {
          time -= 2
          step()
        }
        Thread.sleep(1)
      }
    }
  }
  thread.start()
  
}