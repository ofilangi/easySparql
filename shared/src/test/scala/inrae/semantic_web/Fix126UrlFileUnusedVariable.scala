package inrae.semantic_web

import utest.{TestSuite, Tests, test}

import scala.concurrent.ExecutionContext.Implicits.global

object Fix126UrlFileUnusedVariable extends TestSuite {
  val config: StatementConfiguration = StatementConfiguration.setConfigString(
    s"""
        {
         "sources" : [{
           "id"       : "file_turtle",
           "url"      : "http://localhost:8080/animals.ttl"
         }],
         }
        """.stripMargin)

  def tests = Tests {
    test("order by") {
        SWDiscovery(config)
          .prefix("ns0", "http://www.some-ficticious-zoo.com/rdf#")
          .something("animal")
          .datatype("ns0:name","name")
          .select(Seq("animal","name"))
          .commit()
          .raw.map(r => {
            println(r)
          }
        )
    }
  }
}

