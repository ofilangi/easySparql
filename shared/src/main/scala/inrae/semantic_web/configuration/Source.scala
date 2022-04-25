package inrae.semantic_web.configuration

import inrae.semantic_web.exception._
import upickle.default.{macroRW, ReadWriter => RW}

object Source{
  implicit val rw: RW[Source] = macroRW
}

case class Source(
                   id:String, /* identify the source endpoint */
                   url: String       = "", /* url access */
                   file: String      = "", /* local file access */
                   content: String   = "", /* online definition */
                   mimetype: String  = "application/sparql-query",  /* application/sparql-query, 'turtle', 'text/turtle' 'hypermedia' */
                   method: String    = "POST", /* POST, POST_ENCODED, GET */
                   auth : String     = "", /* basic, digest, bearer, proxy */
                   login : String    = "" ,
                   password : String = "",
                   token : String    = ""
                 ) {
  override def toString: String = {
    { "##### ID :" + id +"\n"} +
      { if (url != "") { " - **url**:" + url +"\n"} else {""} } +
      { if (file != "") { " - **file**:" + file +"\n"} else {""} } +
      { if (content != "") { " - **content**:" + content +"\n"} else {""} } +
      { if (mimetype != "") { " - **mimetype**:" + mimetype +"\n"} else {""} } +
      { if (method != "") { " - **method**:" + method +"\n"} else {""} } +
      { if (auth != "") { " - **auth**:" + auth +"\n"} else {""} } +
      { if (token != "") { " - **method**:" + token +"\n"} else {""} }
  }

  val mimetype_legal = List(
    "application/sparql-query",
    "text/turtle",
    "text/n3",
    "text/rdf-xml",
    "application/rdf+xml"
  )

  mimetype match {
    case a if ! mimetype_legal.contains(a) => throw SWStatementConfigurationException(s"type source unknown :${mimetype}")
    case _ =>
  }

  val method_legal = List("post","get")

  method.toLowerCase() match {
    case a if ! method_legal.contains(a) => throw SWStatementConfigurationException("method source unknown :" + method)
    case _ =>
  }

  val auth_legal = List("basic", "digest", "bearer", "proxy","")

  auth.toLowerCase() match {
    case a if ! auth_legal.contains(a) => throw SWStatementConfigurationException(s"auth source not managed :$auth")
    case _ =>
  }

  if ( url == "" && file == ""&& content == "") throw SWStatementConfigurationException("url/file/content. one of these parameters must be defined.")
  if (( url != "" && file != "") ||
    (url != "" && content != "") ||
    (file != "" && content != "")
  ) throw SWStatementConfigurationException("url/file/content. only one of theses parameters should be defined .")

}
