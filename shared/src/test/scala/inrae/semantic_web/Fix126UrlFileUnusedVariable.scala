package inrae.semantic_web

import inrae.semantic_web.rdf.URI
import inrae.semantic_web.configuration._
import utest.{TestSuite, Tests, test}

import scala.concurrent.ExecutionContext.Implicits.global

object Fix126UrlFileUnusedVariable extends TestSuite {
  val config: SWDiscoveryConfiguration = SWDiscoveryConfiguration.setConfigString(
    """
        {
         "sources" : [{
           "id"       : "file_turtle",
           "file"     : "http://localhost:8080/animals_basic.ttl",
           "mimetype" : "text/turtle"
         }],
         "settings" : {
            "logLevel" : "off",
            "sizeBatchProcessing" : 100
          }
         }
        """.stripMargin)

  def tests = Tests {
    test("order by") {
        SWDiscovery(config)
          .prefix("ns0","http://www.some-ficticious-zoo.com/rdf#")
          .something("animal")
          .datatype(URI("ns0:name"),"name")
          .select(Seq("animal","name"))
          .console
          .commit()
          .raw.map(response => {
            println(response("results")("bindings"))
            println(response("results")("datatypes"))
          }
        )
    }
  }
}

