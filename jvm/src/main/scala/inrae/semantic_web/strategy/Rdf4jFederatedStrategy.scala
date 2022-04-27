package inrae.semantic_web.strategy

import inrae.semantic_web.configuration._
import inrae.semantic_web.driver.{Rdf4jRequestDriver, Rdf4jSparqlRequestDriver, RequestDriver, RequestDriverFactory}
import inrae.semantic_web.event.{DiscoveryRequestEvent, DiscoveryStateRequestEvent, Publisher, Subscriber}
import inrae.semantic_web.exception.SWDiscoveryException
import inrae.semantic_web.sparql.QueryResult
import inrae.semantic_web.{SWTransaction, SparqlQueryBuilder}
import org.eclipse.rdf4j.federated.endpoint.provider.{NativeRepositoryInformation, SPARQLProvider, SPARQLRepositoryInformation}
import org.eclipse.rdf4j.federated.endpoint.{Endpoint, EndpointClassification, ManagedRepositoryEndpoint, SparqlEndpointConfiguration}
import org.eclipse.rdf4j.federated.repository.{FedXRepository, FedXRepositoryConnection}
import org.eclipse.rdf4j.federated.{FedXConfig, FedXFactory}

import java.util
import scala.concurrent.Future

/**
 * Requests are the responsibility of a third party
 */
case class Rdf4jFederatedStrategy(sources: scala.Seq[Source])
  extends StrategyRequest with Rdf4jRequestDriver {

  val startRequestDriverFactoryInst: RequestDriverFactory = RequestDriverFactory.get
  val requestDriverFactoryInst: RequestDriverFactory = sources.map(startRequestDriverFactoryInst.addRepositoryConnection).last
  /* creation des repos pour les connexions locales */
  val drivers: Seq[RequestDriver] = requestDriverFactoryInst.lCon.map(_._1)

  drivers.map(_.subscribe(this.asInstanceOf[Subscriber[DiscoveryRequestEvent, Publisher[DiscoveryRequestEvent]]]))

  val endpoints: util.ArrayList[Endpoint] = new java.util.ArrayList[Endpoint]()

    drivers.foreach {
      case d: Rdf4jSparqlRequestDriver =>
        val p: SPARQLRepositoryInformation = new SPARQLRepositoryInformation(d.idName, d.url)
        val conf = new SparqlEndpointConfiguration()
        conf.setSupportsASKQueries(false) //virtuoso
        p.setEndpointConfiguration(conf)
        endpoints.add(new SPARQLProvider().loadEndpoint(p))
      case _ =>
    }

  val path: String = requestDriverFactoryInst.dataDir.getAbsolutePath
  val end: ManagedRepositoryEndpoint = requestDriverFactoryInst.repository match {
    case Some(repository) =>
      new ManagedRepositoryEndpoint(new NativeRepositoryInformation("local", path),path, EndpointClassification.Local,repository)
    case None =>
      throw SWDiscoveryException("Repository is not initialized")
  }

  // lock--->
  //endpoints.add(EndpointFactory.loadNativeEndpoint("local",RequestDriverFactory.dataDir))
  endpoints.add(end)
  val fedXConf : FedXConfig = new FedXConfig()
                                .withEnableMonitoring(true)
                                .withLogQueryPlan(true)

  val repositoryFederation: FedXRepository = FedXFactory.newFederation().withConfig(fedXConf).withMembers(endpoints).create()

  val con: FedXRepositoryConnection = repositoryFederation.getConnection

  def execute(swt: SWTransaction): Future[QueryResult] = {

    publish(DiscoveryRequestEvent(DiscoveryStateRequestEvent.QUERY_BUILD))
    val query: String = SparqlQueryBuilder.selectQueryString(swt.sw.rootNode)
    requestOnSWDB(query)
  }

  override def requestOnSWDB(query: String): Future[QueryResult] = requestConnexionRepository(con,query)

}
