package inrae.semantic_web.configuration

import inrae.semantic_web.exception._

import scala.collection.Seq
import scala.scalajs.js.annotation.{JSExport, JSExportTopLevel}

@JSExportTopLevel(name="SWConfigurationBuilder")
object SWConfigurationBuilder {

  def file( filename : String, mimetype : String ) : SWConfigurationBuilder = SWConfigurationBuilder(GeneralSetting(),null)
  def sparqlEndpoint( filename : String ) : SWConfigurationBuilder = SWConfigurationBuilder(GeneralSetting(),null)
  def catalog( name : String ) : SWConfigurationBuilder = SWConfigurationBuilder(GeneralSetting(),null)

}

case class SWConfigurationBuilder(settings : GeneralSetting, sources : Seq[Source]) {

}