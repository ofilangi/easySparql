package inrae.semantic_web.strategy

import inrae.data.DataTestFactory
import inrae.semantic_web.SWDiscovery
import inrae.semantic_web.configuration.SWDiscoveryConfiguration
import inrae.semantic_web.rdf.URI
import utest.{TestSuite, Tests, test}

import scala.concurrent.Future

object ProxyStrategyRequestTest extends TestSuite {
  import scala.concurrent.ExecutionContext.Implicits.global

  val proxy_host: String = "http://localhost:8082"

  val insertData: Future[Any] = DataTestFactory.insertVirtuoso1(
    """<http://aa> <http://bb> <http://cc> .""".stripMargin, this.getClass.getSimpleName)

  override def utestAfterAll(): Unit = {
    DataTestFactory.deleteVirtuoso1(this.getClass.getSimpleName)
  }

  def request(config : SWDiscoveryConfiguration): Future[Unit] = {
    insertData.map(_ => {
      SWDiscovery(config)
        .something("h1")
        .isSubjectOf(URI("http://bb"))
        .select(List("h1"))
        .distinct
        .commit()
        .raw
        .map(r => assert(r("results")("bindings").arr.length == 1))
        .recover {
          case _: java.net.ConnectException => assert(true)
          case _ => assert(false)
        }
    }).flatten
  }

  def tests: Tests = Tests {

    test("proxy post") {
      request(SWDiscoveryConfiguration
        .proxy(s"$proxy_host")
        .sparqlEndpoint(DataTestFactory.urlEndpoint))
    }

    test("proxy get") {
      request(SWDiscoveryConfiguration
        .proxy(s"$proxy_host", method = "get")
        .sparqlEndpoint(DataTestFactory.urlEndpoint))
    }

  }
}
