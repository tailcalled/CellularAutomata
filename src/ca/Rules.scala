package ca

import java.awt.Color

trait Rules {
  
  type Cell
  
  val types: Vector[(String, Cell)]
  
  def step(neighborhood: (Int, Int) => Cell): Cell
  def color(cell: Cell): Color
  
}