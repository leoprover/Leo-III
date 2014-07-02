name := "leo-iii"

version := "1.0"

scalaVersion := "2.11.1"

//autoScalaLibrary := true

organization := "org.leo"

fork := true

// General compiler configuration
scalaVersion := "2.11.1"

scalacOptions ++= Seq("-deprecation", "-feature", "-Xlint")

// Console
initialCommands in console := "import leo.datastructures.internal; import leo.datastructures.internal.Term._; import LeoShell._;"

// Compile

libraryDependencies += "org.scala-lang" % "scala-compiler" % scalaVersion.value

libraryDependencies += "org.scala-lang" % "scala-library" % scalaVersion.value

libraryDependencies += "org.scala-lang.modules" %% "scala-parser-combinators" % "1.0.1"

// Tests
libraryDependencies += "junit" % "junit" % "4.11" % "test"

libraryDependencies += "org.scalatest" % "scalatest_2.11" % "2.2.0" % "test"
