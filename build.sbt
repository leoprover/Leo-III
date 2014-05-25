name := "leo-iii"

version := "1.0"

organization := "org.leo"

// General compiler configuration
scalaVersion := "2.10.4"

scalacOptions ++= Seq("-deprecation", "-feature", "-Xlint")

// Console
initialCommands in console := "import datastructures.tptp.Commons._; import LeoShell._;"

// Tests
libraryDependencies += "junit" % "junit" % "4.11" % "test"

libraryDependencies += "org.scalatest" %% "scalatest" % "2.1.7" % "test"

