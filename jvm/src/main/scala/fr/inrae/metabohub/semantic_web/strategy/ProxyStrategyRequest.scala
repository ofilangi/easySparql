package fr.inrae.metabohub.semantic_web.strategy

import fr.inrae.metabohub.semantic_web.SWTransaction
import fr.inrae.metabohub.semantic_web.event.{DiscoveryRequestEvent, DiscoveryStateRequestEvent}
import fr.inrae.metabohub.semantic_web.sparql.QueryResult

import scala.concurrent.Future

case class ProxyStrategyRequest(urlProxy: String, method: String = "post") extends StrategyRequest {
  implicit val ec: scala.concurrent.ExecutionContext = scala.concurrent.ExecutionContext.global

  def execute(transaction: SWTransaction): Future[QueryResult] = {

    publish(DiscoveryRequestEvent(DiscoveryStateRequestEvent.PROCESS_HTTP_REQUEST))

    Future {
      val qr = method match {
        case "post" =>
          QueryResult(requests.post(s"$urlProxy/post",
            data = Map("transaction" -> transaction.removeProxyConfiguration.getSerializedString)).text())

        case "get" => {
          val l = transaction.removeProxyConfiguration.getSerializedString
          println(l)

          QueryResult(requests.get(s"$urlProxy/get",
            params = Map("transaction" -> transaction.removeProxyConfiguration.getSerializedString)).text())
        }

      }
      publish(DiscoveryRequestEvent(DiscoveryStateRequestEvent.FINISHED_HTTP_REQUEST))
      qr
    }
  }

  def request(query: String): Future[QueryResult] = ???

}
