$ARTIFACT="BandWidthTester"
$WITHDEP="all"
$VERSION="1.0-SNAPSHOT"
echo "[`e[42mSTAGE 1`e[0m] `e[33m-----------------------<`e[0m `e[42mBUILDING`e[0m ... `e[33m>-----------------------`e[0m"

gradle clean shadowJar

echo ""
echo ""
echo "[`e[42mSTAGE 2`e[0m] `e[33m-----------------------<`e[0m `e[42mInstalling`e[0m: `e[35m/usr/local/bin/${ARTIFACT}.jar `e[0m  `e[33m>-----------------------`e[0m"

echo "copy build/libs/$ARTIFACT-$VERSION-$WITHDEP.jar $ENV:USERPROFILE/bin/$ARTIFACT.jar"
copy build/libs/$ARTIFACT-$VERSION-$WITHDEP.jar $ENV:USERPROFILE/bin/$ARTIFACT.jar
echo ""

