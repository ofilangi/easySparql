package inrae.semantic_web.FIX

import inrae.data.DataTestFactory
import inrae.semantic_web.{SWDiscovery, StatementConfiguration}
import utest.{TestSuite, Tests, test}

object FIXRegexSomething extends TestSuite {
  val config: StatementConfiguration = StatementConfiguration.setConfigString(
    """
        {
          "sources" : [{
               "id"  : "metabo.ttl",
               "url" : "http://localhost:8080/metabo.ttl"
            }],
            "settings" : {
              "cache" : true,
              "logLevel" : "info",
              "sizeBatchProcessing" : 10,
              "pageSize" : 10
             }
        }
        """.stripMargin)

  override def utestAfterAll(): Unit = {
    DataTestFactory.deleteVirtuoso1(this.getClass.getSimpleName)
  }

  def tests = Tests {

    test("metabo.ttl") {
      SWDiscovery(config)
        .something("h1")
        .filter.regex( "C2C2")
        .select(Seq("h1"))
    }
  }
}
