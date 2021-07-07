package inrae.semantic_web

import inrae.data.DataTestFactory
import inrae.semantic_web.rdf.URI
import utest.{TestSuite, Tests, test}

import scala.scalajs.js

object SWDiscoveryHelperJsTest extends TestSuite {
  implicit val ec: scala.concurrent.ExecutionContext = scala.concurrent.ExecutionContext.global

  val insertData = DataTestFactory.insert_virtuoso1(
    """<aa> <bb> <cc> .
       <aa> <bb> <dd> .
       <aa> a <ee> .
       <dd> <datatype_prop> "1" .
      """.stripMargin, this.getClass.getSimpleName)

  val config: StatementConfiguration = DataTestFactory.getConfigVirtuoso1()

  override def utestAfterAll(): Unit = {
    DataTestFactory.delete_virtuoso1(this.getClass.getSimpleName)
  }
  def tests = Tests {
    test("count") {
      insertData.map(_ => {
          SWDiscoveryJs(config)
            .graph(DataTestFactory.graph1(this.getClass.getSimpleName))
            .something("h1")
            .isSubjectOf(URI("<bb>"))
            .finder
            .count()
            .toFuture
            .map( (count : Int) => { assert( count == 2 ) })
      }).flatten
    }
    test("classes 1") {
      insertData.map(_ => {
        SWDiscoveryJs(config)
          .graph(DataTestFactory.graph1(this.getClass.getSimpleName))
          .something("h1")
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
          SWDiscoveryJs(config)
            .graph(DataTestFactory.graph1(this.getClass.getSimpleName))
            .something("h1")
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
          SWDiscoveryJs(config)
            .graph(DataTestFactory.graph1(this.getClass.getSimpleName))
            .something("h1")
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
        SWDiscoveryJs(config)
          .graph(DataTestFactory.graph1(this.getClass.getSimpleName))
          .something("h1")
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
        SWDiscoveryJs(config)
          .graph(DataTestFactory.graph1(this.getClass.getSimpleName))
          .something("h1")
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
        SWDiscoveryJs(config)
          .graph(DataTestFactory.graph1(this.getClass.getSimpleName))
          .something("h1")
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
        SWDiscoveryJs(config)
          .graph(DataTestFactory.graph1(this.getClass.getSimpleName))
          .something("h1")
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
        SWDiscoveryJs(config)
          .graph(DataTestFactory.graph1(this.getClass.getSimpleName))
          .something("h1")
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
        SWDiscoveryJs(config)
          .graph(DataTestFactory.graph1(this.getClass.getSimpleName))
          .something("h1")
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
        SWDiscoveryJs(config)
          .graph(DataTestFactory.graph1(this.getClass.getSimpleName))
          .something("h1")
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