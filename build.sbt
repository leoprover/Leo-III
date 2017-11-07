import scala.sys.process._

val buildParser = taskKey[Unit]("Run ANTLR parser generation.")
val antlrFile = settingKey[File]("The path to the ANTLR grammar file for Leo's parser.")

lazy val commonSettings = Seq(
    version := "1.1",
    scalaVersion := "2.12.4",
    organization := "org.leo",
    test in assembly := {},
    logLevel := Level.Warn,
    logLevel in assembly := Level.Error
)

lazy val leo = (project in file(".")).
//  enablePlugins(JniNative).
  settings(commonSettings:_*).
  settings(
    name := "Leo III",
    description := "A Higher-Order Theorem Prover.",
    libraryDependencies ++= Seq("org.scalatest" %% "scalatest" % "3.0.0" % "test"),
    javacOptions ++= Seq("-source", "1.8", "-target", "1.8"),
    scalacOptions += "-target:jvm-1.8",
    mainClass in (Compile, run) := Some("leo.Main"),
    mainClass in assembly := Some("leo.Main"),
    mainClass in (Compile, packageBin) := Some("leo.Main"),
    // set stack size to 4m 
    javaOptions += "-Xss4m",
    parallelExecution in Test := false,
    assemblyJarName in assembly := "leo3.jar",
    exportJars := true,
    // options for native bindings
//    target in javah := (sourceDirectory in nativeCompile).value / "javah_include",
    // antlr related stuff
    excludeFilter in unmanagedJars := HiddenFileFilter || "antlr4-tool.jar",
    antlrFile := baseDirectory.value / "contrib" / "tptp.g4",
    buildParser := {
      val log = streams.value.log
      val cachedBuild = FileFunction.cached(streams.value.cacheDirectory / "antlr4", FilesInfo.lastModified, FilesInfo.exists) {
        in =>
          print("Generating parser from tptp grammar ...")
          val target = (javaSource in Compile).value / "leo" / "modules" / "parsers" / "antlr"
          val args: Seq[String] = Seq("-cp", Path.makeString(Seq(unmanagedBase.value / "antlr4-tool.jar")),
            "org.antlr.v4.Tool",
            "-o", target.toString) ++ in.map(_.toString)
          val exitCode = Process("java", args) ! log
          if (exitCode != 0) sys.error(s"ANTLR build failed") else println("successful!")
          print("Cleaning temporary files ...")
          val exitCode2 = Process("rm", Seq((target / "tptp.tokens").toString, (target / "tptpLexer.tokens").toString)) ! log 
          if (exitCode2 != 0) println("cleanup failed.") else println("done!")
          (target ** "*.java").get.toSet
      }
      cachedBuild(Set(antlrFile.value))
    }
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

