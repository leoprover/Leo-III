val buildParser = taskKey[Unit]("Run ANTLR parser generation.")
val antlrFile = settingKey[File]("The path to the ANTLR grammar file for Leo's parser.")

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
    scalacOptions ++= Seq("-Xelide-below","401"),
    exportJars := true,
    // options for native bindings
    target in javah := (sourceDirectory in nativeCompile).value / "javah_include",
    antlrFile := (resourceDirectory in Compile).value / "tptp.g4",
    buildParser := {
      val cachedBuild = FileFunction.cached(streams.value.cacheDirectory / "antlr4", FilesInfo.lastModified, FilesInfo.exists) {
        in =>
          print("Generating parser from tptp grammar ...")
          val target = (javaSource in Compile).value / "leo" / "modules" / "parsers" / "antlr"
          val args: Seq[String] = Seq("-cp", Path.makeString((unmanagedJars in Compile).value.files),
            "org.antlr.v4.Tool",
            "-o", target.toString) ++ in.map(_.toString)
          val exitCode = Process("java", args) ! streams.value.log
          if (exitCode != 0) sys.error(s"ANTLR build failed") else println("successful!")
          print("Cleaning temporary files ...")
          val exitCode2 = Process("rm", Seq((target / "tptp.tokens").toString, (target / "tptpLexer.tokens").toString)) ! streams.value.log
          if (exitCode2 != 0) println("cleanup failed.") else println("done!")
          (target ** "*.java").get.toSet
      }
      cachedBuild(Set(antlrFile.value))
    }
  )


