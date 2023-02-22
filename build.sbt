lazy val leo = (project in file("."))
  .settings(
    name := "Leo III",
    description := "A Higher-Order Theorem Prover.",
    version := "1.7.3",
    organization := "org.leo",
    scalaVersion := "2.13.10",

    logLevel := Level.Warn,

    Compile/mainClass := Some("leo.Main"),
    assembly/mainClass := Some("leo.Main"),
    assembly/assemblyJarName := "leo3.jar",
    assembly/logLevel := Level.Error,
    assembly/test := {},

    scalacOptions ++= Seq(
      "-deprecation",
      "-feature",
    ),
    // set stack size to 4m
    javaOptions ++= Seq(
      "-Xss4m",
      "-Xms512m",
      "-Xmx2g"
    ),
    libraryDependencies += "io.github.leoprover" %% "scala-tptp-parser" % "1.6.5",
    libraryDependencies ++= Seq("org.scalatest" %% "scalatest" % "3.2.15" % "test"),

    Test/parallelExecution := false,
  )

// The following are new commands to allow build with debug output
lazy val elideLevel = settingKey[Int]("elide code below this level.")
Global/elideLevel := 501
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
