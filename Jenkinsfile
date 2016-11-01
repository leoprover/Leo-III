#!groovy

node {
    stage 'Checkout'

    checkout scm
    echo "Downloading PicoSAT"
    sh "wget http://fmv.jku.at/picosat/picosat-965.tar.gz"
    sh "tar -xf picosat-965.tar.gz -C src/native"

    stage 'Build'

    env.JAVA_HOME = tool name: 'Java 8', type: 'jdk'
    env.PATH = "${env.JAVA_HOME}/bin:${env.PATH}"
    echo "JAVA_HOME is: ${env.JAVA_HOME}"

    sh "sbt compile"
    sh "sbt nativeCompile"

    stage 'Run & Archive'

    sh "sbt run"

    sh "sbt assembly"
    archiveArtifacts artifacts: '**/target/*assembly*.jar', fingerprint: true

    stage 'Tests'

    sh "sbt test || true"
    step([$class: 'JUnitResultArchiver', testResults: 'target/test-reports/*.xml', fingerprint: true])


    stage 'Small Benchmark'

}