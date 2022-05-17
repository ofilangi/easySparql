package fr.inrae.metabohub.semantic_web.exception

import scala.scalajs.js.annotation.{JSExport, JSExportTopLevel}

@JSExportTopLevel(name="SWDiscoveryException")
final case class SWDiscoveryException(private val messageInt: String = "",
                                      private val cause: Throwable = None.orNull) extends Exception(messageInt,cause) {
  @JSExport
  def message : String =  messageInt

}