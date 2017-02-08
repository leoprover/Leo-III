lazy val commonSettings = Seq(
    version := "0.1",
    scalaVersion := "2.11.8",

    organization := "org.leo",
    test in assembly := {},

    logLevel := Level.Warn
)


lazy val leo = (project in file(".")).
  enablePlugins(JniNative).
  settings(commonSettings:_*).
  settings(
    name := "Leo III",
    description := "A Higher-Order Theorem Prover.",

    libraryDependencies ++= Seq(
      "org.scala-lang.modules" %% "scala-parser-combinators" % "1.0.4",
      "org.scalatest" %% "scalatest" % "3.0.0" % "test"),
    
    javacOptions ++= Seq("-source", "1.8", "-target", "1.8"),
    scalacOptions += "-target:jvm-1.8",

    mainClass in (Compile, run) := Some("leo.Main"),
    mainClass in (Compile, packageBin) := Some("leo.Main"),
    
    // set stack size to 4m 
    javaOptions += "-Xss4m",
    
    parallelExecution in Test := false,
    
    exportJars := true,
    // options for native bindings
    target in javah := (sourceDirectory in nativeCompile).value / "javah_include"
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


