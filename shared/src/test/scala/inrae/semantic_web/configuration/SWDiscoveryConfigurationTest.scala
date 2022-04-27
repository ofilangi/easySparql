package inrae.semantic_web.configuration

import inrae.semantic_web.exception.SWStatementConfigurationException
import utest._
import wvlet.log.LogLevel

import scala.util.{Failure, Success, Try}

object SWDiscoveryConfigurationTest extends TestSuite {
  val configBase: String = """
            {
             "sources" : [{
               "id"  : "dbpedia",
               "path" : "https://dbpedia.org/sparql",
               "mimetype" : "application/sparql-query",
               "method" : "POST"
             }],
             "settings" : {
               "cache" : true,
               "logLevel" : "info",
               "sizeBatchProcessing" : 10,
               "pageSize" : 10
             }
            }
            """.stripMargin

  def tests: Tests = Tests {

    test("Create a simple source with string configuration") {
      SWDiscoveryConfiguration.setConfigString(configBase)
    }

    test("Get message error") {
      Try(SWDiscoveryConfiguration.setConfigString("""
            {
            """.stripMargin)) match {
        case Success(_) => assert(false)
        case Failure(_: SWStatementConfigurationException) => assert(true)
        case Failure(_) => assert(false)
      }
    }

    test("Get message error - general setting - virgule") {
      Try(SWDiscoveryConfiguration.setConfigString("""
            {
             "settings" : {
               "cache" : true,
             }
             }
            """.stripMargin)) match {
        case Success(_) => assert(false)
        case Failure(_: SWStatementConfigurationException) => assert(true)
        case Failure(_) => assert(false)
      }
    }

    test("Get a unknown source") {
      val c = SWDiscoveryConfiguration.setConfigString(configBase)

      Try(c.source("something")) match {
        case Success(_) => assert(false)
        case Failure(_) => assert(true)
      }
    }

    test("Create a simple source") {

      val dbname = "dbpedia"
      val url = "http://test"
      val mimetype = "application/sparql-query"

      val configDbpediaBasic: SWDiscoveryConfiguration = SWDiscoveryConfiguration(
        Seq(Source(id=dbname, path=url, mimetype=mimetype)))
      val source = configDbpediaBasic.source("dbpedia")

      assert(source.id == dbname)
      assert(source.path == url)
      assert(source.mimetype == mimetype)
    }

    test("unknown mimetype") {

      val dbname = "dbpedia"
      val url = "http://test"
      val mimetype = " -- "

      Try(SWDiscoveryConfiguration(
        Seq(Source(id=dbname, path=url, mimetype=mimetype)))) match {
        case Success(_) => assert(false)
        case Failure(_) => assert(true)
      }
    }

    test("unknown method") {

      val dbname = "dbpedia"
      val url = "http://test"
      val method = " -- "

      Try(SWDiscoveryConfiguration(
        Seq(Source(id=dbname, path=url, mimetype="application/sparql-query",method=Some(method))))) match {
        case Success(_) => assert(false)
        case Failure(_) => assert(true)
      }
    }

    test("Create a request config with an unknown log level ") {
      assert(SWDiscoveryConfiguration
        .setConfigString(configBase.replace("\"info\"",
          "\"hello.world\"")).settings._logLevel == LogLevel.WARN)
    }

    test("Create a request config log level debug ") {
      Try(SWDiscoveryConfiguration
        .setConfigString(configBase.replace("\"info\"",
          "\"debug\"")).settings._logLevel == LogLevel.DEBUG) match {
        case Success(_) => assert(true)
        case Failure(_) => assert(false)
      }
    }

    test("Create a request config log level info ") {

      val c = SWDiscoveryConfiguration
        .setConfigString(configBase)
      assert(c.settings._logLevel == LogLevel.INFO)

    }
    test("Create a request config log level trace ") {
      val c = SWDiscoveryConfiguration
        .setConfigString(configBase.replace("\"info\"",
          "\"trace\""))
      assert(c.settings._logLevel == LogLevel.TRACE)
    }
    test("Create a request config log level warn ") {
      val c = SWDiscoveryConfiguration
        .setConfigString(configBase.replace("\"info\"",
          "\"warn\""))
      assert(c.settings._logLevel == LogLevel.WARN)
    }

    test("Create a request config log level error ") {
      val c = SWDiscoveryConfiguration
        .setConfigString(configBase.replace("\"info\"",
          "\"error\""))
      assert(c.settings._logLevel == LogLevel.ERROR)
    }

    test("Create a request config log level all ") {
      val c = SWDiscoveryConfiguration
        .setConfigString(configBase.replace("\"info\"",
          "\"all\""))
      assert(c.settings._logLevel == LogLevel.ALL)
    }

    test("Create a request config log level off ") {
      val c = SWDiscoveryConfiguration
        .setConfigString(configBase.replace("\"info\"",
          "\"off\""))
      assert(c.settings._logLevel == LogLevel.OFF)
    }

    test("pageSize can not be negative") {
      Try(SWDiscoveryConfiguration
        .setConfigString(configBase.replace("\"pageSize\" : 10",
          "\"pageSize\" : -1"))) match {
        case Success(_) => assert(false)
        case Failure(_) => assert(true)
      }
    }
    test("pageSize can be equal to zero") {
      Try(SWDiscoveryConfiguration
        .setConfigString(configBase.replace("\"pageSize\" : 10",
          "\"pageSize\" : 0"))) match {
        case Success(_) => assert(false)
        case Failure(_) => assert(true)
      }
    }
    test("pageSize") {
      val c = SWDiscoveryConfiguration
        .setConfigString(configBase.replace("\"pageSize\" : 10",
          "\"pageSize\" : 5"))
      assert(c.settings.pageSize == 5)
    }

    test("sizeBatchProcessing can not be negative") {
      Try(SWDiscoveryConfiguration
        .setConfigString(configBase.replace("\"sizeBatchProcessing\" : 10",
          "\"sizeBatchProcessing\" : -1"))) match {
        case Success(_) => assert(false)
        case Failure(_) => assert(true)
      }
    }
    test("sizeBatchProcessing can be equal to zero") {
      Try(SWDiscoveryConfiguration
        .setConfigString(configBase.replace("\"sizeBatchProcessing\" : 10",
          "\"sizeBatchProcessing\" : 0"))) match {
        case Success(_) => assert(false)
        case Failure(_) => assert(true)
      }
    }
    test("sizeBatchProcessing") {
      val c = SWDiscoveryConfiguration
        .setConfigString(configBase.replace("\"sizeBatchProcessing\" : 10",
          "\"sizeBatchProcessing\" : 5"))
      assert(c.settings.sizeBatchProcessing == 5)
    }
  }
}
