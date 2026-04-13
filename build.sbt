ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.3.7"

lazy val root = (project in file("."))
  .settings(
    name := "HW9 Password Cracking"
  )

libraryDependencies += "org.scala-lang.modules"%% "scala-parallel-collections" %  "1.2.0"