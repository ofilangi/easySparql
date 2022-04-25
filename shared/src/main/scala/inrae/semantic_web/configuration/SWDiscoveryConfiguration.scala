/**
 * Discovery configuration definition.
 *
 *
 *
 */
package inrae.semantic_web.configuration

import inrae.semantic_web.exception._
import upickle.default.{macroRW, ReadWriter => RW}

import scala.scalajs.js.annotation.{JSExport, JSExportTopLevel}
import scala.util.{Failure, Success}

case class StatementConfigurationJson(
                                       sources : Seq[Source],
                                       settings : GeneralSetting = new GeneralSetting(),
                                     ) {
  override def toString: String = sources.mkString("\n")+
    "\n\n"  + settings.toString

}

object StatementConfigurationJson{
  implicit val rw: RW[StatementConfigurationJson] = macroRW
}


@JSExportTopLevel(name="SWStringJsonConfiguration")
object SWDiscoveryConfiguration {
  implicit val rw: RW[SWDiscoveryConfiguration] = macroRW
  /**
   * Set a config using class definition
   * @param conf_ext : configuration
   */
  @JSExport
  def setConfig(conf_ext : StatementConfigurationJson) : SWDiscoveryConfiguration = SWDiscoveryConfiguration(conf_ext)

  /**
   * set a config using string configuration
   * @param json_conf : configuration in json format
   */
  @JSExport
  def setConfigString(json_conf: String) : SWDiscoveryConfiguration = {
    util.Try(upickle.default.read[StatementConfigurationJson](json_conf))
    match {
      case Success(v) => SWDiscoveryConfiguration(v)
      case Failure(e) => throw SWStatementConfigurationException(e.getMessage)
    }
  }

}

//@JSExportTopLevel(name="SWDiscoveryConfiguration")
case class SWDiscoveryConfiguration(
                                   conf : StatementConfigurationJson =
                                   new StatementConfigurationJson(
                                     Seq[Source](),GeneralSetting())
                                 ) {
  override def toString: String = conf.toString



  def source(idName : String) : Source = {
    conf.sources.find(source => source.id == idName ) match {
      case Some(v : Source) => v
      case None => throw SWStatementConfigurationException("Unknown source id:"+idName )
    }
  }

  def sources() : Seq[Source] = conf.sources

}
