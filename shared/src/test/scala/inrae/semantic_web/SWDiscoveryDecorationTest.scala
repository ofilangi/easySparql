package inrae.semantic_web

import inrae.data.DataTestFactory
import inrae.semantic_web.node.{Node, Root}
import inrae.semantic_web.rdf.URI
import inrae.semantic_web.configuration._
import utest._

import scala.language.postfixOps

object SWDiscoveryDecorationTest extends TestSuite {

  val config: StatementConfiguration = DataTestFactory.getConfigVirtuoso1()

  override def utestAfterAll(): Unit = {
    DataTestFactory.deleteVirtuoso1(this.getClass.getSimpleName)
  }

  def startRequest =
    SWDiscovery(config)
      .graph(DataTestFactory.graph1(this.getClass.getSimpleName))
      .something("h1")

  def tests = Tests {

    test("setDecoratingAttribute Root") {
      assert(
        startRequest
          .root
          .setDecoration("someKey","someValue")
          .browse(
            (n : Node,deep: Integer)=> {
              n.decorations
            }
          ).filter( _.size>0) == List(Map("someKey"->"someValue")))

    }

    test("setDecoratingAttribute Root with children") {
      val m =
        startRequest
          .root
          .setDecoration("someKey","someValue")
          .focus("h1")
          .setDecoration("someKey2","someValue2")
          .root
          .setDecoration("someKey3","someValue3")
          .browse(
            (n : Node,deep: Integer) => n match {
              case _ : Root => {
                "root" -> n.decorations
              }
              case _ => {
                n.idRef -> n.decorations
              }
            }
          ).toMap

      assert( m("root") == Map("someKey"->"someValue","someKey3"->"someValue3") )
      assert( m("h1") == Map("someKey2"->"someValue2") )

    }

    test("setDecoratingAttribute basic") {

      assert(startRequest
        .setDecoration("someKey","someValue")
        .browse(
          (n : Node,deep: Integer)=> {
            n.decorations
          }
        ).filter( _.size>0) == List(Map("someKey"->"someValue")))

    }

    test("setDecoratingAttribute") {
     val m =
        startRequest
          .setDecoration("someKey","someValue")
          .isObjectOf(URI("http://s2"),"s2")
          .setDecoration("someKey2","someValue2")
          .isObjectOf(URI("http://s3"),"s3")
          .setDecoration("someKey3","someValue3")
          .browse(
            (n : Node,deep: Integer)=> {
              n.idRef -> n.decorations
            }
          ).toMap

      assert( m("h1") == Map("someKey"->"someValue") )
      assert( m("s2") == Map("someKey2"->"someValue2") )
      assert( m("s3") == Map("someKey3"->"someValue3") )
     }

    test("setDecoratingAttribute/getDecoration") {
      (startRequest
        .setDecoration("someKey","someValue")
        .getDecoration("someKey") == "someValue")
    }

    test("setDecoratingAttribute/getDecoration 2") {
      (startRequest
        .getDecoration("someKey") == "")
    }

    test("setDecoratingAttribute/getDecoration 3") {
      (startRequest
        .setDecoration("someKey","someValue")
        .isObjectOf("http://some")
        .getDecoration("someKey") == "")
    }

    test("setDecoratingAttribute/getDecoration 4") {
      (startRequest
        .isObjectOf("http://some","something")
        .setDecoration("someKey","someValue")
        .isObjectOf("http://some")
        .focus("something")
        .getDecoration("someKey") == "someValue")
    }

    test("setDecoratingAttribute/getDecoration using datatype") {
      assert(SWDiscovery(config)
        .setDecoration("someKeyRoot","someValueRoot")
        .something("start")
        .datatype("http://something","d")
        .root
        .getDecoration("someKeyRoot") == "someValueRoot")
    }
  }
}
