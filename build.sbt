lazy val commonSettings = Seq(
    version := "0.1",
    scalaVersion := "2.11.7",

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
      "org.scala-lang" % "scala-compiler" % "2.11.7",
      "org.scalatest" %% "scalatest" % "2.2.6" % Test),
    
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
    target in javah := (sourceDirectory in nativeCompile).value / "javah_include"
  )


