#!/bin/bash

export NODE_OPTIONS=--openssl-legacy-provider

# clean dist directory
rm dist/discovery*.js
# generate build version
rm ./shared/src/main/scala/inrae/semantic_web/SWDiscoveryVersionAtBuildTime.scala

# browser lib debug information
sbt discoveryJS/fastOptJS/webpack
# node lib
sbt discoveryJS/fullOptJS
# browser lib
sbt discoveryJS/fullOptJS/webpack

# browser lib js
cp ./js/target/scala-2.13/scalajs-bundler/main/discovery-fastopt-bundle.js  ./dist/discovery-web-dev.js
cp ./js/target/scala-2.13/scalajs-bundler/main/discovery-fastopt-bundle.js.map ./dist/discovery-web-dev.js.map
cp ./js/target/scala-2.13/scalajs-bundler/main/discovery-opt-bundle.js ./dist/discovery-web.js

# typescript test
sbt npmPackageJson
npm i
npm test -- --silent && echo "typescript test ok" || exit 1

sed -i "s#discovery-fastopt-bundle#discovery-web-dev#g" $(find ./dist -type f -name "*dev*")
sed -i "s#$(pwd)#com/github/p2m2#g" $(find ./dist -type f)

# generate md5sum to check js libraries

cat $(find . -name *.scala | sort -V | grep -v SWDiscoveryVersionAtBuildTime.scala) | md5sum > dist/checksum

# var CI defined on circleci at true

if [ -z "$CI" ];then
  echo " -- commit -- "
  git commit dist -m"update cdn js lib." &>/dev/null
fi
