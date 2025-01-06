#!/usr/bin/env bash
ARTIFACT=BandWidthTester
VERSION=1.0-SNAPSHOT
WITHDEP=jar-with-dependencies
printf "[\033[42mSTAGE 1\033[0m] \033[33m-------------------------------<\033[0m \033[42mBUILDING\033[0m ... \033[33m>-------------------------------\033[0m\n"
mvn clean package; RC=$?

if [[ $RC -eq 0 ]]; then
    echo
    echo
    printf "[\033[42mSTAGE 2\033[0m] \033[33m----------------------- <\033[0m \033[42mInstalling\033[0m: \033[35m/usr/local/bin/${ARTIFACT}.jar \033[0m  \033[33m>-----------------------\033[0m\n"
    echo "sudo cp -p target/${ARTIFACT}-${VERSION}-${WITHDEP}.jar /usr/local/bin/${ARTIFACT}.jar"
    sudo cp -p target/${ARTIFACT}-${VERSION}-${WITHDEP}.jar /usr/local/bin/${ARTIFACT}.jar; RC=$?
    echo
    if [[ $RC -eq 0 ]]; then
        printf "[\033[42mCOMPLETE\033[0m] \033[32;1mSUCCESS\033[0m\n"
    else
        printf "[\033[42mCOMPLETE\033[0m] \0033[31;1mFAILED\033[0m\n"
    fi
else
    printf "[\033[41mCOMPLETE\033[0m] \033[31;1mFAILED\033[0m\n"
fi
echo
