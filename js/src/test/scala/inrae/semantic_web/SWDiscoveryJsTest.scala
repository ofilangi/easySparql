package inrae.semantic_web
import scala.scalajs.js.JSConverters._
import inrae.data.DataTestFactory
import utest.{TestSuite, Tests, test}

object SWDiscoveryJsTest extends TestSuite{
  implicit val ec: scala.concurrent.ExecutionContext = scala.concurrent.ExecutionContext.global

  val insertData = DataTestFactory.insertVirtuoso1(
    """<aa> <bb> <cc> .
       <aa> <bb> <dd> .
       <aa> a <ee> .
       <dd> <datatype_prop> "1" .
      """.stripMargin, this.getClass.getSimpleName)

  val config: StatementConfiguration = DataTestFactory.getConfigVirtuoso1()

  override def utestAfterAll(): Unit = {
    DataTestFactory.deleteVirtuoso1(this.getClass.getSimpleName)
  }

  def startRequest =
    SWDiscoveryJs(config)
      .graph(DataTestFactory.graph1(this.getClass.getSimpleName))
      .something("h1")

  def tests = Tests {


    test("focus") {
      startRequest.focus() match {
        case _ : String => assert(true)
        case _ => assert(false)
      }
    }
    test("focus change") {
      startRequest.focus("h1")
    }
    test("prefix") {
      startRequest.prefix("h","http://test")
    }
    test("graph") {
      startRequest.graph("http://test")
    }
    test("something") {
      startRequest.root().something("http://test")
    }
    test("isSubjectOf") {
      startRequest.isSubjectOf("http://test")
    }
    test("isObjectOf") {
      startRequest.isSubjectOf("http://test")
    }
    test("isLinkTo") {
      startRequest.isSubjectOf("http://test")
    }
    test("isA") {
      startRequest.isA("http://test")
    }
    test("isLinkFrom") {
      startRequest.isLinkFrom("http://test")
    }
    test("set") {
      startRequest.set("http://test")
    }
    test("setList") {
      startRequest.setList("http://test")
    }
    test("datatype") {
      startRequest.datatype("http://test","h2")
    }
    test("remove") {
      startRequest.remove("h1")
    }
    test("console") {
      startRequest.console()
    }
    test("console") {
      startRequest.sparql()  match {
        case _ : String => assert(true)
        case _ => assert(false)
      }
    }
    test("getSerializedString") {
      startRequest.getSerializedString()
    }
    test("setSerializedString") {
      startRequest.setSerializedString(startRequest.getSerializedString())
    }
    test("select") {
      startRequest.select("h1")
    }
    test("select") {
      startRequest.select(Seq("h1").toJSArray,1,1)
    }
    test("selectByPage") {
      startRequest.selectByPage("h1")
    }

    test("selectByPage 2") {
      startRequest.selectByPage(List("h1").toJSArray)
    }

    test("decorate") {
      startRequest.decorate("key","value")
    }

  }
}
