package inrae.semantic_web.configuration

import scala.collection.Seq
import scala.scalajs.js.annotation.JSExportTopLevel


@JSExportTopLevel(name="SWConfigurationBuilder")
case class SWConfigurationBuilder(settings : GeneralSetting=GeneralSetting(), sources : Seq[Source] = Seq()) {

  private[this] def _url(
                path : String ,
                mimetype : String = "",
                method: String    = "POST",
                auth : String     = "",
                login : String    = "" ,
                password : String = "",
                token : String    = "") : SWConfigurationBuilder =
    SWConfigurationBuilder(
      settings,
      sources :+ Source(
        id=path,
        path=path,
        sourcePath=SourcePath.UrlPath,
        mimetype=mimetype,
        method=Some(method),
        auth= auth match {
          case "" => None
          case v => Some(v)
        },
        login= login match {
          case "" => None
          case v => Some(v)
        },
        password= password match {
          case "" => None
          case v => Some(v)
        },
        token= token match {
          case "" => None
          case v => Some(v)
        })
    )

  def urlFile(
           filename : String ,
           mimetype : String = "text/turtle",
           method: String    = "POST",
           auth : String     = "",
           login : String    = "" ,
           password : String = "",
           token : String    = "") : SWConfigurationBuilder = _url(filename,mimetype,method,auth,login,password,token)

  def sparqlEndpoint( url : String,
                      method: String    = "POST",
                      auth : String     = "",
                      login : String    = "" ,
                      password : String = "",
                      token : String    = "" ) : SWConfigurationBuilder =
    _url(url,"application/sparql-query",method,auth,login,password,token)

  def localFile(
            filename : String ,
            mimetype : String = "text/turtle") : SWConfigurationBuilder =
    SWConfigurationBuilder(
      settings,
      sources :+ Source(
        id=filename,
        path=filename,
        sourcePath=SourcePath.LocalFile,
        mimetype = mimetype
      )
    )

  def rdfContent(
                 content : String ,
                 mimetype : String = "text/turtle") : SWConfigurationBuilder =
    SWConfigurationBuilder(
      settings,
      sources :+ Source(
        id=content.hashCode.toString,
        path=content,
        sourcePath=SourcePath.Content,
        mimetype = mimetype
      )
    )

  def setPageSize(pageSize : Int) : SWConfigurationBuilder = SWConfigurationBuilder(
    settings.copy(pageSize=pageSize),
    sources)

  def setSizeBatchProcessing(sizeBatchProcessing : Int)  : SWConfigurationBuilder = SWConfigurationBuilder(
    settings.copy(sizeBatchProcessing=sizeBatchProcessing),
    sources)

  def setLogLevel(logLevel : String) : SWConfigurationBuilder = SWConfigurationBuilder(
    settings.copy(logLevel=logLevel),
    sources)

  def setCache(cache : Boolean) : SWConfigurationBuilder = SWConfigurationBuilder(
    settings.copy(cache=cache),
    sources)
}