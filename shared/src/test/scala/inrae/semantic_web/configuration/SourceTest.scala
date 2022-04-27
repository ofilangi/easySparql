package inrae.semantic_web.configuration

import utest.{TestSuite, Tests, test}

import scala.util.{Failure, Success, Try}

object SourceTest extends TestSuite {
  def tests: Tests = Tests {
    test("bad auth parameter") {
      Try(Source(id="test",path="path",mimetype="application/sparql-query",auth=Some("test"))) match {
        case Success(_) => assert(false)
        case Failure(_) => assert(true)
      }
    }

    test("ok auth parameter") {
      Try(Source(id="test",path="path",mimetype="application/sparql-query",auth=Some("basic"))) match {
        case Success(_) => assert(true)
        case Failure(_) => assert(false)
      }
    }
  }
}
