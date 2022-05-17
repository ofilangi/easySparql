import mill._, scalalib._
import scala.sys.process._
import java.io.File

object app extends ScalaModule {
  def scalaVersion = "2.13.8"
  def version_build : String = {
    val version_discovery : String = "PROXY-LOCAL-BUILD"
    val dir = System.getProperty("user.dir") + "/.."

    val res = Process(
      command="sbt publishLocal",
      cwd=new File(dir),
      extraEnv="DISCOVERY_VERSION"->version_discovery).!!

    println(res)
    version_discovery
  }
  def ivyDeps = Agg(
    ivy"com.lihaoyi::cask:0.8.3",
    ivy"com.github.p2m2::discovery:$version_build",
    ivy"com.github.scopt::scopt:4.0.1"
  )
  object test extends Tests{
    def testFramework = "utest.runner.Framework"

    def ivyDeps = Agg(
      ivy"com.lihaoyi::utest::0.7.10",
      ivy"com.lihaoyi::requests::0.6.9",
    )
  }
}
