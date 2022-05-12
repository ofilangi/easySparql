package inrae.semantic_web.strategy

import inrae.semantic_web.driver.ComunicaFederatedStrategy
import inrae.semantic_web.exception._
import inrae.semantic_web.configuration._

/**
 * Build a strategy to request a set of web sem sources (triple store/file/inline turtle)
 * and configuration : proxy/proxyUrl
 */
object StrategyRequestBuilder {

  def build(config: SWDiscoveryConfiguration): StrategyRequest =
    config.proxy match {
      case Some(proxy) => ProxyStrategyRequest(proxy.url,proxy.method)
      case None => config.sources.length match {
      case 0 => throw SWDiscoveryException("No sources specified")
      case 1 => DiscoveryStrategyRequest(config.sources.head)
      case _ => ComunicaFederatedStrategy(config.sources)
    }
  }
}
