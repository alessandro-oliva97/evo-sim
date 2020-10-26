package evo_sim.view.swing

import cats.effect.IO
import evo_sim.model.{Constants, Environment, World}
import evo_sim.view.View
import evo_sim.view.swing.effects.InputViewEffects._
import evo_sim.view.swing.monadic.{JButtonIO, JPanelIO}
import javax.swing._
import org.jfree.ui.tabbedui.VerticalLayout

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Promise}

object View extends View {

  private val frame = new JFrame("evo-sim")

  override def inputReadFromUser(): IO[Environment] = for {
    environmentPromise <- IO pure { Promise[Environment]() }
    inputPanel <- JPanelIO()
    _ <- inputPanel.layoutSet(new VerticalLayout())
    blobSlider <- createDataInputRow(inputPanel, "#Blob", Constants.MIN_BLOBS, Constants.MAX_BLOBS,
      Constants.DEF_BLOBS)
    plantSlider <- createDataInputRow(inputPanel, "#Plant", Constants.MIN_PLANTS, Constants.MAX_PLANTS,
      Constants.DEF_PLANTS)
    obstacleSlider <- createDataInputRow(inputPanel, "#Obstacle", Constants.MIN_OBSTACLES,
      Constants.MAX_OBSTACLES, Constants.DEF_OBSTACLES)
    luminositySlider <- createDataInputRow(inputPanel, "Luminosity (cd)",
      Constants.SELECTABLE_MIN_LUMINOSITY, Constants.SELECTABLE_MAX_LUMINOSITY, Constants.DEFAULT_LUMINOSITY)
    temperatureSlider <- createDataInputRow(inputPanel, "Temperature (°C)",
      Constants.SELECTABLE_MIN_TEMPERATURE, Constants.SELECTABLE_MAX_TEMPERATURE, Constants.DEF_TEMPERATURE)
    daysSlider <- createDataInputRow(inputPanel, "#Days", Constants.MIN_DAYS, Constants.MAX_DAYS,
      Constants.DEF_DAYS)
    start <- JButtonIO("Start")
    _ <- clickCompletesEnvironmentListenerAdded(start, environmentPromise, temperatureSlider, luminositySlider,
      blobSlider, plantSlider, obstacleSlider, daysSlider, frame)
    //_ <- frame.add(inputPanel, BorderLayout.CENTER)
    //_ <- componentInContentPaneAdded(frame, inputPanel, BorderLayout.CENTER)
    //_ <- componentInContentPaneAdded(frame, start, BorderLayout.SOUTH)
    //_ <- exitOnCloseOperationSet(frame)
    //_ <- isPacked(frame)
    //_ <- isNotResizable(frame)
    //_ <- isVisible(frame)
    environment <- IO { Await.result(environmentPromise.future, Duration.Inf) }
  } yield environment

  override def rendered(world: World): IO[Unit] = for {
    barPanel <- JPanelIO()
    entityPanel <- JPanelIO()
    // TODO statistiche
    shapes <- IO { new JPanelIO(new ShapesPanel(world)) }
    _ <- entityPanel.added(shapes)
    //_ <- allRemoved(frame)
    //_ <- componentInContentPaneAdded(frame, barPanel, BorderLayout.NORTH)
    //_ <- componentInContentPaneAdded(frame, entityPanel, BorderLayout.CENTER)
    //_ <- screenSizeSet(frame)
    //_ <- isPacked(frame)
  } yield ()

  override def resultViewBuiltAndShowed(world: World): IO[Unit] = for {
    _ <- IO {}
    // TODO grafici
  } yield ()
}
