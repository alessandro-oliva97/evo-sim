package evo_sim.view.swing.monadic

import cats.effect.IO
import javax.swing.JLabel

class JLabelIO(val jLabel: JLabel) extends JComponentIO[JLabel](jLabel) {
  def textSet(text: String) = IO {jLabel.setText(text)}
  def textGot() = IO {jLabel.getText}
}

//companion object with utilities
object JLabelIO{
  def apply() = IO { new JLabelIO(new JLabel) }
  def apply(text:String) = IO { new JLabelIO(new JLabel(text)) }
}