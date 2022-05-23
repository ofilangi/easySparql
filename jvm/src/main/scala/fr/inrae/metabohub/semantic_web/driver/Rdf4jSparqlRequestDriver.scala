package fr.inrae.metabohub.semantic_web.driver

import fr.inrae.metabohub.semantic_web.exception.SWDiscoveryException
import fr.inrae.metabohub.semantic_web.sparql.QueryResult
import org.eclipse.rdf4j.repository.RepositoryConnection
import org.eclipse.rdf4j.repository.sparql.SPARQLRepository

import scala.concurrent.Future

case class Rdf4jSparqlRequestDriver(idName: String,
                                    url : String,
                                    login: Option[String],
                                    password: Option[String],
                                    token: Option[String],
                                    auth: Option[String]) extends Rdf4jRequestDriver {

  val repo : SPARQLRepository = new SPARQLRepository(url)

  if (login.isDefined && password.isEmpty || login.isEmpty && password.isDefined)
    throw SWDiscoveryException("login [$login] /password [$password] must be defined")
  if (login.isDefined && password.isDefined) repo.setUsernameAndPassword(login.getOrElse(""),password.getOrElse(""))

  val con: RepositoryConnection = repo.getConnection

  def requestOnSWDB(query: String): Future[QueryResult] = requestConnexionRepository(con,query)

}
