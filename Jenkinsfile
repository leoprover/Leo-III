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

    sh """ sbt "testOnly -- -n Checked " || true """
    step([$class: 'JUnitResultArchiver', testResults: 'target/test-reports/*.xml', fingerprint: true])

    stage 'Soundness Check'

    env.TPTP = tool name: 'TPTP'
    def benchmark = tool name: 'Benchmark'

    sh "python3 ${benchmark}/Scripts/benchmark.py -p ${benchmark} -o soundness_logs -e soundness_errors -r soundness_results -s ${benchmark}/Lists/csa_default"
    archiveArtifacts artifacts: 'soundness_*', fingerprint: true

    stage 'Small Benchmark'

    sh "rm -f benchmark_results"
    sh "rm -f benchmark_logs"
    sh "rm -f benchmark_errors"

    def b = {l ->
      sh "python3 ${benchmark}/Scripts/benchmark.py -p ${benchmark} -o ${l}_logs -e ${l}_errors -r ${l}_results ${benchmark}/Lists/${l}"
      sh "echo >> benchmark_results"
      sh "echo Results for ${l}: >> benchmark_results"
      sh "cat ${l}_results >> benchmark_results"
      sh "cat ${l}_logs >> benchmark_logs"
    }

    b("10THM_rtng0.0")
    b("10THM_rtng0.25")
    b("10THM_rtng0.43")
    archiveArtifacts artifacts: 'benchmark_*', fingerprint: true

}
