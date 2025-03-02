package fr.inrae.metabohub.data

import facade.npm.{qs}
import com.github.p2m2.facade.Axios
import fr.inrae.metabohub.semantic_web.exception.SWDiscoveryException

import scala.scalajs.js
import scala.scalajs.js.Dynamic

case object PostRequest {
  implicit val ec: scala.concurrent.ExecutionContext = scala.concurrent.ExecutionContext.global

  def put(stringQuery: String, url_endpoint: String) = {
    val configAxios = Dynamic.literal(
      "url" -> url_endpoint,
      "method" -> "POST",
      "header" -> Dynamic.literal(
        "Accept" -> "application/json",
        "Content-Type" -> "application/x-www-form-urlencoded"
      ),
      "data" -> qs.stringify(js.Dictionary[String](
        "query" -> stringQuery
      ))
    )

    Axios.request(configAxios).toFuture.map(response => {

    }).recover(
      e => {
        throw SWDiscoveryException(e.getMessage)
      })

  }
}
