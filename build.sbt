import scala.sys.process._

val buildParser = taskKey[Unit]("Run ANTLR parser generation.")
val antlrFile = settingKey[File]("The path to the ANTLR grammar file for Leo's parser.")

lazy val commonSettings = Seq(
    version := "1.5",
    scalaVersion := "2.13.4",
    organization := "org.leo",
    test in assembly := {},
    logLevel := Level.Warn,
    logLevel in assembly := Level.Error
)


lazy val leo = (project in file(".")).
  settings(commonSettings:_*).
  settings(
    name := "Leo III",
    description := "A Higher-Order Theorem Prover.",
    libraryDependencies ++= Seq("org.scalatest" %% "scalatest" % "3.2.2" % "test"),
    mainClass in (Compile, run) := Some("leo.Main"),
    mainClass in assembly := Some("leo.Main"),
    mainClass in (Compile, packageBin) := Some("leo.Main"),
    // set stack size to 4m 
    javaOptions += "-Xss4m",
    parallelExecution in Test := false,
    assemblyJarName in assembly := "leo3.jar",
    exportJars := true
  )

// The following are new commands to allow build with debug output
lazy val elideLevel = settingKey[Int]("elide code below this level.")
elideLevel in Global := 501
scalacOptions ++= Seq("-Xelide-below", elideLevel.value.toString)
def compileCommand(name: String, level: Int) =
  Command.command(s"${name}Compile") { s =>
    s"set elideLevel in Global := $level" ::
      "compile" ::
      s"set elideLevel in Global := 501" ::
      s
  }
commands += compileCommand("debug", 0)
def assemblyCommand(name: String, level: Int) =
  Command.command(s"${name}Assembly") { s =>
    s"set elideLevel in Global := $level" ::
      "assembly" ::
      s"set elideLevel in Global := 501" ::
      s
  }
commands += assemblyCommand("debug", 0)
//commands += compileCommand("prod", 1000)
