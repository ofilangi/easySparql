import sbt.Keys.scalacOptions
import sbt.file
import sbtcrossproject.CrossPlugin.autoImport.crossProject

/* p2m2 libs */
lazy val comunica_query_sparql_version = "2.6.8"
lazy val data_model_rdfjs_version = "1.0.2"
lazy val n3js_facade_version = "1.16.3"
lazy val rdfxml_streaming_parser_version = "2.2.1"
lazy val axios_version = "1.3.2"
//lazy val scalaJsMacrotaskExecutor = "1.0.0"

/* npm libs */
lazy val npm_axios_version = "1.3.4"
lazy val npm_qs_version       = "6.11.0"
lazy val npm_showdown_version = "2.1.0"
lazy val npm_comunica_version_datasource = "1.22.2"
lazy val npm_buffer_version = "6.0.3"
lazy val npm_stream_version = "0.0.2"
lazy val npm_util_version   = "0.12.5"

lazy val types_jest = "29.4.0"
lazy val type_sax = "1.2.4"
lazy val jest = "29.4.2"
lazy val tsjest = "29.0.5"

releaseIgnoreUntrackedFiles := true

val static_version_build = "0.4.2"
val version_build = scala.util.Properties.envOrElse("DISCOVERY_VERSION", static_version_build)
val SWDiscoveryVersionAtBuildTimeFile = "./shared/src/main/scala/fr/inrae/metabohub" +
  "/semantic_web/SWDiscoveryVersionAtBuildTime.scala"


val buildSWDiscoveryVersionAtBuildTimeFile: Unit =
  if ( ! reflect.io.File(SWDiscoveryVersionAtBuildTimeFile).exists)
    reflect.io.File(SWDiscoveryVersionAtBuildTimeFile).writeAll(
      Predef.augmentString(
      s"""|
      |package fr.inrae.metabohub.semantic_web
      |
      |object SWDiscoveryVersionAtBuildTime {
      |   val version : String = " build ${java.time.LocalDate.now.toString}"
      |}""").stripMargin)

ThisBuild / name := "discovery"
ThisBuild / organizationName := "p2m2"
ThisBuild / name := "discovery"
ThisBuild / version :=  version_build
ThisBuild / scalaVersion := "2.13.10" // val scala212 = "2.12.14", val scala3 = "3.0.0"
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

lazy val discovery=
  crossProject(JSPlatform,JVMPlatform)
    .in(file("."))
  .settings(
    libraryDependencies ++= Seq(
      "com.softwaremill.sttp.client3" %% "core" % "3.8.12" % Test,
      "com.lihaoyi" %%% "utest" % "0.8.1" % Test,
      "com.lihaoyi" %%% "upickle" % "3.0.0-M2",
      "org.wvlet.airframe" %%% "airframe-log" % "23.2.3",
      "io.lemonlabs" %%% "scala-uri" % "4.0.3"
    ),
    testFrameworks += new TestFramework("utest.runner.Framework"),
    scalacOptions ++= Seq("-deprecation", "-feature"),
    classLoaderLayeringStrategy := ClassLoaderLayeringStrategy.AllLibraryJars,
    coverageMinimumStmtTotal := 93,
    coverageFailOnMinimum := false,
    coverageHighlighting := true,
    Test / parallelExecution := false
  )
  .jsConfigure(_.enablePlugins(ScalaJSBundlerPlugin))
  .jsSettings(
    scalacOptions ++= Seq("-P:scalajs:nowarnGlobalExecutionContext"),
    libraryDependencies ++= Seq(
      ("org.scala-js" %%% "scalajs-java-securerandom" % "1.0.0").cross(CrossVersion.for3Use2_13),
      "com.github.p2m2" %%% "comunica-query-sparql" % comunica_query_sparql_version ,
      "com.github.p2m2" %%% "data-model-rdfjs" % data_model_rdfjs_version ,
      "com.github.p2m2" %%% "n3js" % n3js_facade_version ,
      "com.github.p2m2" %%% "rdfxml-streaming-parser" % rdfxml_streaming_parser_version,
      "com.github.p2m2" %%% "axios" % axios_version
    ),
    webpackBundlingMode := BundlingMode.LibraryAndApplication(),
    Compile / npmDependencies  ++= Seq(
      "axios" -> npm_axios_version,
      "qs" -> npm_qs_version,
      "showdown" -> npm_showdown_version,
      "@comunica/utils-datasource" -> npm_comunica_version_datasource,
      "@types/sax" -> type_sax,
      "buffer" -> npm_buffer_version,
      "stream" -> npm_stream_version,
      "util" -> npm_util_version
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
      "org.scala-js" %%% "scalajs-dom" % "2.1.0"
    )
  )
  .jvmSettings(
    //run / fork := true,
    libraryDependencies ++= Seq(
      "com.lihaoyi" %% "requests" % "0.8.0",
      "org.scala-js" %% "scalajs-stubs" % "1.1.0" % "provided",
      "org.slf4j" % "slf4j-api" % "2.0.5",
      "org.slf4j" % "slf4j-simple" % "2.0.5",
      "org.eclipse.rdf4j" % "rdf4j-sail" % "4.2.3",
      ("org.eclipse.rdf4j" % "rdf4j-storage" % "4.2.3")
        .exclude("commons-codec","commons-codec"),
      ("org.eclipse.rdf4j" % "rdf4j-tools-federation" % "4.2.3")
        .exclude("commons-codec","commons-codec")
    ),
    assembly / assemblyJarName := s"discovery-$version_build.jar",
    assembly / logLevel := Level.Info,
    assembly / assemblyMergeStrategy := {
      case PathList("META-INF", xs @ _*) => MergeStrategy.discard
      case "module-info.class"  => MergeStrategy.first
      case x =>
        val oldStrategy = (ThisBuild / assemblyMergeStrategy).value
        oldStrategy(x)
    }
  )
/**
 * Build package.json to publish on npm repository
 */
// first define a task key
lazy val npmPackageJson = taskKey[Unit]("Build the discovery package.json")

npmPackageJson := {

  val scalaJsBundlerPackageJsonFile = IO.readLines(new File("js/target/scala-2.13/scalajs-bundler/main/package.json")).filter(_.nonEmpty)
  val indexStartDependencies = scalaJsBundlerPackageJsonFile.zipWithIndex.map {
    case (v, i) if v.contains("dependencies") => i
    case _ => -1
  }.filter(_ > 0).head

  val indexEndDependencies =  scalaJsBundlerPackageJsonFile.zipWithIndex.map {
    case (v, i) if v.contains("}") && i > indexStartDependencies => i
    case _ => -1
  }.filter(_ > 0).head

  val dependencies = scalaJsBundlerPackageJsonFile.zipWithIndex.collect{
    case (x,idx) if (idx > indexStartDependencies) && (idx < indexEndDependencies) => x
  }

  reflect.io.File("./package.json").writeAll(
    Predef.augmentString(
s"""{
   "name": "@${(ThisBuild / organizationName).value}/${(ThisBuild / name).value}",
   "description": "${(ThisBuild / description).value}",
   "version": "${(ThisBuild / version).value}",
   "main": "./js/target/scala-2.13/scalajs-bundler/main/discovery-opt.js",
   "types": "./ts/types/discovery.d.ts",
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

Global / onChangedBuildSource := ReloadOnSourceChanges
