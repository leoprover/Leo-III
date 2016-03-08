lazy val commonSettings = Seq(
  version := "0.1",
  scalaVersion := "2.11.6"
)

lazy val leo = (project in file(".")).
  settings(commonSettings:_*).
  settings(
    organization := "org.leo",
    
    name := "Leo III",
    
    description := "A Higher-Order Theorem Prover.",
    
    libraryDependencies ++= Seq(
      "org.scala-lang.modules" %% "scala-parser-combinators" % "1.0.4",
      "org.scalatest" %% "scalatest" % "2.2.6" % "test"),
    
    scalacOptions ++= Seq("-Xelide-below","401"),
    
    mainClass in (Compile, packageBin) := Some("leo.Main"),
    
    mainClass in (Compile, run) := Some("leo.Main"),
    
    javaOptions += "-Xss4m",
    
    parallelExecution in Test := false,
    
    logLevel in compile := Level.Warn,
    
    exportJars := true
  )
