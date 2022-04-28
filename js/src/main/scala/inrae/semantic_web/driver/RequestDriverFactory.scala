package inrae.semantic_web.driver

import inrae.semantic_web.configuration._
import inrae.semantic_web.exception.SWDiscoveryException

object RequestDriverFactory {
  def build() : RequestDriverFactory = {
    RequestDriverFactory()
  }
}

case class RequestDriverFactory(lCon : Seq[(RequestDriver, Unit)] = Seq())  {

  def addRepositoryConnection( source : Source ) : RequestDriverFactory = {
    val rq : RequestDriver = source.mimetype match {
      case "application/sparql-query"  =>
        AxiosRequestDriver(
          source.id,
          source.method.getOrElse("POST"),
          source.path,
          source.login,
          source.password,
          source.token,
          source.auth)
      case
        "application/trig" |
        "application/n-quads" |
        "text/turtle" |
        "application/n-triples" |
        "text/n3" |
        "application/ld+json" | "application/json" |
        "application/rdf+xml" |
        "text/rdf-xml" |
        "text/html" |
        "application/xhtml+xml" |
        "image/svg+xml" |
        "application/xml" =>
          ComunicaRequestDriver(
            source.id,
            source.path,
            source.sourcePath,
            source.mimetype,
            source.login,
            source.password)
      case _ =>
        throw SWDiscoveryException("Bad definition of source configuration :"+source.toString)
    }

    RequestDriverFactory(Seq( (rq,()) ))
  }

}
