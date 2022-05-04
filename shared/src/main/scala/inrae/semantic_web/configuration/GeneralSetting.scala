package inrae.semantic_web.configuration

import inrae.semantic_web.exception._
import wvlet.log.LogLevel
import wvlet.log.Logger.rootLogger.warn

object GeneralSetting {
  implicit val rw: OptionPickler.ReadWriter[GeneralSetting] = OptionPickler.macroRW
}

/**
 * GeneralSetting configuration definition.
 *
 * @constructor create a configuration.
 * @param cache to available cache
 * @param logLevel level definition (trace, debug, info, warn, error, all, off)
 * @param sizeBatchProcessing size of number of element inside a sparql request
 * @param pageSize number of result by page when a lazy request is used
 */


case class GeneralSetting(
                           cache : Boolean = true,
                           logLevel : String = "warn"          , // trace, debug, info, warn, error, all, off
                           sizeBatchProcessing : Int = 150,
                           pageSize : Int = 10
                         ) {

  override def toString: String = {
    "##### General Settings \n"  +
      s" - **cache** :$cache \n" +
      s" - **logLevel** :$logLevel \n" +
      s" - **sizeBatchProcessing** :$sizeBatchProcessing \n" +
      s" - **pageSize** :$pageSize \n"
  }

  val _logLevel : LogLevel = logLevel.toLowerCase() match {
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
    throw SWStatementConfigurationException("pageSize can not be equal to zero or negative !")
  }

  if ( sizeBatchProcessing<=0 ) {
    throw SWStatementConfigurationException("sizeBatchProcessing can not be equal to zero or negative !")
  }

}
