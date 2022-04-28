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
}

case class SWDiscoveryConfiguration(
                                     sources : Seq[Source] = Seq(),
                                     settings : GeneralSetting = new GeneralSetting(),
                                   ) {
  override def toString: String = sources.mkString("\n")+
    "\n\n"  + settings.toString

  def source(idName : String) : Source = {
    sources.find(source => source.id == idName ) match {
      case Some(v : Source) => v
      case None => throw SWStatementConfigurationException("Unknown source id:"+idName )
    }
  }
}
