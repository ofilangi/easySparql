package inrae.semantic_web.strategy

import inrae.semantic_web.exception._
import inrae.semantic_web.configuration._

/**
 * Build a strategy to request a set of web sem sources (triple store/file/inline turtle)
 * and configuration : proxy/proxyUrl
 */

object StrategyRequestBuilder {

  def build(config: SWDiscoveryConfiguration): StrategyRequest = {
    println("=================================================")
    println(config)
    config.sources.length match {
      case 0 => throw SWDiscoveryException("No sources specified")
      case _ if ! config.settings.proxy.isEmpty => ProxyStrategyRequest(config.settings.proxy)
      case 1 => DiscoveryStrategyRequest(config.sources(0))
      case _ => Rdf4jFederatedStrategy(config.sources)
    }
  }
}
