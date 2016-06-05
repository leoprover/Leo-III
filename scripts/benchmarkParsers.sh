echo "######################################################"
echo "compare runtime of both parsers"
echo "######################################################"
./scripts/runTests.sh leo.modules.parsers.ParseTHFBenchmark
echo "######################################################"
echo "compare runtime of both parsers on random terms"
echo "######################################################"
./scripts/runTests.sh leo.modules.parsers.ParseTermBenchmark
