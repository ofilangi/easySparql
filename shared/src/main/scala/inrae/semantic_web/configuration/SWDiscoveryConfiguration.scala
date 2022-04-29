/**
 * Discovery configuration definition.
 *
 *
 *
 */
package inrae.semantic_web.configuration

import inrae.semantic_web.exception._

import scala.scalajs.js.annotation.{JSExport, JSExportTopLevel}
import scala.util.{Failure, Success}

@JSExportTopLevel(name="SWDiscoveryConfiguration")
object SWDiscoveryConfiguration {
  implicit val rw: OptionPickler.ReadWriter[SWDiscoveryConfiguration] = OptionPickler.macroRW

  /**
   * set a config using string configuration
   * @param json_conf : configuration in json format
   */
  @JSExport
  def setConfigString(json_conf: String) : SWDiscoveryConfiguration = {
    util.Try(OptionPickler.read[SWDiscoveryConfiguration](json_conf))
    match {
      case Success(v) => v
      case Failure(e) => throw SWStatementConfigurationException(e.getMessage)
    }
  }

  @JSExport
  def init() : SWDiscoveryConfiguration = SWDiscoveryConfiguration()
}

case class SWDiscoveryConfiguration(
                                     settings : GeneralSetting = new GeneralSetting(),
                                     sources : Seq[Source] = Seq(),
                                   ) {

  @JSExport
  def source(idName : String) : Source = {
    sources.find(source => source.id == idName ) match {
      case Some(v : Source) => v
      case None => throw SWStatementConfigurationException("Unknown source id:"+idName )
    }
  }

  @JSExport
  def urlFile(
               filename : String ,
               mimetype : String = "text/turtle",
               method: String    = "POST",
               auth : String     = "",
               login : String    = "" ,
               password : String = "",
               token : String    = "") : SWDiscoveryConfiguration = _url(filename,mimetype,method,auth,login,password,token)

  @JSExport
  def sparqlEndpoint( url : String,
                      method: String    = "POST",
                      auth : String     = "",
                      login : String    = "" ,
                      password : String = "",
                      token : String    = "" ) : SWDiscoveryConfiguration =
    _url(url,"application/sparql-query",method,auth,login,password,token)

  // Not managed for node/js
  //@JSExport
  def localFile(
                 filename : String ,
                 mimetype : String = "text/turtle") : SWDiscoveryConfiguration =
    SWDiscoveryConfiguration(
      settings,
      sources :+ Source(
        id=filename,
        path=filename,
        sourcePath=SourcePath.LocalFile,
        mimetype = mimetype
      )
    )

  @JSExport
  def rdfContent(
                  content : String ,
                  mimetype : String = "text/turtle") : SWDiscoveryConfiguration =
    SWDiscoveryConfiguration(
      settings,
      sources :+ Source(
        id=content.hashCode.toString,
        path=content,
        sourcePath=SourcePath.Content,
        mimetype = mimetype
      )
    )

  @JSExport
  def sourcesSize: Int = sources.length

  @JSExport
  def setPageSize(pageSize : Int) : SWDiscoveryConfiguration = SWDiscoveryConfiguration(
    settings.copy(pageSize=pageSize),
    sources)

  @JSExport
  def pageSize: Int  = settings.pageSize

  @JSExport
  def setSizeBatchProcessing(sizeBatchProcessing : Int)  : SWDiscoveryConfiguration = SWDiscoveryConfiguration(
    settings.copy(sizeBatchProcessing=sizeBatchProcessing),
    sources)

  @JSExport
  def sizeBatchProcessing: Int  = settings.sizeBatchProcessing

  @JSExport
  def setLogLevel(logLevel : String) : SWDiscoveryConfiguration = SWDiscoveryConfiguration(
    settings.copy(logLevel=logLevel),
    sources)

  @JSExport
  def logLevel: String = settings.logLevel

  @JSExport
  def setCache(cache : Boolean) : SWDiscoveryConfiguration = SWDiscoveryConfiguration(
    settings.copy(cache=cache),
    sources)

  @JSExport
  def cache: Boolean = settings.cache

  private[this] def _url(
                          path : String ,
                          mimetype : String = "",
                          method: String    = "POST",
                          auth : String     = "",
                          login : String    = "" ,
                          password : String = "",
                          token : String    = "") : SWDiscoveryConfiguration =
    SWDiscoveryConfiguration(
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
}
