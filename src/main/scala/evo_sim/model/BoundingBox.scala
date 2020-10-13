package evo_sim.model

import BoundingBox._

case class Point2D(x: Int, y:Int)

//Bounding Box trait, Point(centered)
trait BoundingBox {
  def point: Point2D
}

object BoundingBox {
  //case classes for the different types of Bounding Boxes
  //Circle: Point & Radius
  case class Circle private[BoundingBox](point: Point2D, radius: Int) extends BoundingBox
  //Rectangle: Point & w + h
  case class Rectangle private[BoundingBox](point: Point2D, width: Int, height: Int) extends BoundingBox
  //Triangle: Point & h + angle (angle is defaulted as 60 -> equilateral), apothem = h/3*2 -> circe radius circumscribed in the triangle
  case class Triangle private[BoundingBox](point: Point2D, height: Int, angle: Double = 60.0) extends BoundingBox
  //apply methods
  def apply(point: Point2D, radius: Int): Circle = Circle(point, radius)
  def apply(point: Point2D, width: Int, height: Int): Rectangle = Rectangle(point, width, height)
  def apply(point: Point2D, height: Int, angle : Double): Triangle = Triangle(point, height, angle)
}

//creation bounding boxes
/*
val circle = Circle((1,2), 4)
println(circle.point + ", " + circle.radius + "->" + circle.getClass)
val rect = Rectangle((0,0), 5, 5)
println(rect.point + ", " + rect.width + ", " + rect.height + "->" + rect.getClass)
val tri = Triangle((7,7), 8)
println(tri.point + ", " + tri.height + ", " + tri.angle + "->" + tri.getClass)*/

object Intersection {

  //Intersection between a circle and any other entity (Circle, Rect, Triangle)
  def intersected(body1: BoundingBox, body2: BoundingBox): Boolean = (body1, body2) match {
    case (body1: Circle, circle: Circle) => circleIntersectsCircle(body1, circle)
    case (body1: Circle, rectangle: Rectangle) => circleIntersectsRectangle(body1, rectangle)
    case (body1: Circle, triangle: Triangle) => circleIntersectsTriangle(body1, triangle)
    case (body1: Rectangle, circle: Circle) => circleIntersectsRectangle(circle, body1)
    case (body1: Rectangle, rectangle: Rectangle) => rectangleIntersectsRectangle(body1, rectangle)
    case (body1: Rectangle, triangle: Triangle) => rectangleIntersectsTriangle(body1, triangle)
    case (body1: Triangle, circle: Circle) => circleIntersectsTriangle(circle, body1)
    case (body1: Triangle, rectangle: Rectangle) => rectangleIntersectsTriangle(rectangle, body1)
    case (body1: Triangle, triangle: Triangle) =>triangleIntersectsTriangle(body1, triangle)
    case _ => false
  }

  // distance between centers, then check if is less than the sum of both the circle radius
  // https://stackoverflow.com/questions/8367512/how-do-i-detect-intersections-between-a-circle-and-any-other-circle-in-the-same
  private def circleIntersectsCircle(circle1: Circle, circle2: Circle) =
    Math.hypot(circle1.point.x - circle2.point.x, circle1.point.y - circle2.point.y) < (circle1.radius + circle2.radius)

  // Treat the triangle as a circle, simpler collision, apothem * 2 = circe radius circumscribed in the triangle
  private def circleIntersectsTriangle(circle: Circle, triangle: Triangle) =
    Math.hypot(circle.point.x - triangle.point.x, circle.point.y - triangle.point.y) < (circle.radius + triangle.height / 3 * 2)

  private def circleIntersectsRectangle(circle: Circle, rectangle: Rectangle) =
    rectangle.point.x + rectangle.width > circle.point.x && rectangle.point.y + rectangle.height > circle.point.y &&
      circle.point.x + circle.radius > rectangle.point.x && circle.point.y + circle.radius > rectangle.point.y

  // Collision between two rectangles (https://developer.mozilla.org/en-US/docs/Games/Techniques/2D_collision_detection#:~:text=One%20of%20the%20simpler%20forms,a%20collision%20does%20not%20exist.)
  /**
   * Bx + Bw > Ax &&
   * By + Bh > Ay &&
   * Ax + Aw > Bx &&
   * Ay + Ah > By;
   */
  private def rectangleIntersectsRectangle(rectangle1: Rectangle, rectangle2: Rectangle) = false

  private def rectangleIntersectsTriangle(rectangle: Rectangle, triangle: Triangle) = false

  private def triangleIntersectsTriangle(triangle1: Triangle, triangle2: Triangle) = false


}
