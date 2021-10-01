package inrae.semantic_web

import inrae.data.DataTestFactory
import inrae.semantic_web.rdf.URI
import utest.{TestSuite, Tests, test}

import scala.concurrent.ExecutionContext.Implicits.global



object Fix144TriplyDb extends TestSuite {
  val config: StatementConfiguration = StatementConfiguration.setConfigString(
    s"""
        {
          "sources" : [{
               "id"  : "triplydb",
               "url" : "https://api.triplydb.com/datasets/gr/gr/services/gr/sparql"
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
    test("Fix Triplydb access #144") {
        SWDiscovery(config)
          .something("h1")
          .isSubjectOf(URI("a"),"type")
          .console
          .select(Seq("type"))
          .commit()
          .raw.map(r => {
            println(r)
        })
    }
  }
}

