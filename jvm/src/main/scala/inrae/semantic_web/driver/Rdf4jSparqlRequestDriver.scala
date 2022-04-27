package inrae.semantic_web.driver

import inrae.semantic_web.sparql.QueryResult
import org.eclipse.rdf4j.federated.endpoint.SparqlEndpointConfiguration
import org.eclipse.rdf4j.federated.endpoint.provider.SPARQLRepositoryInformation
import org.eclipse.rdf4j.repository.sparql.SPARQLRepository

import scala.concurrent.Future

case class Rdf4jSparqlRequestDriver(idName: String,
                                    url : String,
                                    login: Option[String],
                                    password: Option[String],
                                    token: Option[String],
                                    auth: Option[String]) extends Rdf4jRequestDriver {

  var p : SPARQLRepositoryInformation = new SPARQLRepositoryInformation(idName, url)
  val conf = new SparqlEndpointConfiguration()
  val repo = new SPARQLRepository(url)
  if (login.isDefined && password.isDefined) repo.setUsernameAndPassword(login.getOrElse(""),password.getOrElse(""))

  val con = repo.getConnection()

  def requestOnSWDB(query: String): Future[QueryResult] = requestConnexionRepository(con,query)

}
