package inrae.semantic_web.driver

import inrae.semantic_web.configuration.ConfigurationObject.Source
import inrae.semantic_web.exception.SWDiscoveryException

object RequestDriverFactory  {


  def build( source : Source ) : RequestDriver = {

    val graph = "fr:inrae:semantic_web:discovery:"+source.id

    source.mimetype match {
      case "application/sparql-query" if source.url != "" =>
        AxiosRequestDriver(
          source.id,
          source.method,
          source.url,
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
            source.url,
            source.content,
            source.mimetype,
            source.login,
            source.password,
            sourceType = "file")
      case _ =>
        throw SWDiscoveryException("Bad definition of source configuration :"+source.toString)
    }
  }

}
