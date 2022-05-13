package fr.inrae.metabohub.semantic_web.driver

import fr.inrae.metabohub.semantic_web.sparql.QueryResult

import scala.concurrent.Future

abstract class HttpRequestDriver  extends RequestDriver {
  def post(query: String): Future[QueryResult]
  def get(query: String): Future[QueryResult]
}
