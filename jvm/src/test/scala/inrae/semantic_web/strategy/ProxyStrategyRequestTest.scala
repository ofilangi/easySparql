package inrae.semantic_web.strategy

import fr.inrae.mth.app.SWDiscoveryProxy
import inrae.data.DataTestFactory
import inrae.semantic_web.SWDiscovery
import inrae.semantic_web.configuration.SWDiscoveryConfiguration
import io.undertow.Undertow
import utest.{TestSuite, Tests, test}

import scala.concurrent.Future

object ProxyStrategyRequestTest extends TestSuite {
  import scala.concurrent.ExecutionContext.Implicits.global
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

  override def utestAfterAll(): Unit = {
    DataTestFactory.deleteVirtuoso1(this.getClass.getSimpleName)
  }

  def tests: Tests = Tests {
    test("proxy request test") - withServer(SWDiscoveryProxy) { host =>
      println(host)
      val config : SWDiscoveryConfiguration =
        SWDiscoveryConfiguration
        .proxy(s"$host")
          .sparqlEndpoint(DataTestFactory.urlEndpoint)

      insertData.map(_ => {
      SWDiscovery(config)
        .something("h1")
        .select(List("h1"))
        .commit()
        .raw
        .map(_ => assert(true))
        .recover(f => { println(f.getMessage);assert(false) })
      }).flatten
    }

  }
}
