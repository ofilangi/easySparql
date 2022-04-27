package inrae.semantic_web.driver

import inrae.data.DataTestFactory
import inrae.semantic_web.configuration._

import utest.{TestSuite, Tests, test}

import scala.util.{Failure, Success, Try}
import java.io.File

object RequestDriverFactoryTest extends TestSuite {

  def tests = Tests {

    test("url application/sparql-query should instantiate Rdf4jSparqlRequestDriver") {
      val source : Source = Source(id="test",path="http://test",mimetype="application/sparql-query",method=Some("POST"))
      Try(RequestDriverFactory.build(source)) match {
        case Success(_ : Rdf4jSparqlRequestDriver) => assert(true)
        case Success(_) => assert(false)
        case Failure(_) => assert(false)
      }
    }

    test("url text/turtle should instantiate Rdf4jRequestDriver") {
      val source : Source = Source(id="test",path=DataTestFactory.urlEndpoint,mimetype="text/turtle")
      Try(RequestDriverFactory.build(source)) match {
        case Success(_ : Rdf4jLocalRequestDriver) => assert(true)
        case Success(c) => assert(false)
        case Failure(e) => assert(false)
      }
    }

    test("bad url text/turtle should instantiate Rdf4jRequestDriver") {
      val source : Source = Source(id="test",path="http://badurl",mimetype="text/turtle")
      Try(RequestDriverFactory.build(source)) match {
        case Success(_ : Rdf4jLocalRequestDriver) => assert(false)
        case Success(c) => assert(false)
        case Failure(e) => assert(true)
      }
    }

    test("file should instantiate Rdf4jRequestDriver ") {

      val tempFile = File.createTempFile("test-", "-rdf")
      val source : Source = Source(id="test",path=tempFile.getAbsolutePath,
        sourcePath = SourcePath.LocalFile,mimetype="text/turtle")
      Try(RequestDriverFactory.build(source)) match {
        case Success(_ : Rdf4jLocalRequestDriver) => { tempFile.delete() ; assert(true) }
        case Success(_) => { tempFile.delete() ;assert(false) }
        case Failure(_) => { tempFile.delete() ;assert(false) }
      }
    }

    test("bad definition of path file should give an error") {

      val tempFile = File.createTempFile("test-", "-rdf")
      val source : Source = Source(id="test",path="/some/some",mimetype="text/turtle")
      Try(RequestDriverFactory.build(source)) match {
        case Success(_ : Rdf4jLocalRequestDriver) => assert(false)
        case Success(_) => { tempFile.delete() ;assert(false) }
        case Failure(_) => { tempFile.delete() ;assert(true) }
      }
    }
  }

}
