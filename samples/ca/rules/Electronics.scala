package ca.rules

import ca.Rules
import java.awt.Color

object Electronics extends Rules {
  
  sealed trait Cell
  case object Empty extends Cell
  case class Wire(voltage: Double) extends Cell
  case class Source(voltage: Double) extends Cell
  
  val types = Vector(("Empty", Empty), ("Wire", Wire(0)), ("+5V", Source(5)), ("GND", Source(0)))
  
  def color(cell: Cell) = cell match {
    case Empty => Color.BLACK
    case Wire(v) =>
      val c = (v/5 * 127).floor.toInt
      new Color(128 + c min 255 max 0, 128 + c min 255 max 0, 128 - c min 255 max 0)
    case Source(v) => if (v > 0) Color.RED else Color.BLUE
  }
  def step(n: (Int, Int) => Cell) = n(0, 0) match {
    case Empty => Empty
    case Source(v) => Source(v)
    case Wire(_) =>
      val ns = Vector((-1, 0), (1, 0), (0, -1), (0, 1))
      val vs = ns.map(n.tupled).collect { case Wire(v) => v; case Source(v) => v }
      if (vs.length > 0) Wire(vs.sum / vs.length)
      else Wire(0)
  }
  
}