## Library generation

```
sbt discoveryJS/fullOptJS
sbt discoveryJVM/package
sbt publishLocal 
```

### Library generation html/nodejs

```bash
./update_cdn_libjs.sh
```

## test

```
sbt discoveryJVM/test  
```

## coverage

```
sbt clean coverage discoveryJVM/test discoveryJVM/coverageReport 
chromium ./jvm/target/scala-2.13/scoverage-report/index.html
```

### dependencies

```bash
npm install axios --save-dev
npm install qs --save-dev
npm install browserify
```

### memo

```
sbt compile
sbt discoveryJS/test
sbt discoveryJVM/test
sbt discoveryJS/fastOptJS 
sbt discoveryJS/fullOptJS
sbt discoveryJS/fastOptJS::webpack
sbt discoveryJS/fullOptJS::webpack
sbt discoveryJVM/testOnly inrae.semantic_web.QueryPlannerTest
```

## local publication -> .ivy2

``` 
sbt publishLocal
``` 

## oss.sonatype maven central repository publication
https://oss.sonatype.org/

``` 
sbt publish
```

### NPM publication

``` 
sbt discoveryJS/fullOptJS/webpack
sbt npmPackageJson => genere package.json
npm view @p2m2/discovery version -> list published version
npm unpublish @p2m2/discovery@X.X.X -> unpublished lib
npm publish --access public
```

### generate-changelog / Usage

```
type(category): description [flags]
Where type is one of the following:
```
  * breaking
  * build
  * ci
  * chore
  * docs
  * feat
  * fix
  * other
  * perf
  * refactor
  * revert
  * style
  * test
