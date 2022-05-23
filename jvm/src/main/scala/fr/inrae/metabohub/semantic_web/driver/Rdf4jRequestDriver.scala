package fr.inrae.metabohub.semantic_web.driver

import fr.inrae.metabohub.semantic_web.exception._
import fr.inrae.metabohub.semantic_web.event.{DiscoveryRequestEvent, DiscoveryStateRequestEvent}
import fr.inrae.metabohub.semantic_web.sparql.QueryResult
import org.eclipse.rdf4j.query.{MalformedQueryException, UnsupportedQueryLanguageException}
import org.eclipse.rdf4j.query.resultio.sparqljson.SPARQLResultsJSONWriter
import org.eclipse.rdf4j.repository.{RepositoryConnection, RepositoryException}

import java.io.ByteArrayOutputStream
import scala.concurrent.Future
import scala.util.{Failure, Success, Try}

trait Rdf4jRequestDriver extends RequestDriver {

  @throws(classOf[SWDiscoveryException])
  def requestConnexionRepository(con : RepositoryConnection, query : String): Future[QueryResult] = {
    Future {
      publish(DiscoveryRequestEvent(DiscoveryStateRequestEvent.START_HTTP_REQUEST))
      publish(DiscoveryRequestEvent(DiscoveryStateRequestEvent.PROCESS_HTTP_REQUEST))
      val out = new ByteArrayOutputStream()

      Try(con
        .prepareTupleQuery(query)
        .evaluate(new SPARQLResultsJSONWriter(out))) match {
        case Success(_) =>
          publish(DiscoveryRequestEvent(DiscoveryStateRequestEvent.FINISHED_HTTP_REQUEST))
          publish(DiscoveryRequestEvent(DiscoveryStateRequestEvent.RESULTS_BUILD))
          val response = fr.inrae.metabohub.semantic_web.sparql.QueryResult(out.toString())
          publish(DiscoveryRequestEvent(DiscoveryStateRequestEvent.RESULTS_DONE))
          response
        case Failure(e : MalformedQueryException) =>
          publish(DiscoveryRequestEvent(DiscoveryStateRequestEvent.ERROR_HTTP_REQUEST))
          throw SWDiscoveryException(s"** MalformedQueryException **\n$query\n${e.getMessage}")
        case Failure(e : UnsupportedQueryLanguageException) =>
          publish(DiscoveryRequestEvent(DiscoveryStateRequestEvent.ERROR_HTTP_REQUEST))
          throw SWDiscoveryException(s"** UnsupportedQueryLanguageException **\n${e.getMessage}")
        case Failure(e : UnsupportedOperationException) =>
          publish(DiscoveryRequestEvent(DiscoveryStateRequestEvent.ERROR_HTTP_REQUEST))
          throw SWDiscoveryException(s"** UnsupportedOperationException **\n${e.getMessage}")
        case Failure(e : RepositoryException) =>
          publish(DiscoveryRequestEvent(DiscoveryStateRequestEvent.ERROR_HTTP_REQUEST))
          throw SWDiscoveryException(s"** RepositoryException **\ncon:$con\n${e.getMessage}")
        case Failure(e) =>
          publish(DiscoveryRequestEvent(DiscoveryStateRequestEvent.ERROR_HTTP_REQUEST))
          throw SWDiscoveryException(s"** Unknown error ** \n${e.getMessage}")
       }
    }
  }
}
