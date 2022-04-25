package inrae.semantic_web.strategy

import inrae.semantic_web.exception._
import inrae.semantic_web.configuration._
import inrae.semantic_web.driver.{Rdf4jRequestDriver, Rdf4jSparqlRequestDriver, RequestDriver, RequestDriverFactory}
import inrae.semantic_web.event.{DiscoveryRequestEvent, DiscoveryStateRequestEvent, Publisher, Subscriber}
import inrae.semantic_web.sparql.QueryResult
import inrae.semantic_web.{SWTransaction, SparqlQueryBuilder}
import org.eclipse.rdf4j.federated.endpoint.provider.{NativeRepositoryInformation, SPARQLProvider, SPARQLRepositoryInformation}
import org.eclipse.rdf4j.federated.endpoint.{Endpoint, EndpointClassification, ManagedRepositoryEndpoint, SparqlEndpointConfiguration}
import org.eclipse.rdf4j.federated.{FedXConfig, FedXFactory}

import java.util
import scala.concurrent.Future

/**
 * Requests are the responsibility of a third party
 */
case class Rdf4jFederatedStrategy(sources: Seq[Source])
  extends StrategyRequest with Rdf4jRequestDriver {

  /* creation des repos pour les locaux */
  val drivers: Seq[RequestDriver] = sources.map(RequestDriverFactory.build(_))

  drivers.map(_.subscribe(this.asInstanceOf[Subscriber[DiscoveryRequestEvent, Publisher[DiscoveryRequestEvent]]]))

  val endpoints: util.ArrayList[Endpoint] = new java.util.ArrayList[Endpoint]()

    drivers.foreach {
      case d: Rdf4jSparqlRequestDriver => {
        var p: SPARQLRepositoryInformation = new SPARQLRepositoryInformation(d.idName, d.url)
        val conf = new SparqlEndpointConfiguration()
        conf.setSupportsASKQueries(false) //virtuoso
        p.setEndpointConfiguration(conf)
        endpoints.add(new SPARQLProvider().loadEndpoint(p))
      }
      case _ =>
    }

  val path = RequestDriverFactory.dataDir.getAbsolutePath
  val end = new ManagedRepositoryEndpoint(new NativeRepositoryInformation("local", path),path, EndpointClassification.Local,RequestDriverFactory.repository)

  // lock--->
  //endpoints.add(EndpointFactory.loadNativeEndpoint("local",RequestDriverFactory.dataDir))
  endpoints.add(end)
  val fedXConf : FedXConfig = new FedXConfig()
                                .withEnableMonitoring(true)
                                .withLogQueryPlan(true);

  val repositoryFederation = FedXFactory.newFederation().withConfig(fedXConf).withMembers(endpoints).create()

  val con = repositoryFederation.getConnection()

  def execute(swt: SWTransaction): Future[QueryResult] = {

    publish(DiscoveryRequestEvent(DiscoveryStateRequestEvent.QUERY_BUILD))
    val query: String = SparqlQueryBuilder.selectQueryString(swt.sw.rootNode)
    requestOnSWDB(query)
  }

  override def requestOnSWDB(query: String): Future[QueryResult] = requestConnexionRepository(con,query)

}
