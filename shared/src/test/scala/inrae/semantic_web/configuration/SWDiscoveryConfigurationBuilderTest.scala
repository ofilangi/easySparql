package inrae.semantic_web.configuration

import utest.{TestSuite, Tests, test}
import wvlet.log.LogLevel

import scala.util.Try

object SWDiscoveryConfigurationBuilderTest extends TestSuite {
  def tests: Tests = Tests {
    test("default") {
      assert(Try(SWDiscoveryConfiguration()).isSuccess)
    }

    test("urlfile") {
      val s = SWDiscoveryConfiguration().urlFile("http://localhost:8080/animals.ttl")
      assert(s.sources.length == 1)
      assert(s.sources.last.path == "http://localhost:8080/animals.ttl")
      assert(s.sources.last.mimetype == "text/turtle")
    }

    test("sparqlEndpoint") {
      val s = SWDiscoveryConfiguration().sparqlEndpoint("http://localhost/sparql")
      assert(s.sources.length == 1)
      assert(s.sources.last.path == "http://localhost/sparql")
      assert(s.sources.last.mimetype == "application/sparql-query")
    }

    test("sparqlEndpoint auth=basic") {
      val s = SWDiscoveryConfiguration().sparqlEndpoint("http://localhost/sparql",auth="basic")
      assert(s.sources.length == 1)
      assert(s.sources.last.path == "http://localhost/sparql")
      assert(s.sources.last.mimetype == "application/sparql-query")
    }

    test("sparqlEndpoint login=xxxx") {
      val s = SWDiscoveryConfiguration().sparqlEndpoint("http://localhost/sparql",login="xxxxx")
      assert(s.sources.length == 1)
      assert(s.sources.last.path == "http://localhost/sparql")
      assert(s.sources.last.mimetype == "application/sparql-query")
    }

    test("sparqlEndpoint password=xxxx") {
      val s = SWDiscoveryConfiguration().sparqlEndpoint("http://localhost/sparql",password="xxxxx")
      assert(s.sources.length == 1)
      assert(s.sources.last.path == "http://localhost/sparql")
      assert(s.sources.last.mimetype == "application/sparql-query")
    }

    test("sparqlEndpoint login=xxxx,password=xxxx") {
      val s = SWDiscoveryConfiguration().sparqlEndpoint("http://localhost/sparql",login="xxxxx",password="xxxxx")
      assert(s.sources.length == 1)
      assert(s.sources.last.path == "http://localhost/sparql")
      assert(s.sources.last.mimetype == "application/sparql-query")
    }

    test("sparqlEndpoint token=xxxx") {
      val s = SWDiscoveryConfiguration().sparqlEndpoint("http://localhost/sparql",token="xxxxx")
      assert(s.sources.length == 1)
      assert(s.sources.last.path == "http://localhost/sparql")
      assert(s.sources.last.mimetype == "application/sparql-query")
    }

    test("localFile") {
      val s = SWDiscoveryConfiguration().localFile("/localhost/animals.ttl")
      assert(s.sources.length == 1)
      assert(s.sources.last.path == "/localhost/animals.ttl")
      assert(s.sources.last.mimetype == "text/turtle")
    }

    test("rdfContent") {
      val content= ":some :some2 :some3."
      val s = SWDiscoveryConfiguration().rdfContent(content)
      assert(s.sources.length == 1)
      assert(s.sources.last.path == content)
      assert(s.sources.last.mimetype == "text/turtle")
    }

    test("federation") {
      val content= ":some :some2 :some3."

      val s = SWDiscoveryConfiguration()
        .urlFile("http://localhost:8080/animals.ttl")
        .sparqlEndpoint("http://localhost/sparql")
        .localFile("/localhost/animals.ttl")
        .rdfContent(content)

      assert(s.sources.length == 4)
      assert(s.sources.last.path == content)
    }

    test("getSourcesSize") {
      assert(SWDiscoveryConfiguration().localFile("/localhost/animals.ttl").sourcesSize == 1)
    }

    test("setPageSize/getPageSize") {
      assert(SWDiscoveryConfiguration().setPageSize(3).pageSize == 3)
    }

    test("setSizeBatchProcessing/getSizeBatchProcessing") {
      assert(SWDiscoveryConfiguration().setSizeBatchProcessing(3).sizeBatchProcessing == 3)
    }

    test("setLogLevel/getLogLevel") {
      assert(SWDiscoveryConfiguration().setLogLevel("debug").logLevel == "debug")
      assert(SWDiscoveryConfiguration().setLogLevel("info").settings._logLevel == LogLevel.INFO)
      assert(SWDiscoveryConfiguration().setLogLevel("some").settings._logLevel == LogLevel.WARN)
    }

    test("setCache/getCache") {
      assert(!SWDiscoveryConfiguration().setCache(false).cache)
      assert(SWDiscoveryConfiguration().setCache(true).cache)
    }

  }
}
