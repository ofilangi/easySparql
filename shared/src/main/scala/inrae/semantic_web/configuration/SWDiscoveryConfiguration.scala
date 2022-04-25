/**
 * Discovery configuration definition.
 *
 *
 *
 */
package inrae.semantic_web.configuration

import upickle.default.{macroRW, ReadWriter => RW}
import wvlet.log.LogLevel
import wvlet.log.Logger.rootLogger.warn

import scala.scalajs.js.annotation.{JSExport, JSExportTopLevel}
import scala.util.{Failure, Success}


final case class StatementConfigurationException(private val message: String = "",
                                            private val cause: Throwable = None.orNull) extends Exception(message,cause)

/**
 * Configuration definition.
 *
 */
@JSExportTopLevel(name="ConfigurationObject")
object ConfigurationObject {

  /* sources configuration */
  @JSExportTopLevel(name="StatementConfigurationJson")
  case class StatementConfigurationJson(
                                         sources : Seq[Source],
                                         settings : GeneralSetting = new GeneralSetting(),
                                       ) {
    override def toString: String = sources.mkString("\n")+
      "\n\n"  + settings.toString

  }
  @JSExportTopLevel(name="Source")
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
      case a if ! mimetype_legal.contains(a) => throw StatementConfigurationException(s"type source unknown :${mimetype}")
      case _ =>
    }

    val method_legal = List("post","get")

    method.toLowerCase() match {
      case a if ! method_legal.contains(a) => throw StatementConfigurationException("method source unknown :" + method)
      case _ =>
    }

    val auth_legal = List("basic", "digest", "bearer", "proxy","")

    auth.toLowerCase() match {
      case a if ! auth_legal.contains(a) => throw StatementConfigurationException(s"auth source not managed :$auth")
      case _ =>
    }

    if ( url == "" && file == ""&& content == "") throw StatementConfigurationException("url/file/content. one of these parameters must be defined.")
    if (( url != "" && file != "") ||
      (url != "" && content != "") ||
      (file != "" && content != "")
    ) throw StatementConfigurationException("url/file/content. only one of theses parameters should be defined .")

  }

  /**
   * GeneralSetting configuration definition.
   *
   * @constructor create a configuration.
   * @param cache to available cache
   * @param logLevel level definition (trace, debug, info, warn, error, all, off)
   * @param sizeBatchProcessing
   * @param pageSize
   * @param proxy
   * @param urlProxy
   */
  @JSExportTopLevel(name="GeneralSetting")
  case class GeneralSetting(
                      cache : Boolean = true,
                      logLevel : String = "warn"          , // trace, debug, info, warn, error, all, off
                      sizeBatchProcessing : Int = 150,
                      pageSize : Int = 10,
                      proxy : Boolean = false ,  /* send request to a discovery proxy */
                      urlProxy : String = "http://urlProxy",
                    ) {

    override def toString: String = {
      "##### General Settings \n"  +
      s" - **cache** :$cache \n" +
        s" - **logLevel** :$logLevel \n" +
        s" - **sizeBatchProcessing** :$sizeBatchProcessing \n" +
        s" - **pageSize** :$pageSize \n" +
        { if (proxy) { " - **urlProxy**:" + urlProxy +"\n"} else {""} }
    }

    def getLogLevel: LogLevel = logLevel.toLowerCase() match {
      case "debug" | "d" => LogLevel.DEBUG
      case "info" | "i" => LogLevel.INFO
      case "warn" | "w" => LogLevel.WARN
      case "error" | "e" => LogLevel.ERROR
      case "trace" | "t" => LogLevel.TRACE
      case "all" => LogLevel.ALL
      case "off" => LogLevel.OFF
      case _ =>
        warn("[config.settings.logLevel] possible value : trace, debug, info, warn, error, all, off . find ["+logLevel+"]")
        LogLevel.WARN
    }

    if ( pageSize<=0 ) {
      throw StatementConfigurationException("pageSize can not be equal to zero or negative !")
    }

  }

  case class Prefixes(list : Map[String,String])

  object Prefixes{
    implicit val rw: RW[Prefixes] = macroRW
  }

  object GeneralSetting{
    implicit val rw: RW[GeneralSetting] = macroRW
  }

  object Source{
    implicit val rw: RW[Source] = macroRW
  }

  object StatementConfigurationJson{
    implicit val rw: RW[StatementConfigurationJson] = macroRW
  }
}

@JSExportTopLevel(name="SWStringJsonConfiguration")
object SWDiscoveryConfiguration {
  implicit val rw: RW[SWDiscoveryConfiguration] = macroRW
  /**
   * Set a config using class definition
   * @param conf_ext : configuration
   */
  @JSExport
  def setConfig(conf_ext : ConfigurationObject.StatementConfigurationJson) : SWDiscoveryConfiguration = SWDiscoveryConfiguration(conf_ext)

  /**
   * set a config using string configuration
   * @param json_conf : configuration in json format
   */
  @JSExport
  def setConfigString(json_conf: String) : SWDiscoveryConfiguration = {
    util.Try(upickle.default.read[ConfigurationObject.StatementConfigurationJson](json_conf))
    match {
      case Success(v) => SWDiscoveryConfiguration(v)
      case Failure(e) => throw StatementConfigurationException(e.getMessage)
    }
  }

}

//@JSExportTopLevel(name="SWDiscoveryConfiguration")
case class SWDiscoveryConfiguration(
                                   conf : ConfigurationObject.StatementConfigurationJson =
                                   new ConfigurationObject.StatementConfigurationJson(
                                     Seq[ConfigurationObject.Source](),ConfigurationObject.GeneralSetting())
                                 ) {
  override def toString: String = conf.toString



  def source(idName : String) : ConfigurationObject.Source = {
    conf.sources.find(source => source.id == idName ) match {
      case Some(v : ConfigurationObject.Source) => v
      case None => throw StatementConfigurationException("Unknown source id:"+idName )
    }
  }

  def sources() : Seq[ConfigurationObject.Source] = conf.sources

}
