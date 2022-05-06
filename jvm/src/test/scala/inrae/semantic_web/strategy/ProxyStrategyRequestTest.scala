package inrae.semantic_web.strategy

import fr.inrae.mth.app.SWDiscoveryProxy
import inrae.data.DataTestFactory
import inrae.semantic_web.SWDiscovery
import inrae.semantic_web.configuration.SWDiscoveryConfiguration
import inrae.semantic_web.rdf.URI
import utest.{TestSuite, Tests, test}

import scala.concurrent.Future

object ProxyStrategyRequestTest extends TestSuite {
  import scala.concurrent.ExecutionContext.Implicits.global

  val insertData: Future[Any] = DataTestFactory.insertVirtuoso1(
    """<http://aa> <http://bb> <http://cc> .""".stripMargin, this.getClass.getSimpleName)

  override def utestAfterAll(): Unit = {
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
        }).map( _ => SWDiscoveryProxy.closeService() )
    }).flatten
  }

  def tests: Tests = Tests {

    val host: String = "http://localhost:8082"


    test("proxy post") {

      SWDiscoveryProxy.main(Array("--port","8082","--host","localhost","--verbose"))

      request(SWDiscoveryConfiguration
        .proxy(s"$host")
        .sparqlEndpoint(DataTestFactory.urlEndpoint))

      SWDiscoveryProxy.closeService()
    }

    test("proxy get") {
      SWDiscoveryProxy.main(Array("--port","8082","--host","localhost","--verbose"))

      request(SWDiscoveryConfiguration
        .proxy(s"$host", method = "get")
        .sparqlEndpoint(DataTestFactory.urlEndpoint))

      SWDiscoveryProxy.closeService()
    }

  }
}
