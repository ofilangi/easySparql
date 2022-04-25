package inrae.semantic_web

import inrae.data.DataTestFactory
import inrae.semantic_web.rdf.URI
import inrae.semantic_web.configuration._
import utest.{TestSuite, Tests, test}



object Fix144TriplyDb extends TestSuite {
  val config: StatementConfiguration = StatementConfiguration.setConfigString(
    """
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
          .filter.contains("Business")
          .select(Seq("type"))

      /*
      ********* Inactive test because remote access ******
          .commit()
          .raw.map(r => {
            println(r)
        })*/
    }
  }
}

