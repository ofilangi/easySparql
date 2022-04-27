package inrae.semantic_web.driver

import inrae.data.DataTestFactory
import inrae.semantic_web.configuration._

import utest.{TestSuite, Tests, test}

import scala.util.{Failure, Success, Try}
import java.io.File

object RequestDriverFactoryTest extends TestSuite {
  
  def teardown() = RequestDriverFactory.shutDown
  def tests: Tests = Tests {

    val startRequestDriverFactoryInst: RequestDriverFactory = RequestDriverFactory.get

    test("url application/sparql-query should instantiate Rdf4jSparqlRequestDriver") {
      val source : Source = Source(id="test",path="http://test",mimetype="application/sparql-query",method=Some("POST"))
      val rdfI = startRequestDriverFactoryInst.addRepositoryConnection(source)

      Try(rdfI.lCon.map(_._1).last) match {
        case Success(_ : Rdf4jSparqlRequestDriver) => assert(true)
        case Success(_) => assert(false)
        case Failure(_) => assert(false)
      }
      rdfI.close()
    }

    test("url text/turtle should instantiate Rdf4jRequestDriver") {
      val source : Source = Source(id="test",path=DataTestFactory.urlEndpoint,mimetype="text/turtle")
      val rdfI = startRequestDriverFactoryInst.addRepositoryConnection(source)

      Try(rdfI.lCon.map(_._1).last) match {
        case Success(_ : Rdf4jLocalRequestDriver) => assert(true)
        case Success(_) => assert(false)
        case Failure(_) => assert(false)
      }
      rdfI.close()
    }

    test("bad url text/turtle should instantiate Rdf4jRequestDriver") {
      val source : Source = Source(id="test",path="http://badurl",mimetype="text/turtle")
      Try(startRequestDriverFactoryInst.addRepositoryConnection(source).lCon.map(_._1).last) match {
        case Success(_ : Rdf4jLocalRequestDriver) => assert(false)
        case Success(_) => assert(false)
        case Failure(_) => assert(true)
      }
    }

    test("file should instantiate Rdf4jRequestDriver ") {

      val tempFile = File.createTempFile("test-", "-rdf")
      val source : Source = Source(id="test",path=tempFile.getAbsolutePath,
        sourcePath = SourcePath.LocalFile,mimetype="text/turtle")
      val rdfI = startRequestDriverFactoryInst.addRepositoryConnection(source)

      Try(rdfI.lCon.map(_._1).last) match {
        case Success(_ : Rdf4jLocalRequestDriver) => tempFile.delete() ; assert(true)
        case Success(_) => tempFile.delete() ;assert(false)
        case Failure(_) => tempFile.delete() ;assert(false)
      }
      rdfI.close()
    }

    test("bad definition of path file should give an error") {

      val tempFile = File.createTempFile("test-", "-rdf")
      val source : Source = Source(id="test",path="/some/some",sourcePath=SourcePath.LocalFile,mimetype="text/turtle")

      Try(startRequestDriverFactoryInst.addRepositoryConnection(source).lCon.map(_._1).last) match {
        case Success(_ : Rdf4jLocalRequestDriver) => assert(false)
        case Success(_) => tempFile.delete() ;assert(false)
        case Failure(_) => tempFile.delete() ;assert(true)
      }
    }
  }
}
