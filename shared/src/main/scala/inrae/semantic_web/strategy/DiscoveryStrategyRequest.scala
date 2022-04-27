package inrae.semantic_web.strategy

import inrae.semantic_web.exception._
import inrae.semantic_web.configuration._
import inrae.semantic_web.driver._
import inrae.semantic_web.event.{DiscoveryRequestEvent, DiscoveryStateRequestEvent, Publisher, Subscriber}
import inrae.semantic_web.{SWTransaction, SparqlQueryBuilder}
import inrae.semantic_web.node.{Root, pm}
import inrae.semantic_web.sparql.QueryResult
import wvlet.log.Logger.rootLogger.trace

import scala.concurrent.Future

case class DiscoveryStrategyRequest(source : Source) extends StrategyRequest {

  val driver : RequestDriver = RequestDriverFactory.get.addRepositoryConnection(source).lCon.map(_._1).last

  driver.subscribe(this.asInstanceOf[Subscriber[DiscoveryRequestEvent,Publisher[DiscoveryRequestEvent]]])

  def execute(swt : SWTransaction) : Future[QueryResult] = {

    publish(DiscoveryRequestEvent(DiscoveryStateRequestEvent.QUERY_BUILD))
    val query: String = SparqlQueryBuilder.selectQueryString(swt.sw.rootNode)
    driver.request(query)
  }

  def request(query: String): Future[QueryResult] = driver.request(query)

}
