package fr.inrae.metabohub.semantic_web.strategy
import fr.inrae.metabohub.semantic_web.configuration._
import fr.inrae.metabohub.semantic_web.driver._
import fr.inrae.metabohub.semantic_web.event.{DiscoveryRequestEvent, DiscoveryStateRequestEvent, Publisher, Subscriber}
import fr.inrae.metabohub.semantic_web.{SWTransaction, SparqlQueryBuilder}
import fr.inrae.metabohub.semantic_web.sparql.QueryResult

import scala.concurrent.Future

case class DiscoveryStrategyRequest(source : Source) extends StrategyRequest {

  val driver : RequestDriver = RequestDriverFactory.build().addRepositoryConnection(source).lCon.map(_._1).last

  driver.subscribe(this.asInstanceOf[Subscriber[DiscoveryRequestEvent,Publisher[DiscoveryRequestEvent]]])

  def execute(swt : SWTransaction) : Future[QueryResult] = {

    publish(DiscoveryRequestEvent(DiscoveryStateRequestEvent.QUERY_BUILD))
    val query: String = SparqlQueryBuilder.selectQueryString(swt.sw.rootNode)
    driver.request(query)
  }

  def request(query: String): Future[QueryResult] = driver.request(query)

}
