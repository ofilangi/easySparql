package inrae.semantic_web

import inrae.semantic_web.configuration.SWDiscoveryConfiguration
import inrae.semantic_web.rdf.URI
import utest.{TestSuite, Tests, assert, test}

import scala.concurrent.ExecutionContext.Implicits.global

object RequestWithPrefixAndDatatypeTest  extends TestSuite  {

  val turtleContent: String =
    """@prefix ns0: <http://www.some-ficticious-zoo.com/rdf#> .
    ns0:lion ns0:name "Lion" ;
              ns0:species "Panthera leo" ;
              ns0:class "Mammal" .
                                            ns0:tarantula
                                                ns0:name "Tarantula" ;
                                                ns0:species "Avicularia avicularia" ;
                                                ns0:class "Arachnid" .

                                            ns0:hippopotamus
                                                ns0:name "Hippopotamus" ;
                                                ns0:species "Hippopotamus amphibius" ;
                                                ns0:class "Mammal" .
      """.stripMargin

  def tests: Tests = Tests {

    test("prefix with datatype") {

      val configTurtleContent : SWDiscoveryConfiguration = SWDiscoveryConfiguration
        .init()
        .rdfContent(turtleContent)
        .setPageSize(5)
        .setSizeBatchProcessing(10)
        .setLogLevel("warn")
        .setCache(false);

      SWDiscovery(configTurtleContent)
      SWDiscovery(configTurtleContent)
        .prefix("v","http://www.some-ficticious-zoo.com/rdf#")
        .something("h1")
          .datatype("v:name","name")
          .isSubjectOf(URI("v:class"))
           .set("Mammal")
        .select(List("h1","name"))
        .distinct
        .commit()
        .raw
          .map(result => {
            assert(result("results")("datatypes")("name").obj.size == 2)
          })
    }
  }
}
