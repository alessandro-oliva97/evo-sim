package evo_sim.view.swing.monadic

import java.awt.{Component, Container, LayoutManager}

import cats.effect.IO

class ContainerIO(container: Container) extends ComponentIO(container) {
  def added(component: ComponentIO) = IO {
    container.add(component.component)
  }
  def added(name: String, component: Component) = IO {
    container.add(name, component.component)
  }
  def added(component: Component,  constraints : Object) = IO {
    container.add(component.component, constraints)
  }
  def layoutSet(mgr : LayoutManager): IO[Unit] = IO {
    container.setLayout(mgr)
  }
}

//companion object with utilities to be added

