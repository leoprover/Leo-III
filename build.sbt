name := "leo-iii"

version := "1.0"

scalaVersion := "2.10.3"

autoScalaLibrary := true

organization := "org.leo"

fork := true

// General compiler configuration
scalaVersion := "2.10.4"

scalacOptions ++= Seq("-deprecation", "-feature", "-Xlint")

// Console
initialCommands in console := "import leo.datastructures.internal; import leo.datastructures.internal.Term._; import LeoShell._;"

// Compile

//libraryDependencies += "org.scala-lang" % "scala-compiler" % scalaVersion.value "scala-tool"
//libraryDependencies += "org.scala-lang" % "scala-library" % scalaVersion.value

// Tests
libraryDependencies += "junit" % "junit" % "4.11" % "test"

libraryDependencies += "org.scalatest" %% "scalatest" % "2.1.7" % "test"

libraryDependencies += ("org.scala-stm" %% "scala-stm" % "0.7")
