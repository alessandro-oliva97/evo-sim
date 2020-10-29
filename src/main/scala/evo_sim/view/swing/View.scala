package evo_sim.view.swing

import java.awt.Toolkit.getDefaultToolkit
import java.awt.{BorderLayout, Dimension}

import cats.effect.IO
import evo_sim.model.World.{WorldHistory, fromIterationsToDays}
import evo_sim.model.{Environment, World}
import evo_sim.view.View
import evo_sim.view.swing.custom.components.ShapesPanel
import evo_sim.view.swing.effects.InputViewEffects.inputViewCreated
import evo_sim.view.swing.monadic.{JFrameIO, JLabelIO, JPanelIO}
import javax.swing.WindowConstants.EXIT_ON_CLOSE
import javax.swing._

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Promise}

object View extends View {

  val frame = new JFrameIO(new JFrame("evo-sim"))

  override def inputReadFromUser(): IO[Environment] = for {
    environmentPromise <- IO pure { Promise[Environment]() }
    inputPanel <- inputViewCreated(environmentPromise)
    cp <- frame.contentPane()
    _ <- cp.added(inputPanel, BorderLayout.CENTER)
    _ <- frame.defaultCloseOperationSet(EXIT_ON_CLOSE)
    _ <- frame.packedInvokingAndWaiting()
    _ <- frame.visibleInvokingAndWaiting(true)
    environment <- IO { Await.result(environmentPromise.future, Duration.Inf) }
    _ <- frame.addComponentAdapterInvokingAndWaiting()
    dimension <- IO { new Dimension(getDefaultToolkit.getScreenSize.width, getDefaultToolkit.getScreenSize.height) }
    _ <- frame.setPreferredSizeInvokingAndWaiting(dimension)
    _ <- frame.resizableInvokingAndWaiting(true)
  } yield environment

  override def rendered(world: World): IO[Unit] = for {
    barPanel <- JPanelIO()
    entityPanel <- JPanelIO()
    _ <- indicatorsUpdated(world, barPanel)
    shapes <- IO { new JPanelIO(new ShapesPanel(world)) }
    _ <- entityPanel.added(shapes)
    cp <- frame.contentPane()
    _ <- cp.allRemovedInvokingAndWaiting()
    _ <- cp.addedInvokingAndWaiting(barPanel, BorderLayout.NORTH)
    _ <- cp.addedInvokingAndWaiting(entityPanel, BorderLayout.CENTER)
    _ <- frame.packedInvokingAndWaiting()
  } yield ()

  //inside rendered
  private def indicatorsUpdated(world:World, barPanel:JPanelIO): IO[Unit] = {
    for {
      //barPanel has default FlowLayout
      _ <- barPanel.allRemoved()
      days <- JLabelIO("days: " + fromIterationsToDays(world.currentIteration) + " / " + fromIterationsToDays(world.totalIterations))
      _ <- barPanel.added(days)
      population <- JLabelIO("population: " + world.entities.size)
      _ <- barPanel.added(population)
      population <- JLabelIO("temp: " + world.temperature)
      _ <- barPanel.added(population)
      population <- JLabelIO("luminosity: " + world.luminosity)
      _ <- barPanel.added(population)
    } yield ()
    //refactor repeated code for jlabels
  }

  override def resultViewBuiltAndShowed(world: WorldHistory): IO[Unit] = for {
    _ <- IO {}
    // TODO grafici
  } yield ()
}
