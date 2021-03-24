package inrae.semantic_web.strategy

import inrae.semantic_web.driver.ComunicaFederatedStrategy
import inrae.semantic_web.{SWDiscoveryException, StatementConfiguration}

/**
 * Build a strategy to request a set of web sem sources (triple store/file/inline turtle)
 * and configuration : proxy/proxyUrl
 */
object StrategyRequestBuilder {

  def build(config: StatementConfiguration): StrategyRequest = {

    config.sources().length match {
      case 0 => throw SWDiscoveryException("No sources specified")
      case _ if config.conf.settings.proxy => ProxyStrategyRequest(config.conf.settings.urlProxy)
      case 1 => DiscoveryStrategyRequest(config.sources()(0))
      case _ => ComunicaFederatedStrategy(config.sources())
    }
  }
}
