package fr.inrae.metabohub.semantic_web.driver

import utest.{TestSuite, Tests, test}

import scala.util.{Failure, Success, Try}

object Rdf4jSparqlRequestDriverTest extends TestSuite {
  def tests: Tests = Tests {
    test("without authentication should be ok") {
      assert(Try(Rdf4jSparqlRequestDriver(idName="test", url="http://localhost:8890/sparql", login=None,
        password=None, auth = None, token = None)).isSuccess)
    }

    test("with login/password should be ok") {
      assert(Try(Rdf4jSparqlRequestDriver(idName="test", url="http://localhost:8890/sparql", login=Some("login"),
        password=Some("pass"), auth = None,token = None)).isSuccess)
    }

    test("with only login without password should be false") {
      assert(Try(Rdf4jSparqlRequestDriver(idName="test", url="http://localhost:8890/sparql", login=Some("login"),
        password=None, auth = None,token = None)).isFailure)
    }

    test("with only password without login should be false") {
      assert(Try(Rdf4jSparqlRequestDriver(idName="test", url="http://localhost:8890/sparql", login=None,
        password=Some("pass"),auth = None, token = None)).isFailure)
    }

    test("with login/password/auth should be true") {
      assert(Try(Rdf4jSparqlRequestDriver(idName="test", url="http://localhost:8890/sparql", login=Some("login"),
        password=Some("pass"), auth = Some("basic"), token = None)).isSuccess)
    }
  }
}