#!/usr/bin/env bash
ARTIFACT=BandWidthTester
VERSION=1.0-SNAPSHOT
WITHDEP=jar-with-dependencies
printf "[\033[42mSTAGE 1\033[0m] \033[33m-----------------------<\033[0m \033[42mBUILDING\033[0m ... \033[33m>-----------------------\033[0m\n"
mvn clean package

echo
echo
printf "[\033[42mSTAGE 2\033[0m] \033[33m----------------------- <\033[0m \033[42mInstalling\033[0m: \033[35m/usr/local/bin/${ARTIFACT}.jar \033[0m  \033[33m>-----------------------\033[0m\n"
echo "sudo cp -p target/${ARTIFACT}-${VERSION}-${WITHDEP}.jar /usr/local/bin/${ARTIFACT}.jar"
sudo cp -p target/${ARTIFACT}-${VERSION}-${WITHDEP}.jar /usr/local/bin/${ARTIFACT}.jar
echo
