lazy val leo = (project in file("."))
  .settings(
    name := "Leo-III",
    description := "A Higher-Order Theorem Prover.",
    version := "1.8.1",
    organization := "org.leo",
    licenses += "BSD-3-Clause" -> url("https://opensource.org/licenses/BSD-3-Clause"),

    scalaVersion := "3.9.0",

    logLevel := Level.Warn,

    Compile/mainClass := Some("leo.Main"),
    assembly/mainClass := Some("leo.Main"),
    assembly/assemblyJarName := s"leo3-${version.value}.jar",
    assembly/logLevel := Level.Error,
    assembly/test := {},

    scalacOptions ++= Seq(
      "-deprecation",
      "-feature"
    ),
    // set stack size to 4m
    javaOptions ++= Seq(
      "-Xss4m",
      "-Xms512m",
      "-Xmx2g"
    ),
    //resolvers += "Sonatype S01 OSS Snapshots" at "https://s01.oss.sonatype.org/content/repositories/snapshots",
    libraryDependencies += "io.github.leoprover" %% "scala-tptp-parser" % "1.7.5",
    libraryDependencies += "io.github.leoprover" %% "logic-embedding-runtime" % "2.0.0",
    libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.20" % "test",
    
    nativeImageOptions += s"-H:ReflectionConfigurationFiles=${baseDirectory.value / "contrib" / "native-image-configs" / "reflect-config.json"}",
    nativeImageOptions += s"-H:ConfigurationFileDirectories=${baseDirectory.value / "contrib" / "native-image-configs" }",
    nativeImageOptions +="-H:+JNI",
    //nativeImageOptions +="--static",
    //nativeImageOptions +="--libc=musl",
    //nativeImageOptions +="-H:CCompilerPath=/home/lex/dev/casc/x86_64-linux-musl-native/bin/x86_64-linux-musl-gcc",
    //nativeImageOptions +="-H:UseMuslC=/home/lex/dev/casc/x86_64-linux-musl-native",

    Test/parallelExecution := false,
  ).enablePlugins(NativeImagePlugin)

