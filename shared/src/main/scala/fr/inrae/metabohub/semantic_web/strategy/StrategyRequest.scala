package fr.inrae.metabohub.semantic_web.strategy

import fr.inrae.metabohub.semantic_web.SWTransaction
import fr.inrae.metabohub.semantic_web.driver.RequestDriver
import fr.inrae.metabohub.semantic_web.event.{DiscoveryRequestEvent, Publisher, Subscriber}
import fr.inrae.metabohub.semantic_web.sparql.QueryResult

import scala.concurrent.Future


/** Management of discovery requests
 *
 *  This is further documentation of what we're documenting.
 *  Here are more details about how it works and what it does.
 */
abstract class StrategyRequest()
  extends Publisher[DiscoveryRequestEvent]
    with Subscriber[DiscoveryRequestEvent,RequestDriver] {

  def execute(swt : SWTransaction) : Future[QueryResult]

  def request(query: String): Future[QueryResult]

  def notify(pub: RequestDriver, event: DiscoveryRequestEvent): Unit = {
    publish(DiscoveryRequestEvent(event.state))
  }

}
