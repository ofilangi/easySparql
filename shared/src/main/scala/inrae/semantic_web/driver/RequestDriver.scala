package inrae.semantic_web.driver

import inrae.semantic_web.exception._
import inrae.semantic_web.event.{DiscoveryRequestEvent, DiscoveryStateRequestEvent, Publisher}
import inrae.semantic_web.sparql.{QueryResult, QueryResultManager}
import wvlet.log.Logger.rootLogger.debug

import scala.concurrent.Future

object RequestDriver {
  var qrmBySource = Map[RequestDriver,QueryResultManager]()

  def getQrm(rd: RequestDriver): QueryResultManager = {
    qrmBySource.get(rd) match {
      case Some(qrm) => qrm
      case None => {
        qrmBySource += rd -> QueryResultManager()
        qrmBySource(rd)
      }
    }
  }
}

trait RequestDriver extends Publisher[DiscoveryRequestEvent] {
  implicit val ec: scala.concurrent.ExecutionContext = scala.concurrent.ExecutionContext.global

  def request(query: String): Future[QueryResult] = {
    /* memorize */
    RequestDriver.getQrm(this).get(query) match {
      case Some(resultsString) => Future {
        publish(DiscoveryRequestEvent(DiscoveryStateRequestEvent.RESULTS_DONE))
        QueryResult(resultsString)
      }
      case None => {
        publish(DiscoveryRequestEvent(DiscoveryStateRequestEvent.START_HTTP_REQUEST))
        val dateStart = System.nanoTime
        val t1 = System.nanoTime
        debug("RequestDriver Send request "+dateStart+","+t1)
        requestOnSWDB(query).map(resultsQR => {

          publish(DiscoveryRequestEvent(DiscoveryStateRequestEvent.RESULTS_BUILD))
          val duration = (System.nanoTime - t1) / 1e9d
          debug(s"RequestDriver Receive results  -- Elapsed Time : ${duration}")
          debug("RequestDriver Memorize (Mb) =>"+(resultsQR.results.length.toDouble/(1024*1024)))
          RequestDriver.getQrm(this).set(query, resultsQR.results)
          publish(DiscoveryRequestEvent(DiscoveryStateRequestEvent.RESULTS_DONE))
          resultsQR
        })
      }

    }
  }

  protected def requestOnSWDB(query: String): Future[QueryResult]
}
