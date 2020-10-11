package evo_sim.model

import evo_sim.model.Entities.{BaseBlob, BaseFood, BaseObstacle}
import evo_sim.model.EntityBehaviour.SimulableEntity

case class World(width: Int, height: Int, currentIteration: Int, entities: Set[SimulableEntity])

//companion object
object World {
  def worldCreated(env: Environment): World = {
    val blobs: Set[BaseBlob] = Iterator.fill(env.initialBlobNumber)(BaseBlob(
      boundingBox = BoundingBox.Rectangle.apply(point = Point2D(20, 15), width = 10, height = 10),
      life = 100,
      velocity = 50,
      degradationEffect = DegradationEffect.standardDegradation,
      fieldOfViewRadius = 10,
      movementStrategy = MovingStrategies.baseMovement)).toSet

    val foods: Set[BaseFood] = Iterator.fill(env.initialFoodNumber)(BaseFood(
      boundingBox = BoundingBox.Circle.apply(point = Point2D(100, 100), radius = 10),
      degradationEffect = DegradationEffect.foodDegradation,
      life = 100,
      effect = Effect.standardFoodEffect)).toSet

    val obstacles: Set[BaseObstacle] = Iterator.fill(env.initialObstacleNumber)(BaseObstacle(
      boundingBox = BoundingBox.Triangle(point = Point2D(200, 200), height = 15),
      effect = Effect.neutralEffect)).toSet

    val entities: Set[SimulableEntity] = blobs ++ foods ++ obstacles

    World(width = 100, height = 100, currentIteration = 0, entities = entities)
  }
}



