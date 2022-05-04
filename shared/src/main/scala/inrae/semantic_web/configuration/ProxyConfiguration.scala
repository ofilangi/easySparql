package inrae.semantic_web.configuration

import inrae.semantic_web.exception.SWStatementConfigurationException


object ProxyConfiguration {
  implicit val rw: OptionPickler.ReadWriter[ProxyConfiguration] = OptionPickler.macroRW
}

case class ProxyConfiguration(url : String, method : String = "post") {
  method.toLowerCase.trim match {
    case "post" | "get" =>
    case _ => throw SWStatementConfigurationException(
      s"unknown proxy method definition [$method]. should be post | get")
  }
}
