package fr.inrae.metabohub.semantic_web.strategy

import com.github.p2m2.facade.Axios
import facade.npm.qs
import fr.inrae.metabohub.semantic_web.SWTransaction
import fr.inrae.metabohub.semantic_web.event.{DiscoveryRequestEvent, DiscoveryStateRequestEvent}
import fr.inrae.metabohub.semantic_web.exception.SWDiscoveryException
import fr.inrae.metabohub.semantic_web.sparql.QueryResult

import scala.concurrent.Future
import scala.scalajs.js
import scala.scalajs.js.{Dynamic, JSON, URIUtils}

case class ProxyStrategyRequest(urlProxy: String, method: String = "post") extends StrategyRequest {
  implicit val ec: scala.concurrent.ExecutionContext = scala.concurrent.ExecutionContext.global

  def execute(transaction: SWTransaction): Future[QueryResult] =
    method match {
      case "post" => post("transaction",transaction.removeProxyConfiguration.getSerializedString)
      case "get" => get("transaction",transaction.removeProxyConfiguration.getSerializedString)
    }


  def request(query: String): Future[QueryResult] = { Future { QueryResult("Not yet implemented !") } }

  def get(key: String, value: String): Future[QueryResult] = {
    publish(DiscoveryRequestEvent(DiscoveryStateRequestEvent.PROCESS_HTTP_REQUEST))

    val configAxios = Dynamic.literal(
      "header" -> Dynamic.literal(
        "Accept" -> "application/json"
      )
    )

    Axios.get(s"$urlProxy/get?$key="+URIUtils.encodeURIComponent(value),configAxios).toFuture.map(response => {
      publish(DiscoveryRequestEvent(DiscoveryStateRequestEvent.FINISHED_HTTP_REQUEST))
      QueryResult(JSON.stringify(response.data))
    }).recover(
      e => {
        publish(DiscoveryRequestEvent(DiscoveryStateRequestEvent.ERROR_HTTP_REQUEST))
        throw SWDiscoveryException(e.getMessage)
      } )
  }

  def post(key: String, value: String): Future[QueryResult] = {
    publish(DiscoveryRequestEvent(DiscoveryStateRequestEvent.PROCESS_HTTP_REQUEST))
    println("urlProxy:"+urlProxy)
    val configAxios = Dynamic.literal(
      "url" -> "urlProxy/post",
      "method" -> "POST",
      "header" -> Dynamic.literal(
        "Accept" -> "application/json",
        "Content-Type" -> "application/x-www-form-urlencoded"
      ),
      "data" -> qs.stringify(js.Dictionary[String](
        key -> value
      ))
    )

    Axios.request(configAxios).toFuture.map( response => {
      publish(DiscoveryRequestEvent(DiscoveryStateRequestEvent.FINISHED_HTTP_REQUEST))
      QueryResult(JSON.stringify(response.data))
    }).recover(
      e => {
        publish(DiscoveryRequestEvent(DiscoveryStateRequestEvent.ERROR_HTTP_REQUEST))
        throw SWDiscoveryException(e.getMessage)
      } )
  }

}
