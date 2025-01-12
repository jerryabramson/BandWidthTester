#!/usr/bin/env bash
CWD=$(pwd)
EXEDIR=$(dirname $0)
if [[ "$EXEDIR" == "." ]]; then
   DIR=$CWD
else
   DIR=$EXEDIR
fi
java -jar /usr/local/lib/BandWidthTester.jar $@
