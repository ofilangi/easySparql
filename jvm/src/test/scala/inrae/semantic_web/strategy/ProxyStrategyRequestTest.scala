package inrae.semantic_web.strategy

import fr.inrae.mth.app.SWDiscoveryProxy
import inrae.data.DataTestFactory
import inrae.semantic_web.SWDiscovery
import inrae.semantic_web.SWDiscoverySelectIterable.insertData
import inrae.semantic_web.configuration.SWDiscoveryConfiguration
import inrae.semantic_web.rdf.URI
import io.undertow.Undertow
import utest.{TestSuite, Tests, test}

import scala.concurrent.Future

object ProxyStrategyRequestTest extends TestSuite {
  import scala.concurrent.ExecutionContext.Implicits.global

  val host: String = "http://localhost:8082"
  SWDiscoveryProxy.main(Array("--port","8082","--host","localhost","--verbose"))

  val insertData: Future[Any] = DataTestFactory.insertVirtuoso1(
    """<http://aa> <http://bb> <http://cc> .""".stripMargin, this.getClass.getSimpleName)

  override def utestAfterAll(): Unit = {
    SWDiscoveryProxy.closeService()
    DataTestFactory.deleteVirtuoso1(this.getClass.getSimpleName)
  }

  def request(config : SWDiscoveryConfiguration) = {
    insertData.map(_ => {
      SWDiscovery(config)
        .something("h1")
        .isSubjectOf(URI("http://bb"))
        .select(List("h1"))
        .distinct
        .commit()
        .raw
        .map(r => assert(r("results")("bindings").arr.length == 1) )
        .recover(f => {
          println(f.getMessage);
          assert(false)
        })
    }).flatten
  }

  def tests: Tests = Tests {
    test("proxy post") {
      request(SWDiscoveryConfiguration
        .proxy(s"$host", method = "post")
        .sparqlEndpoint(DataTestFactory.urlEndpoint))
    }

    test("proxy get") {
      request(SWDiscoveryConfiguration
        .proxy(s"$host", method = "get")
        .sparqlEndpoint(DataTestFactory.urlEndpoint))
    }

  }
}
