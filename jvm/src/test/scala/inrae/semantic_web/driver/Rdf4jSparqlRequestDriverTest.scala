package inrae.semantic_web.driver

import utest.{TestSuite, Tests, test}

import scala.util.{Failure, Success, Try}

object Rdf4jSparqlRequestDriverTest extends TestSuite {
  def tests: Tests = Tests {
    test("without authentication should be ok") {
      Try(Rdf4jSparqlRequestDriver(
        idName="test",
        url="http://localhost:8890/sparql",
        login=None,
        password=None,
        auth = None,
        token = None
      )) match {
        case Success(_) => assert(true)
        case Failure(_) => assert(false)
      }
    }

    test("with login/password should be ok") {
      Try(Rdf4jSparqlRequestDriver(
        idName="test",
        url="http://localhost:8890/sparql",
        login=Some("login"),
        password=Some("pass"),
        auth = None,
        token = None
      )) match {
        case Success(_) => assert(true)
        case Failure(_) => assert(false)
      }
    }

    test("with only login without password should be false") {
      Try(Rdf4jSparqlRequestDriver(
        idName="test",
        url="http://localhost:8890/sparql",
        login=Some("login"),
        password=None,
        auth = None,
        token = None
      )) match {
        case Success(_) => assert(false)
        case Failure(_) => assert(true)
      }
    }

    test("with only password without login should be false") {
      Try(Rdf4jSparqlRequestDriver(
        idName="test",
        url="http://localhost:8890/sparql",
        login=None,
        password=Some("pass"),
        auth = None,
        token = None
      )) match {
        case Success(_) => assert(false)
        case Failure(_) => assert(true)
      }
    }

    test("with login/password/auth should be true") {
      Try(Rdf4jSparqlRequestDriver(
        idName="test",
        url="http://localhost:8890/sparql",
        login=Some("login"),
        password=Some("pass"),
        auth = Some("basic"),
        token = None
      )) match {
        case Success(_) => assert(true)
        case Failure(_) => assert(false)
      }
    }

  }
}