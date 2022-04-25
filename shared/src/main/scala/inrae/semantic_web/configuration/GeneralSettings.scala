package inrae.semantic_web.configuration

import inrae.semantic_web.exception._

import upickle.default.{macroRW, ReadWriter => RW}
import wvlet.log.LogLevel
import wvlet.log.Logger.rootLogger.warn

object GeneralSetting{
  implicit val rw: RW[GeneralSetting] = macroRW
}

/**
 * GeneralSetting configuration definition.
 *
 * @constructor create a configuration.
 * @param cache to available cache
 * @param logLevel level definition (trace, debug, info, warn, error, all, off)
 * @param sizeBatchProcessing
 * @param pageSize
 * @param proxy
 * @param urlProxy
 */


case class GeneralSetting(
                           cache : Boolean = true,
                           logLevel : String = "warn"          , // trace, debug, info, warn, error, all, off
                           sizeBatchProcessing : Int = 150,
                           pageSize : Int = 10,
                           proxy : Boolean = false ,  /* send request to a discovery proxy */
                           urlProxy : String = "http://urlProxy",
                         ) {

  override def toString: String = {
    "##### General Settings \n"  +
      s" - **cache** :$cache \n" +
      s" - **logLevel** :$logLevel \n" +
      s" - **sizeBatchProcessing** :$sizeBatchProcessing \n" +
      s" - **pageSize** :$pageSize \n" +
      { if (proxy) { " - **urlProxy**:" + urlProxy +"\n"} else {""} }
  }

  def getLogLevel: LogLevel = logLevel.toLowerCase() match {
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

}
