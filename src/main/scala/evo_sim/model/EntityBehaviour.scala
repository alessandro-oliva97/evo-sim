package evo_sim.model

import evo_sim.model.BoundingBox.Circle
import evo_sim.model.Entities.{BaseBlob, BaseFood, BaseObstacle, PoisonBlob, SlowBlob}
import evo_sim.model.EntityStructure.{Blob, Entity, Food, Obstacle}

object EntityBehaviour {

  trait Simulable extends Updatable with Collidable //-able suffix refers to behaviour only
  type SimulableEntity = Entity with Simulable

  //Base blob behaviour implementation
  trait BaseBlobBehaviour extends Simulable {
    self: Blob => //BaseBlob
    override def updated(world: World): Set[SimulableEntity] = {
      Set(BaseBlob(Circle(self.movementStrategy(self, world.entities), self.boundingBox.radius),
        self.degradationEffect(self), self.velocity, self.degradationEffect, self.fieldOfViewRadius, self.movementStrategy))
    }

    override def collided(other: SimulableEntity): Set[SimulableEntity] = other match {
      case _: Blob => Set(this)
      case food: BaseFood => food.effect(this)
      case obstacle: BaseObstacle => obstacle.effect(this)
      case _ => Set(this)
    }
  }

  trait SlowBlobBehaviour extends Simulable {
    self: SlowBlob => //SlowBlob
    override def updated(world: World): Set[SimulableEntity] = {
      def newSelf = self.slownessCooldown match {
        case n if n > 1 => SlowBlob(Circle(self.movementStrategy(self, world.entities), self.boundingBox.radius), self.degradationEffect(self), self.velocity, self.degradationEffect,
          self.fieldOfViewRadius, self.movementStrategy, self.slownessCooldown - 1, self.initialVelocity)
        case _ => BaseBlob(Circle(self.movementStrategy(self, world.entities), self.boundingBox.radius), self.degradationEffect(self), self.initialVelocity, self.degradationEffect,
          self.fieldOfViewRadius, self.movementStrategy)
      }

      Set(newSelf)
    }

    override def collided(other: SimulableEntity): Set[SimulableEntity] = other match {
      case _: Blob => Set(this)
      case food: BaseFood => food.effect(this)
      case obstacle: BaseObstacle => obstacle.effect(this)
      case _ => Set(this)
    }
  }

  trait PoisonBlobBehaviour extends Simulable {
    self: PoisonBlob =>

    override def updated(world: World): Set[SimulableEntity] = {
      def newSelf = self.poisonCooldown match {
        case n if n > 1 => PoisonBlob(Circle(self.movementStrategy(self, world.entities), self.boundingBox.radius), self.degradationEffect(self), self.velocity, self.degradationEffect,
          self.fieldOfViewRadius, self.movementStrategy, self.poisonCooldown - 1)
        case _ => BaseBlob(Circle(self.movementStrategy(self, world.entities), self.boundingBox.radius), self.degradationEffect(self), self.velocity, self.degradationEffect,
          self.fieldOfViewRadius, self.movementStrategy)
      }

      Set(newSelf)
    }

    override def collided(other: SimulableEntity): Set[SimulableEntity] = other match {
      case _: Blob => Set(this)
      case food: BaseFood => food.effect(this)
      case obstacle: BaseObstacle => obstacle.effect(this)
      case _ => Set(this)
    }
  }

  trait BaseFoodBehaviour extends Simulable {
    self: Food =>

    override def updated(world: World): Set[SimulableEntity] = {
      val life = self.degradationEffect(this)
      life match {
        case n if n > 0 => Set(BaseFood(self.boundingBox, self.degradationEffect, life, self.effect))
        case _ => Set()
      }
    }

    override def collided(other: SimulableEntity): Set[SimulableEntity] = other match {
      case _: Blob => Set()
      case _ => Set(this)
    }
  }

  trait NeutralBehaviour extends Simulable {
    self: Obstacle =>

    override def updated(world: World): Set[SimulableEntity] = Set(this)

    override def collided(other: SimulableEntity): Set[SimulableEntity] = Set(this)
  }

}
