import mill._, scalalib._

object app extends ScalaModule {
  def scalaVersion = "2.13.8"

  def ivyDeps = Agg(
    ivy"com.lihaoyi::cask:0.8.3",
    ivy"com.github.p2m2::discovery:0.4.0",
    ivy"com.github.scopt::scopt:4.0.1",
    ivy"org.eclipse.rdf4j:rdf4j-sail:4.0.0",
    ivy"org.eclipse.rdf4j:rdf4j-storage:4.0.0",
    ivy"org.eclipse.rdf4j:rdf4j-tools-federation:4.0.0",
    ivy"org.eclipse.rdf4j:rdf4j-query:4.0.0",
    ivy"org.eclipse.rdf4j:rdf4j-queryparser-sparql:4.0.0",
    ivy"org.eclipse.rdf4j:rdf4j-queryparser-api:4.0.0",
    ivy"org.eclipse.rdf4j:rdf4j-repository-sparql:4.0.0",
    ivy"org.eclipse.rdf4j:rdf4j-rio-rdfxml:4.0.0",
    ivy"org.slf4j:slf4j-api:1.7.36",
    ivy"org.slf4j:slf4j-simple:1.7.36"

  )
  object test extends Tests{
    def testFramework = "utest.runner.Framework"

    def ivyDeps = Agg(
      ivy"com.lihaoyi::utest::0.7.10",
      ivy"com.lihaoyi::requests::0.6.9",
    )
  }
}
