echo "######################################################"
echo "compare output of both parsers"
echo "######################################################"
./scripts/runTests.sh leo.modules.parsers.ParseThf2Test
echo "######################################################"
echo "compare output of both parsers on random terms"
echo "######################################################"
./scripts/runTests.sh leo.modules.parsers.ParseTermTest
