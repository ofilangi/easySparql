package fr.inrae.mth.app

import inrae.data.DataTestFactory
import inrae.semantic_web.configuration.SWDiscoveryConfiguration
import inrae.semantic_web.sparql.QueryResult
import inrae.semantic_web.{SWDiscovery, SWTransaction}
import io.undertow.Undertow
import ujson.Arr
import utest.{TestSuite, Tests, test}

import scala.concurrent.Future

object SWDiscoveryProxyTest extends TestSuite {

  def withServer[T](example: cask.main.Main)(f: String => T): T = {
    val server = Undertow.builder
      .addHttpListener(8081, "localhost")
      .setHandler(example.defaultHandler)
      .build
    server.start()
    val res =
      try f("http://localhost:8081")
      finally server.stop()
    res
  }

  val insertData: Future[Any] = DataTestFactory.insertVirtuoso1(
    """<http://aa> <http://bb> <http://cc> .""".stripMargin, this.getClass.getSimpleName)

  val varName : String = "h1"

  val bindingsResult: Arr = ujson.Arr(
    ujson.Obj(varName -> ujson.Obj("type" -> "uri","value"->"http://aa")),
    ujson.Obj(varName -> ujson.Obj("type" -> "uri","value"->"http://bb")),
    ujson.Obj(varName -> ujson.Obj("type" -> "uri","value"->"http://cc")),
  )

  override def utestAfterAll(): Unit = {
    DataTestFactory.deleteVirtuoso1(this.getClass.getSimpleName)
  }


  val config: SWDiscoveryConfiguration = DataTestFactory.getConfigVirtuoso1()

  val transactionTest: SWTransaction = SWDiscovery(config)
    .graph(DataTestFactory.graph1(this.getClass.getSimpleName))
    .something(varName)
    .select(Seq(varName))

  def testResponse(response: requests.Response) = {
    assert(response.statusCode == 200)
    val jsonV = QueryResult(response.text()).json
    assert(jsonV("results")("bindings") == bindingsResult)
  }

  def tests: Tests = Tests {
    test("GET") - withServer(SWDiscoveryProxy){ host =>
      testResponse(requests.get(s"$host/get",params=Map("transaction"->transactionTest.getSerializedString)))
    }

    test("POST") - withServer(SWDiscoveryProxy){ host =>
      testResponse(requests.post(s"$host/post",data=Map("transaction"->transactionTest.getSerializedString)))
    }

    test("Empty close connexion")
  }

}
