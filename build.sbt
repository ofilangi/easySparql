import sbt.Keys.scalacOptions
import sbtcrossproject.CrossPlugin.autoImport.crossProject

/* scala libs */
lazy val utestVersion = "0.7.7"
lazy val upickleVersion  = "1.2.2"
lazy val airframeLogVersion = "20.11.0"
lazy val scalaParserCombinatorVersion = "1.1.2"
lazy val RosHttpVersion = "3.0.0"
lazy val scalaJsDOMVersion = "1.1.0"
lazy val scalaStubVersion = "1.0.0"
lazy val scalatagVersion = "0.9.2"
lazy val rdf4jVersion = "3.6.0-M2"
lazy val slf4j_version = "1.7.9"

/* p2m2 libs */
lazy val comunica_actor_init_sparql_rdfjs_version = "1.0.0"
lazy val data_model_rdfjs_version = "1.0.0"
lazy val n3js_facade_version = "1.0.1"
lazy val rdfxml_streaming_parser_version = "1.0.0"

/* npm libs */
lazy val npm_axios_version = "0.21.1"
lazy val npm_qs_version = "6.9.6"
lazy val npm_showdown_version = "1.9.1"
lazy val npm_comunica_version = "1.19.2"

releaseIgnoreUntrackedFiles := true

val version_build = scala.util.Properties.envOrElse("DISCOVERY_VERSION", "local-SNAPSHOT" )
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
ThisBuild / scalaVersion := "2.13.5"
ThisBuild / organization := "com.github.p2m2"
ThisBuild / organizationName := "p2m2"
ThisBuild / organizationHomepage := Some(url("https://www6.inrae.fr/p2m2"))
ThisBuild / licenses := Seq("MIT License" -> url("http://www.opensource.org/licenses/mit-license.php"))
ThisBuild / homepage := Some(url("https://github.com/p2m2/Discovery"))
ThisBuild / description := "Ease Sparql request on the network MetaboHUB/Semantics Databases."
ThisBuild / scmInfo := Some(
    ScmInfo(
      url("https://github.com/p2m2/Discovery"),
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
    resolvers += Resolver.bintrayRepo("hmil", "maven"),
    libraryDependencies ++= Seq(
      "com.lihaoyi" %%% "utest" % utestVersion % Test,
      "fr.hmil" %%% "roshttp" % RosHttpVersion % Test ,
      "com.lihaoyi" %%% "upickle" % upickleVersion,
      "org.wvlet.airframe" %%% "airframe-log" % airframeLogVersion,
      "org.scala-lang.modules" %%% "scala-parser-combinators" % scalaParserCombinatorVersion
    ),
    testFrameworks += new TestFramework("utest.runner.Framework"),
    scalacOptions ++= Seq("-deprecation", "-feature"),
    classLoaderLayeringStrategy := ClassLoaderLayeringStrategy.AllLibraryJars,
    coverageMinimum := 70,
    coverageFailOnMinimum := false,
    coverageHighlighting := true,
    parallelExecution in Test := false
  )
  .jsConfigure(_.enablePlugins(ScalaJSBundlerPlugin))
  .jsSettings(
    libraryDependencies ++= Seq(
      "com.github.p2m2" %%% "comunica-actor-init-sparql-rdfjs" % comunica_actor_init_sparql_rdfjs_version ,
      "com.github.p2m2" %%% "data-model-rdfjs" % data_model_rdfjs_version ,
      "com.github.p2m2" %%% "n3js" % n3js_facade_version ,
      "com.github.p2m2" %%% "rdfxml-streaming-parser" % rdfxml_streaming_parser_version,
    ),
    webpackBundlingMode := BundlingMode.LibraryAndApplication(),
    npmDependencies in Compile ++= Seq(
      "axios" -> npm_axios_version,
      "qs" -> npm_qs_version,
      "showdown" -> npm_showdown_version,
      "@comunica/utils-datasource" -> npm_comunica_version
    ),

    scalaJSLinkerConfig in (Compile, fastOptJS ) ~= {
      _.withOptimizer(false)
        .withPrettyPrint(true)
        .withSourceMap(true)
    },
    scalaJSLinkerConfig in (Compile, fullOptJS) ~= {
      _.withSourceMap(false)
        .withModuleKind(ModuleKind.CommonJSModule)
    },
    libraryDependencies ++= Seq(
      "org.scala-js" %%% "scalajs-dom" % "1.1.0"
    )
  )
  .jvmSettings(
    libraryDependencies ++= Seq(
      "org.scala-js" %% "scalajs-stubs" % scalaStubVersion % "provided",
      "org.slf4j" % "slf4j-api" % slf4j_version,
      "org.slf4j" % "slf4j-simple" % slf4j_version,
      "org.eclipse.rdf4j" % "rdf4j-storage" % rdf4jVersion,
      "org.eclipse.rdf4j" % "rdf4j-tools-federation" % rdf4jVersion
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


  val file =  reflect.io.File("./package.json").writeAll(
    Predef.augmentString(
s"""{
   "name": "@${(ThisBuild / organizationName).value}/${(ThisBuild / name).value}",
   "description": "${(ThisBuild / description).value}",
   "version": "${(ThisBuild / version).value}",
   "main": "./js/target/scala-2.13/scalajs-bundler/main/discovery-opt.js",
   "files": [
     "js/target/scala-2.13/scalajs-bundler/main/discovery-opt.js"
   ],
   "dependencies": {
${dependencies.mkString("\n")}
   },
   "repository": {
     "type": "git",
     "url": "git+https://github.com/p2m2/Discovery.git"
   },
   "keywords": [
     "sparql",
     "rdf",
     "scalajs"
   ],
   "author": "Olivier Filangi",
   "license": "MIT",
   "bugs": {
     "url": "https://github.com/p2m2/Discovery/issues"
   },
   "homepage": "https://github.com/p2m2/Discovery#README.md"
 }
 """).stripMargin)
}

Global / onChangedBuildSource := ReloadOnSourceChanges
