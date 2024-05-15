lazy val leo = (project in file("."))
  .settings(
    name := "Leo III",
    description := "A Higher-Order Theorem Prover.",
    version := "1.7.10",
    organization := "org.leo",
    scalaVersion := "2.13.14",
    licenses += "BSD-3-Clause" -> url("https://opensource.org/licenses/BSD-3-Clause"),

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
    //resolvers += "Sonatype S01 OSS Snapshots" at "https://s01.oss.sonatype.org/content/repositories/snapshots",
    libraryDependencies += "io.github.leoprover" %% "scala-tptp-parser" % "1.7.1",
    libraryDependencies ++= Seq("org.scalatest" %% "scalatest" % "3.2.15" % "test"),
    
    nativeImageOptions += s"-H:ReflectionConfigurationFiles=${target.value / "native-image-configs" / "reflect-config.json"}",
    nativeImageOptions += s"-H:ConfigurationFileDirectories=${target.value / "native-image-configs" }",
    nativeImageOptions +="-H:+JNI",
    //nativeImageOptions +="--static",
    //nativeImageOptions +="--libc=musl",
    //nativeImageOptions +="-H:CCompilerPath=/home/lex/dev/casc/x86_64-linux-musl-native/bin/x86_64-linux-musl-gcc",
    //nativeImageOptions +="-H:UseMuslC=/home/lex/dev/casc/x86_64-linux-musl-native",

    Test/parallelExecution := false,
  ).enablePlugins(NativeImagePlugin)

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
