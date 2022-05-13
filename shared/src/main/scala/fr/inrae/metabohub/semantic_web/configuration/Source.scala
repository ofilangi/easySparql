package fr.inrae.metabohub.semantic_web.configuration

import fr.inrae.metabohub.semantic_web.configuration.SourcePath.{SourcePath, UrlPath}
import fr.inrae.metabohub.semantic_web.exception.SWStatementConfigurationException

object Source {
  implicit val rw: OptionPickler.ReadWriter[Source] = OptionPickler.macroRW
}

case class Source(
                   id:String, /* identify the source endpoint */
                   path: String,
                   sourcePath: SourcePath = UrlPath, /* local file access */
                   mimetype: String,
                   method: Option[String] = None, /* POST, POST_ENCODED, GET */
                   auth : Option[String]  = None, /* basic, digest, bearer, proxy */
                   login : Option[String]  = None,
                   password : Option[String] = None,
                   token : Option[String] = None
                 ) {
  override def toString: String = {
    { "##### ID :" + id +"\n"} +
      { " - **path**:" + path +"\n"} +
      { " - **file**:" + sourcePath +"\n"}  +
      { " - **mimetype**:" + mimetype +"\n"}  +
      { " - **method**:" + method +"\n"}  +
      { " - **auth**:" + auth +"\n"}  +
      { " - **method**:" + token +"\n"}
  }

  val mimetype_legal = List(
    "application/sparql-query",
    "text/turtle",
    "text/n3",
    "text/rdf-xml",
    "application/rdf+xml"
  )

  mimetype match {
    case a if sourcePath == SourcePath.UrlPath && ! mimetype_legal.contains(a) =>
      throw SWStatementConfigurationException(s"unknown mimetype :$mimetype")
    case _ =>
  }

  val method_legal = List("post","get")

  method match {
    case Some(methodValue) =>  methodValue.toLowerCase() match {
      case a if ! method_legal.contains(a) => throw SWStatementConfigurationException("method source unknown :" + method)
      case _ =>
    }
    case None =>
  }

  val auth_legal = List("basic", "digest", "bearer", "proxy")

  auth match {
    case Some(authValue) => authValue.toLowerCase() match {
      case a if ! auth_legal.contains(a) => throw SWStatementConfigurationException(s"auth source is not managed :$auth")
      case _ =>
    }
    case None =>
  }
}
