package inrae.semantic_web.strategy

import inrae.semantic_web.configuration._
import utest.{TestSuite, Tests, test}

import scala.util.{Failure, Success, Try}

object StrategyRequestBuilderTest extends TestSuite {

  def tests = Tests {

    test("none source should fail") {
      Try(StrategyRequestBuilder.build(
        SWDiscoveryConfiguration.setConfigString(
          """
          {
           "sources" : []
           }
          """.stripMargin))) match {
        case Success(_) => assert(false)
        case Failure(_) => assert(true)
      }
    }

    test("1 source") {
      Try(StrategyRequestBuilder.build(SWDiscoveryConfiguration
        .setConfigString(
          """{
           "sources" : [{
               "id"  : "dbpedia",
               "url" : "https://dbpedia.org/sparql",
               "mimetype" : "application/sparql-query"
             }]
           }""".stripMargin))) match {
        case Success(_ : DiscoveryStrategyRequest) => assert(true)
        case Success(_) => assert(false)
        case Failure(_) => assert(false)
      }
    }
    test("2 sources") {
      Try(StrategyRequestBuilder.build(SWDiscoveryConfiguration
        .setConfigString(
          """{
           "sources" : [{
               "id"  : "dbpedia",
               "url" : "https://dbpedia.org/sparql",
               "mimetype" : "application/sparql-query"
             },{
               "id"  : "dbpedia2",
               "url" : "https://dbpedia.org/sparql2",
               "mimetype" : "application/sparql-query"
             }]
           }""".stripMargin))) match {
        case Success(_ : Rdf4jFederatedStrategy) => assert(true)
        case Success(_) => assert(false)
        case Failure(_) => assert(false)
      }
    }

    test("2 sources - proxy use") {
      Try(StrategyRequestBuilder.build(SWDiscoveryConfiguration
        .setConfigString(
          """{
           "sources" : [{
               "id"  : "dbpedia",
               "url" : "https://dbpedia.org/sparql",
               "mimetype" : "application/sparql-query"
             },{
               "id"  : "dbpedia2",
               "url" : "https://dbpedia.org/sparql2",
               "mimetype" : "application/sparql-query"
             }],
             "settings" : {
               "proxy"   : true,
               "urlProxy" : "https://myproxy/sparql"
             }
           }""".stripMargin))) match {
        case Success(strategy : ProxyStrategyRequest) => assert(strategy.urlProxy == "https://myproxy/sparql")
        case Success(_) => assert(false)
        case Failure(_) => assert(false)
      }
    }

  }

}
