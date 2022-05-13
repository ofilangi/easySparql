package fr.inrae.metabohub.semantic_web.driver

import fr.inrae.metabohub.semantic_web.configuration._
import fr.inrae.metabohub.semantic_web.driver.ComunicaRequestDriver.SourceComunica
import fr.inrae.metabohub.semantic_web.event.{DiscoveryRequestEvent, DiscoveryStateRequestEvent}
import fr.inrae.metabohub.semantic_web.sparql.QueryResult
import fr.inrae.metabohub.semantic_web.strategy.StrategyRequest
import fr.inrae.metabohub.semantic_web.{SWTransaction, SparqlQueryBuilder}
import fr.inrae.metabohub.semantic_web.exception._

import scala.concurrent.Future

case class ComunicaFederatedStrategy(sources: Seq[Source]) extends StrategyRequest {
  implicit val ec: scala.concurrent.ExecutionContext = scala.concurrent.ExecutionContext.global
  /* Manage N3Store for content definition */
  val lSourcesDefinition : Future[List[SourceComunica]] =
    Future.sequence(sources.toList.collect {
      case source : Source if source.sourcePath == SourcePath.UrlPath => Future { ComunicaRequestDriver.sourceFromUrl(source.path,source.mimetype) }.asInstanceOf[Future[SourceComunica]]
      case source : Source if source.sourcePath == SourcePath.Content => ComunicaRequestDriver.sourceFromContent(source.path,source.mimetype).asInstanceOf[Future[SourceComunica]]
      case _ => throw SWDiscoveryException("unknown source definition.")
    })

  def execute(swt: SWTransaction): Future[QueryResult] = {
    publish(DiscoveryRequestEvent(DiscoveryStateRequestEvent.QUERY_BUILD))
    val query: String = SparqlQueryBuilder.selectQueryString(swt.sw.rootNode)
    request(query)
  }

  def request(query: String): Future[QueryResult] = {

    lSourcesDefinition.flatMap( lSources => {
      ComunicaRequestDriver.requestOnSWDBWithSources(query, lSources)
    }

    )
  }
}
