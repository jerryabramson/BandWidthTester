@set ARTIFACT=BandWidthTester
@set VERSION=1.0-SNAPSHOT
@set WITHDEP=jar-with-dependencies
@for /F %%a in ('echo prompt $E^| cmd') do @set "ESC=%%a"

@echo ^[%ESC%[42mSTAGE 1%ESC%[0m^] %ESC%[33m-----------------------^<%ESC%[0m %ESC%[42mBUILDING%ESC%[0m ... %ESC%[33m^>-----------------------%ESC%[0m

@cmd /c mvn clean package

@echo [%ESC%[42mSTAGE 2%ESC%[0m] %ESC%[33m----------------------- ^<%ESC%[0m %ESC%[42mInstalling%ESC%[0m: %ESC%[35m/usr/local/bin/${ARTIFACT}.jar %ESC%[0m  %ESC%[33m^>-----------------------%ESC%[0m
copy target\%ARTIFACT%-%VERSION%-%WITHDEP%.jar %USERPROFILE%/bin/%ARTIFACT%.jar

