import sbt.Keys.scalacOptions
import sbtcrossproject.CrossPlugin.autoImport.crossProject

/* scala libs */
lazy val utestVersion = "0.7.11"
lazy val upickleVersion  = "1.5.0"
lazy val airframeLogVersion = "21.12.1"
lazy val sttpClient3Version = "3.4.1"
lazy val scalaStubVersion = "1.1.0"
lazy val scalatagVersion = "0.11.1"
lazy val rdf4jVersion = "3.7.7"
lazy val slf4j_version = "1.7.36"
lazy val scalaUriVersion = "3.6.0"
lazy val scalajsDom = "1.2.0"

/* p2m2 libs */
lazy val comunica_actor_init_sparql_rdfjs_version = "1.22.3"
lazy val data_model_rdfjs_version = "1.0.1"
lazy val n3js_facade_version = "1.13.0"
lazy val rdfxml_streaming_parser_version = "1.5.0"
lazy val axios_version = "0.26.1"

/* npm libs */
lazy val npm_qs_version = "6.10.3"
lazy val npm_showdown_version = "1.9.1"
lazy val npm_comunica_version_datasource = "1.22.2"

lazy val types_jest = "27.4.0"
lazy val type_sax = "1.2.4"
lazy val jest = "27.4.7"
lazy val tsjest = "27.1.3"

releaseIgnoreUntrackedFiles := true

val static_version_build = "0.3.2"
val version_build = scala.util.Properties.envOrElse("DISCOVERY_VERSION", static_version_build )
val SWDiscoveryVersionAtBuildTimeFile = "./shared/src/main/scala/inrae/semantic_web/SWDiscoveryVersionAtBuildTime.scala"


val buildSWDiscoveryVersionAtBuildTimeFile =
  if ( ! reflect.io.File(SWDiscoveryVersionAtBuildTimeFile).exists)
    reflect.io.File(SWDiscoveryVersionAtBuildTimeFile).writeAll(
      Predef.augmentString(
      s"""|
      |package inrae.semantic_web
      |
      |object SWDiscoveryVersionAtBuildTime {
      |   val version : String = " build ${java.time.LocalDate.now.toString}"
      |}""").stripMargin)

ThisBuild / name := "discovery"
ThisBuild / organizationName := "p2m2"
ThisBuild / name := "discovery"
ThisBuild / version :=  version_build
ThisBuild / scalaVersion := "2.13.8"
ThisBuild / organization := "com.github.p2m2"
ThisBuild / organizationName := "p2m2"
ThisBuild / organizationHomepage := Some(url("https://www6.inrae.fr/p2m2"))
ThisBuild / licenses := Seq("MIT License" -> url("http://www.opensource.org/licenses/mit-license.php"))
ThisBuild / homepage := Some(url("https://github.com/p2m2/discovery"))
ThisBuild / description := "Ease Sparql request to reach semantic database."
ThisBuild / scmInfo := Some(
    ScmInfo(
      url("https://github.com/p2m2/discovery"),
      "scm:git@github.com:p2m2/Discovery.git"
    )
  )
ThisBuild / developers := List(
    Developer("ofilangi", "Olivier Filangi", "olivier.filangi@inrae.fr",url("https://github.com/ofilangi"))
  )
ThisBuild / credentials += {

    val realm = scala.util.Properties.envOrElse("REALM_CREDENTIAL", "" )
    val host = scala.util.Properties.envOrElse("HOST_CREDENTIAL", "" )
    val login = scala.util.Properties.envOrElse("LOGIN_CREDENTIAL", "" )
    val pass = scala.util.Properties.envOrElse("PASSWORD_CREDENTIAL", "" )

    val file_credential = Path.userHome / ".sbt" / ".credentials"

    if (reflect.io.File(file_credential).exists) {
      Credentials(file_credential)
    } else {
        Credentials(realm,host,login,pass)
    }
  }

ThisBuild / publishTo := {
  if (isSnapshot.value)
    Some("Sonatype Snapshots Nexus" at "https://oss.sonatype.org/content/repositories/snapshots")
  else
    Some("Sonatype Snapshots Nexus" at "https://oss.sonatype.org/service/local/staging/deploy/maven2")
}

ThisBuild / publishConfiguration := publishConfiguration.value.withOverwrite(true)
ThisBuild / publishLocalConfiguration := publishLocalConfiguration.value.withOverwrite(true)
ThisBuild / pomIncludeRepository := { _ => false }
ThisBuild / publishMavenStyle := true


lazy val root = (project in file("."))
  .aggregate(discovery.js, discovery.jvm)
  .settings(
    // crossScalaVersions must be set to Nil on the aggregating project
    crossScalaVersions := Nil,
    publish / skip := true
  )

lazy val discovery=crossProject(JSPlatform, JVMPlatform).in(file("."))
  .settings(
    libraryDependencies ++= Seq(
      "com.softwaremill.sttp.client3" %% "core" % sttpClient3Version % Test,
      "com.lihaoyi" %%% "utest" % utestVersion % Test,
      "com.lihaoyi" %%% "upickle" % upickleVersion,
      "org.wvlet.airframe" %%% "airframe-log" % airframeLogVersion,
      "io.lemonlabs" %%% "scala-uri" % scalaUriVersion
    ),
    testFrameworks += new TestFramework("utest.runner.Framework"),
    scalacOptions ++= Seq("-deprecation", "-feature"),
    classLoaderLayeringStrategy := ClassLoaderLayeringStrategy.AllLibraryJars,
    coverageMinimumStmtTotal := 86,
    coverageFailOnMinimum := false,
    coverageHighlighting := true,
    Test / parallelExecution := false
  )
  .jsConfigure(_.enablePlugins(ScalaJSBundlerPlugin))
  .jsSettings(
    libraryDependencies ++= Seq(
      "com.github.p2m2" %%% "comunica-actor-init-sparql-rdfjs" % comunica_actor_init_sparql_rdfjs_version ,
      "com.github.p2m2" %%% "data-model-rdfjs" % data_model_rdfjs_version ,
      "com.github.p2m2" %%% "n3js" % n3js_facade_version ,
      "com.github.p2m2" %%% "rdfxml-streaming-parser" % rdfxml_streaming_parser_version,
      "com.github.p2m2" %%% "axios" % axios_version,
    ),
    webpackBundlingMode := BundlingMode.LibraryAndApplication(),
    Compile / npmDependencies  ++= Seq(
      "axios" -> axios_version,
      "qs" -> npm_qs_version,
      "showdown" -> npm_showdown_version,
      "@comunica/utils-datasource" -> npm_comunica_version_datasource,
      "@types/sax" -> type_sax
    ),

    Compile / fastOptJS / scalaJSLinkerConfig ~= {
      _.withOptimizer(false)
        .withPrettyPrint(true)
        .withSourceMap(true)
    },
    Compile / fullOptJS / scalaJSLinkerConfig ~= {
      _.withSourceMap(false)
        .withModuleKind(ModuleKind.CommonJSModule)
    },
    libraryDependencies ++= Seq(
      "org.scala-js" %%% "scalajs-dom" % scalajsDom
    )
  )
  .jvmSettings(
    libraryDependencies ++= Seq(
      "org.scala-js" %% "scalajs-stubs" % scalaStubVersion % "provided",
      "org.slf4j" % "slf4j-api" % slf4j_version,
      "org.slf4j" % "slf4j-simple" % slf4j_version,
      "org.eclipse.rdf4j" % "rdf4j-sail" % rdf4jVersion % "provided",
      "org.eclipse.rdf4j" % "rdf4j-storage" % rdf4jVersion % "provided",
      "org.eclipse.rdf4j" % "rdf4j-tools-federation" % rdf4jVersion % "provided"
    ))

/**
 * Build package.json to publish on npm repository
 */
// first define a task key
lazy val npmPackageJson = taskKey[Unit]("Build the discovery package.json")

npmPackageJson := {

  val scalaJsBundlerPackageJsonFile = IO.readLines(new File("js/target/scala-2.13/scalajs-bundler/main/package.json")).filter(_.length>0)
  val indexStartDependencies = scalaJsBundlerPackageJsonFile.zipWithIndex.map {
     case (v,i) if v.contains("dependencies") => i
     case _ => -1
   }.filter( _ > 0)(0)

  val indexEndDependencies =  scalaJsBundlerPackageJsonFile.zipWithIndex.map {
    case (v,i) if (v.contains("}") && i > indexStartDependencies) => i
    case _ => -1
  }.filter( _ > 0)(0)

  val dependencies = scalaJsBundlerPackageJsonFile.zipWithIndex.collect{
    case (x,idx) if ( (idx > indexStartDependencies) && (idx < indexEndDependencies) ) => x
  }

  reflect.io.File("./package.json").writeAll(
    Predef.augmentString(
s"""{
   "name": "@${(ThisBuild / organizationName).value}/${(ThisBuild / name).value}",
   "description": "${(ThisBuild / description).value}",
   "version": "${(ThisBuild / version).value}",
   "main": "./js/target/scala-2.13/scalajs-bundler/main/discovery-opt.js",
   "files": [
     "js/target/scala-2.13/scalajs-bundler/main/discovery-opt.js"
   ],
   "scripts": {
    "test": "jest --detectOpenHandles"
    },
  "devDependencies": {
    "@types/jest": "^$types_jest ",
    "jest": "^$jest ",
    "ts-jest": "^$tsjest"
  },
  "jest": {
    "transform": {
      ".(ts|tsx)": "ts-jest"
    },
    "testRegex": "(ts/__tests__/.*|\\\\.(test|spec))\\\\.(ts|tsx|js)$$",
    "moduleFileExtensions": [
      "ts",
      "tsx",
      "js"
    ]
   },
   "dependencies": {
${dependencies.mkString("\n")}
   },
   "repository": {
     "type": "git",
     "url": "git+https://github.com/p2m2/discovery.git"
   },
   "keywords": [
     "sparql",
     "rdf",
     "scalajs"
   ],
   "author": "Olivier Filangi",
   "license": "MIT",
   "bugs": {
     "url": "https://github.com/p2m2/discovery/issues"
   },
   "homepage": "https://p2m2.github.io/discovery/"
 }
 """).stripMargin)
}

assembly / assemblyMergeStrategy := {
  case PathList("META-INF", xs @ _*) => MergeStrategy.discard
  case x => MergeStrategy.first
}

assembly / target := file("assembly")
assembly / assemblyJarName := s"discovery-$version_build.jar"

Global / onChangedBuildSource := ReloadOnSourceChanges
