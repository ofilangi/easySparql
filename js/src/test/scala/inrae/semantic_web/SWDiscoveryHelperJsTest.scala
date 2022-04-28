package inrae.semantic_web

import inrae.data.DataTestFactory
import inrae.semantic_web.rdf.URI
import inrae.semantic_web.configuration._
import utest.{TestSuite, Tests, test}

import scala.scalajs.js

object SWDiscoveryHelperJsTest extends TestSuite {
  implicit val ec: scala.concurrent.ExecutionContext = scala.concurrent.ExecutionContext.global

  val insertData = DataTestFactory.insertVirtuoso1(
    """<aa> <bb> <cc> .
       <aa> <bb> <dd> .
       <aa> a <ee> .
       <dd> <datatype_prop> "1" .
      """.stripMargin, this.getClass.getSimpleName)

  val config: SWDiscoveryConfiguration = DataTestFactory.getConfigVirtuoso1()

  override def utestAfterAll(): Unit = {
    DataTestFactory.deleteVirtuoso1(this.getClass.getSimpleName)
  }

  def startRequest =
    SWDiscoveryJs(config)
    .graph(DataTestFactory.graph1(this.getClass.getSimpleName))
    .something("h1")

  def tests = Tests {
    test("count") {
      insertData.map(_ => {
        startRequest
            .isSubjectOf(URI("<bb>"))
            .finder
            .count(js.Array("h1"))
            .toFuture
            .map( (count : Int) => { assert( count == 2 ) })
      }).flatten
    }
    test("classes 1") {
      insertData.map(_ => {
        startRequest
          .isSubjectOf(URI("<bb>"))
          .focus("h1")
          .finder
          .classes()
          .toFuture
          .map( (lUris : js.Array[URI]) => {
            assert( lUris.toList == List(URI("ee")) )
          })
      }).flatten
    }
    test("classes 2") {
        insertData.map(_ => {
          startRequest
            .isSubjectOf(URI("<bb>"))
            .finder
            .classes()
            .toFuture
            .map((lUris: js.Array[URI]) => {
              assert(lUris.toList == List())
            })
        }).flatten
      }
      test("classes 3") {
        insertData.map(_ => {
          startRequest
            .finder
            .classes()
            .toFuture
            .map( (lUris : js.Array[URI]) => {
              assert( lUris.toList == List(URI("ee")) )
            })
        }).flatten
      }

    test("objectProperties 1") {
      insertData.map(_ => {
        startRequest
          .finder
          .objectProperties()
          .toFuture
          .map( (lUris : js.Array[URI]) => {
            assert( lUris.toList == List(URI("bb")) )
          })
      }).flatten
    }
    test("objectProperties 2") {
      insertData.map(_ => {
        startRequest
          .isSubjectOf(URI("<bb>"))
          .finder
          .objectProperties()
          .toFuture
          .map( (lUris : js.Array[URI]) => {
            assert( lUris.toList == List() )
          })
      }).flatten
    }
    test("subjectProperties 1") {
      insertData.map(_ => {
        startRequest
          .finder
          .subjectProperties()
          .toFuture
          .map( (lUris : js.Array[URI]) => {
            assert( lUris.toList.contains(URI("bb")) )
            assert( lUris.toList.contains(URI("datatype_prop")) )
          })
      }).flatten
    }
    test("subjectProperties 2") {
      insertData.map(_ => {
        startRequest
          .isSubjectOf(URI("<bb>"))
          .finder
          .subjectProperties()
          .toFuture
          .map( (lUris : js.Array[URI]) => {
            assert( lUris.toList == List(URI("bb")) )
          })
      }).flatten
    }
    test("datatypeProperties 1") {
      insertData.map(_ => {
        startRequest
          .finder
          .datatypeProperties()
          .toFuture
          .map((lUris: js.Array[URI]) => {
            assert(lUris.toList == List(URI("datatype_prop")))
          })
      }).flatten
    }
    test("datatypeProperties 2") {
      insertData.map(_ => {
        startRequest
          .isSubjectOf(URI("<bb>"))
          .finder
          .datatypeProperties()
          .toFuture
          .map( (lUris : js.Array[URI]) => {
            assert( lUris.toList == List(URI("datatype_prop")) )
          })
      }).flatten
    }
    test("datatypeProperties 3") {
      insertData.map(_ => {
        startRequest
          .isObjectOf(URI("<bb>"))
          .finder
          .datatypeProperties()
          .toFuture
          .map( (lUris : js.Array[URI]) => {
            assert( lUris.toList == List() )
          })
      }).flatten
    }
  }
}