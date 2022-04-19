lazy val leo = (project in file("."))
  .settings(
    name := "Leo III",
    description := "A Higher-Order Theorem Prover.",
    version := "1.6.10",
    organization := "org.leo",
    scalaVersion := "2.13.8",

    test in assembly := {},
    logLevel := Level.Warn,
    logLevel in assembly := Level.Error,
    mainClass in (Compile, run) := Some("leo.Main"),
    mainClass in assembly := Some("leo.Main"),
    mainClass in (Compile, packageBin) := Some("leo.Main"),

    scalacOptions ++= Seq(
      "-deprecation",
      "-feature",
    ),

    libraryDependencies += "io.github.leoprover" %% "scala-tptp-parser" % "1.6",
    libraryDependencies ++= Seq("org.scalatest" %% "scalatest" % "3.2.10" % "test"),

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
