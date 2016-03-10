package ca.rules

import ca.Rules
import java.awt.Color

object GoL extends Rules {
  
  type Cell = Boolean
  
  val types = Vector(("Dead", false), ("Alive", true))
  
  def step(alive: (Int, Int) => Boolean) = {
    val n = (-1 to 1).map(x => (-1 to 1).map(y => if (alive(x, y)) 1 else 0).sum).sum
    if (alive(0, 0)) {
      3 <= n && n <= 4
    }
    else {
      n == 3
    }
  }
  def color(alive: Boolean) =
    if (alive) Color.BLUE
    else Color.WHITE
  
}