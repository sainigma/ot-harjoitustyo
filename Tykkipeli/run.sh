#1/bin/bash
BROWSER="firefox"

function buildNative {
 mvn clean
 mvn -P $1 package
 rm -rf ./builds/$2
 mkdir -p builds/
 mkdir -p builds/$2
 mkdir -p builds/$2/assets
 cp ./target/Tykkipeli.jar ./builds/$2/Tykkipeli.jar
 cp -R ./assets ./builds/$2/
 cp -R ./config ./builds/$2/
 (cd ./builds/$2/; zip -r ../Tykkipeli_$2.zip .)
}

function buildAll {
 buildNative "lwjgl-natives-linux-amd64" "lin64"
 buildNative "lwjgl-natives-windows-amd64" "win64"
 buildNative "lwjgl-natives-macos-amd64" "osx64"
 buildNative "lwjgl-natives-windows-x86" "win32"
}

function build {
 case "$1" in
  "lin64") buildNative "lwjgl-natives-linux-amd64" $1;;
  "win64") buildNative "lwjgl-natives-windows-amd64" $1;;
  "osx64") buildNative "lwjgl-natives-macos-amd64" $1;;
  "win32") buildNative "lwjgl-natives-windows-x86" $1;;
  "all") buildAll;;
  *) echo "build param missing or invalid."; echo "available params: lin64, win64, osx64, win32";;
 esac
}

case "$1" in
 "checkstyle") mvn jxr:jxr checkstyle:checkstyle; URL=file:///$PWD/target/site/checkstyle.html;;
 "test") mvn test jacoco:report; URL=file:///$PWD/target/site/jacoco/index.html;;
 "javadoc") mvn javadoc:javadoc; URL=file:///$PWD/target/site/apidocs/index.html;;
 "build") build $2;;
 *) echo "invalid arguments";;
esac

if [ "$1" != "build" ]; then
 $2 $URL &
fi
