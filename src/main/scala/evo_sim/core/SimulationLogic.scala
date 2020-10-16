package evo_sim.core

import evo_sim.model.EntityBehaviour.SimulableEntity
import evo_sim.model.Intersection.intersected
import evo_sim.model.World
import evo_sim.model.World.worldEnvironmentUpdated

//maybe this object inside SimulationEngine
object SimulationLogic {
  /*def worldUpdated(world: World): World =
    World(
      world.width,
      world.height,
      world.currentIteration + 1,
      world.entities.foldLeft(Set[SimulableEntity]())((updatedEntities, entity) => updatedEntities ++ entity.updated(world)),
      world.totalIterations
    )*/

  def worldUpdated(world: World): World = {
    val environmentModifiers = worldEnvironmentUpdated(world)

    world.copy(
      temperature = world.temperature + environmentModifiers.temperature,
      luminosity = world.luminosity + environmentModifiers.luminosity,
      currentIteration = world.currentIteration + 1,
      entities = world.entities.foldLeft(Set[SimulableEntity]())((updatedEntities, entity) => updatedEntities ++ entity.updated(world))
    )
  }

  def collisionsHandled(world: World): World = {
    def collisions = for {
      i <- world.entities
      j <- world.entities
      if i != j && intersected(i.boundingBox, j.boundingBox)
    } yield (i, j)

    def collidingEntities = collisions.map(_._1)

    def entitiesAfterCollision =
      collisions.foldLeft(world.entities -- collidingEntities)((entitiesAfterCollision, collision) => entitiesAfterCollision ++ collision._1.collided(collision._2))

    World(
      world.temperature,
      world.luminosity,
      world.width,
      world.height,
      world.currentIteration,
      entitiesAfterCollision,
      world.totalIterations
    )
  }
}
