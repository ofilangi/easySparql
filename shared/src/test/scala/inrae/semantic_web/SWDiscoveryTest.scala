package inrae.semantic_web

import inrae.data.DataTestFactory
import inrae.semantic_web.node.{Node, Root}
import inrae.semantic_web.rdf._
import inrae.semantic_web.configuration._
import utest._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.language.postfixOps
import scala.util.{Failure, Success, Try}

object SWDiscoveryTest extends TestSuite {

  val insertData = DataTestFactory.insertVirtuoso1(
    """
      <http://aa> <http://bb> <http://cc> .
      <http://aa> <http://bb2> <http://cc2> .
      <http://aa> <http://bb2> <http://cc3> .

      <http://bb2> a owl:ObjectProperty .

      <http://aa1> a <http://LeafType> .

      <http://aa2> a <http://LeafType> .
      <http://aa2> a <http://OwlClass> .


      <http://aa3> <http://propDatatype> "test" .

      <http://OwlClass> a owl:Class .
      """.stripMargin, this.getClass.getSimpleName)

  val config: SWDiscoveryConfiguration = DataTestFactory.getConfigVirtuoso1()

  override def utestAfterAll(): Unit = {
    DataTestFactory.deleteVirtuoso1(this.getClass.getSimpleName)
  }

  def startRequest =
    SWDiscovery(config)
      .graph(DataTestFactory.graph1(this.getClass.getSimpleName))
      .something("h1")

  def tests = Tests {
    test("No sources definition") {
      insertData.map(_ => {
        val config: SWDiscoveryConfiguration = SWDiscoveryConfiguration.setConfigString(""" { "sources" : [] } """)
        SWDiscovery(config)
          .something("h1")
          .select(List("h1"))
          .commit()
          .raw
          .map(_ => assert(false))
          .recover((_) => assert(true))
      }).flatten
    }

    test("something") {
      insertData.map(_ => {
        startRequest
          .select(List("h1"))
          .commit()
          .raw
          .map(_ => assert(true))
      }).flatten
    }

    test("isSubjectOf") {
      insertData.map(_ => {
        startRequest
          .set(URI("http://aa"))
          .isSubjectOf(URI("http://bb"), "var")
          .select(List("var"))
          .commit()
          .raw
          .map(result => {
            assert(result("results")("bindings").arr.length == 1)
            assert(SparqlBuilder.createUri(result("results")("bindings")(0)("var")).localName == "http://cc")
          })
      }).flatten
    }
    test("datatype 1") {
      insertData.map(_ => {
        startRequest
          .set(URI("http://aa3"))
          .datatype(URI("http://propDatatype"), "d")
          .select(List("h1","d"))
          .commit()
          .raw
          .map(
            response => {
              println(response)
              assert(response("results")("datatypes")("d")("http://aa3")(0)("value").toString().length > 0)
            }
          )
      }).flatten
    }

    test("datatype 2") {
      insertData.map(_ => {
        startRequest
          .set(URI("http://aa3"))
          .datatype(URI("http://propDatatype"), "d")
          .select(List("d","h1"))
          .commit()
          .raw
          .map(
            response => {
              assert(response("results")("datatypes")("d")("http://aa3")(0)("value").toString().length > 0)
            }
          )
      }).flatten
    }

    test("datatype 3") {
        Try(
          startRequest
          .set(URI("http://aa3"))
          .datatype(URI("http://propDatatype"), "d")
          .select(List("d"))
          .commit()) match {
            case Success(_) => assert(false)
            case Failure(_) => assert(true)
          }
    }

    test("datatype 4") {
      insertData.map(_ => {
        startRequest
          .set(URI("http://aa3"))
          .datatype(URI("http://propDatatype"), "d")
          .select(List("h1"))
          .commit()
          .raw
          .map(
            response => {
              assert(SparqlBuilder.createUri(response("results")("bindings")(0)("h1")).localName == "http://aa3" )
            }
          )
      }).flatten
    }

    test("focus on the root using focus method") {
      val disco = SWDiscovery(config)
      val f = disco.focus()
      assert(Try(disco.focus(f)).isSuccess)
      assert(Try(disco.something("h1").focus(f)).isSuccess)
    }

    test("bad focus") {
      assert(Try(startRequest.focus("h2")).isFailure)
    }

    test("use named graph") {
      assert(Try(startRequest.isSubjectOf(URI("http://bb2"))).isSuccess)
    }

    test("test console") {
      assert(Try(startRequest.isSubjectOf(URI("http://bb2")).console).isSuccess)
    }

    test("refExist") {
      assert(Try(startRequest.refExist("h1")).isSuccess)
    }

    test("refExist2") {
      assert(Try(startRequest.refExist("h2")).isFailure)
    }

    test("remove Something h1") {
      val sw = startRequest.remove("h1")
      assert(sw.rootNode.idRef == sw.focus())
      assert(sw.something("h").focus() == "h")
    }

    test("Remove nothing") {

      val sw =  SWDiscovery(config)
                  .remove("h1")
      assert(sw.rootNode.idRef == sw.focus())

    }

    test("Remove root") {
      val sw =  SWDiscovery(config)
      sw.remove(sw.rootNode.idRef)
      assert(sw.rootNode.idRef == sw.focus())
    }

    test("Remove branch") {
      SWDiscovery(config)
          .something("h1")
          .isObjectOf(URI("http://h1"),"h2")
          .isObjectOf(URI("http://h11"),"h22")
          .root
          .something("d1")
          .isObjectOf(URI("http://d1"),"d2")
          .isObjectOf(URI("http://d11"),"d22")
            .remove("h1")
          .browse( (n: Node,d:Integer) => {
            n match {
              case _ : Root => assert(true)
              case _ => assert(n.idRef.startsWith("d"))
            }
          })
    }

    test("browse") {
      val listBrowse : Seq[String] =
        startRequest
        .isSubjectOf("http://test","h2")
         .browse( (n : Node, p:Integer) => { n.idRef} )
      assert( listBrowse.contains("h1") )
      assert( listBrowse.contains("h2") )
    }

    test("sparql get") {
       assert( startRequest
          .isSubjectOf("http://test","h2")
          .sparql_get.length>0)
    }

    test("sparql curl") {
      assert( startRequest
        .isSubjectOf("http://test","h2")
        .sparql_curl.length>0)
    }

    test("prefix") {
      assert(
        startRequest
          .prefix("some","http://something")
          .getPrefix("some") == IRI("http://something"))
    }

    test("prefix 2") {
      assert(
        startRequest
          .prefix("some","http://something")
          .getPrefixes().contains("some") )
    }
    test("prefix 3") {
      assert(
        startRequest
          .prefixes(Map("some"->"http://something"))
          .getPrefixes().contains("some") )
    }

    test("setConfig/getConfig") {
      assert(startRequest.getConfig.sources.head.id == DataTestFactory.getConfigVirtuoso1().sources.head.id)

      assert(startRequest.setConfig(DataTestFactory.getConfigVirtuoso2()).getConfig.sources.head.id ==
        DataTestFactory.getConfigVirtuoso2().sources.head.id)
    }

    test("setConfig/getConfig during query build") {
      assert(
        startRequest
        .setConfig(DataTestFactory.getConfigVirtuoso2())
         .isObjectOf("http://test11")
          .getConfig.sources.head.id == DataTestFactory.getConfigVirtuoso2().sources.head.id )
    }

    test("namedGraph") {
      assert(Try(startRequest.namedGraph(DataTestFactory.graph1(this.getClass.getSimpleName))).isSuccess)
    }

    test("directive") {
      assert(Try(startRequest.directive("something directive")
        .toString.startsWith("something directive")).isSuccess)
    }

    test("decoration") {
      assert(SWDiscovery(config).something("h").setDecoration("test","something").getDecoration("test")
        == "something")
    }

    test("bad decoration") {
      assert(Try(SWDiscovery(config,rootNode = Root(),fn =  Some("idNotExistInRootNode"))
        .setDecoration("bad declaration because not node is defined","something")).isFailure)
    }

    test("get unknown decoration") {
      assert(SWDiscovery(config).something("h").getDecoration("test") == "")
    }

  }
}
