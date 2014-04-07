import sbt._
import Keys._

object MMBuild extends Build{
  lazy val root = Project(id="root", base=file(".")).aggregate(test1, test2)
  lazy val test1 = Project(id="test1", base=file("test1"))
  lazy val test2 = Project(id="test2", base=file("test2"))
}
