package inrae.semantic_web.configuration

import scala.collection.Seq
import scala.scalajs.js.annotation.{JSExport, JSExportTopLevel}

@JSExportTopLevel(name="SWConfigurationBuilder")
object SWConfigurationBuilder {

  def file( filename : String, mimetype : String ) : SWConfigurationBuilder = SWConfigurationBuilder(ConfigurationObject.GeneralSetting(),null)
  def sparqlEndpoint( filename : String ) : SWConfigurationBuilder = SWConfigurationBuilder(ConfigurationObject.GeneralSetting(),null)
  def catalog( name : String ) : SWConfigurationBuilder = SWConfigurationBuilder(ConfigurationObject.GeneralSetting(),null)

}

case class SWConfigurationBuilder(settings : ConfigurationObject.GeneralSetting, sources : Seq[ConfigurationObject.Source]) {

}