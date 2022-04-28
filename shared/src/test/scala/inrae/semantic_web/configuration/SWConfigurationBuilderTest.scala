package inrae.semantic_web.configuration

import utest.{TestSuite, Tests, test}
import wvlet.log.LogLevel

import scala.util.Try

object SWConfigurationBuilderTest extends TestSuite {
  def tests: Tests = Tests {
    test("default") {
      assert(Try(SWConfigurationBuilder()).isSuccess)
    }

    test("urlfile") {
      val s = SWConfigurationBuilder().urlFile("http://localhost:8080/animals.ttl")
      assert(s.sources.length == 1)
      assert(s.sources.last.path == "http://localhost:8080/animals.ttl")
      assert(s.sources.last.mimetype == "text/turtle")
    }

    test("sparqlEndpoint") {
      val s = SWConfigurationBuilder().sparqlEndpoint("http://localhost/sparql")
      assert(s.sources.length == 1)
      assert(s.sources.last.path == "http://localhost/sparql")
      assert(s.sources.last.mimetype == "application/sparql-query")
    }

    test("localFile") {
      val s = SWConfigurationBuilder().localFile("/localhost/animals.ttl")
      assert(s.sources.length == 1)
      assert(s.sources.last.path == "/localhost/animals.ttl")
      assert(s.sources.last.mimetype == "text/turtle")
    }

    test("rdfContent") {
      val content= ":some :some2 :some3."
      val s = SWConfigurationBuilder().rdfContent(content)
      assert(s.sources.length == 1)
      assert(s.sources.last.path == content)
      assert(s.sources.last.mimetype == "text/turtle")
    }

    test("federation") {
      val content= ":some :some2 :some3."

      val s = SWConfigurationBuilder()
        .urlFile("http://localhost:8080/animals.ttl")
        .sparqlEndpoint("http://localhost/sparql")
        .localFile("/localhost/animals.ttl")
        .rdfContent(content)

      assert(s.sources.length == 4)
      assert(s.sources.last.path == content)
    }

    test("setPageSize") {
      val s = SWConfigurationBuilder().setPageSize(3)
      assert(s.settings.pageSize == 3)
    }

    test("setSizeBatchProcessing") {
      val s = SWConfigurationBuilder().setSizeBatchProcessing(3)
      assert(s.settings.sizeBatchProcessing == 3)
    }

    test("setLogLevel") {
      val s = SWConfigurationBuilder().setLogLevel("info")
      assert(s.settings.logLevel == "info")
      assert(s.settings._logLevel == LogLevel.INFO)

      assert(SWConfigurationBuilder().setLogLevel("some").settings._logLevel == LogLevel.WARN)
    }

    test("setCache") {
      assert(!SWConfigurationBuilder().setCache(false).settings.cache)
      assert(SWConfigurationBuilder().setCache(true).settings.cache)
    }

  }
}
