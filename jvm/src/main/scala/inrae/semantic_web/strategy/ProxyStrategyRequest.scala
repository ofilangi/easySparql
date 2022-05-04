package inrae.semantic_web.strategy

import inrae.semantic_web.SWTransaction
import inrae.semantic_web.sparql.QueryResult

import scala.concurrent.Future

case class ProxyStrategyRequest(urlProxy: String, method : String = "post") extends StrategyRequest {
  implicit val ec: scala.concurrent.ExecutionContext = scala.concurrent.ExecutionContext.global

  def execute(sw: SWTransaction): Future[QueryResult] = {
    println("HHHHHHHHHHHHHHHHHHHHHHH :"+urlProxy+"/post")
    Future {
      QueryResult(requests.post(s"$urlProxy/post",data=Map("transaction"->sw.getSerializedString)).text())
    }
  }

  def request(query: String): Future[QueryResult] = {
    Future {
      QueryResult(requests.get(s"$urlProxy",params=Map("query"->query)).text())
    }
  }

}
