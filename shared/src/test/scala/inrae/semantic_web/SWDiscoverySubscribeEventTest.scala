package inrae.semantic_web

import inrae.data.DataTestFactory
import inrae.semantic_web.rdf._
import inrae.semantic_web.configuration._
import utest._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.language.postfixOps

object SWDiscoverySubscribeEventTest extends TestSuite {

  DataTestFactory.deleteVirtuoso1(this.getClass.getSimpleName)

  DataTestFactory.insertVirtuoso1(
    """
      <http://aaSWSubscribeEventTest> <http://bb> <http://cc> .
      <http://aa> <http://datatype> "testdatatype" .
      """.stripMargin, this.getClass.getSimpleName)

  val config: SWDiscoveryConfiguration = DataTestFactory.getConfigVirtuoso1()

  def stepDiscoveryExecutor(unsubscribe : Boolean = false) = {
    var stepDiscovery : Map[String,Boolean] = Map(
      "QUERY_BUILD" -> false,
      "DATATYPE_BUILD" -> false,
      "DATATYPE_DONE" -> false,
      "START_HTTP_REQUEST" -> false,
      "PROCESS_HTTP_REQUEST" -> false,
      "FINISHED_HTTP_REQUEST" -> false,
      "RESULTS_BUILD" -> false,
      "RESULTS_DONE"-> false)

    def funEvent( event: String ) =  {
      stepDiscovery = stepDiscovery + (event -> true)
    }

    def funProg( percent: Double ) =  {
      assert(percent <=1)
      assert(percent >=0)
    }

    val sw = SWDiscovery(config)

    val swr = sw.something("h1")
      .isSubjectOf(URI("http://bb"))
      .select(List("h1"))

    if(! unsubscribe) {
      swr.requestEvent(funEvent)
      swr.progression(funProg)
    }

    swr.commit().raw.map(_ => {
          assert(swr.currentRequestEvent == "REQUEST_DONE")
          if (unsubscribe)
            assert(stepDiscovery.forall( x => ! x._2))
          else {
            assert(stepDiscovery.forall( x => x._2))
          }
        })
    }

  def tests = Tests {

    test("DiscoveryRequestEvent steps") {
      stepDiscoveryExecutor(false)
    }

    test("unsubscribe") {
      stepDiscoveryExecutor(true)
    }

    test("DiscoveryRequestEvent ERROR_HTTP_REQUEST") {
      val config: SWDiscoveryConfiguration =
        SWDiscoveryConfiguration.setConfigString(""" {
                               |         "sources" : [{
                               |           "id"       : "badtps",
                               |           "path"      : "http://bidon",
                               |           "mimetype" : "application/sparql-query"
                               |         }]} """.stripMargin)

      var stepDiscovery : Map[String,Boolean] = Map(
        "ERROR_HTTP_REQUEST" -> false
      )

       val swr =
        SWDiscovery(config).something("h1")
        .isSubjectOf(URI("http://bb"))
        .select(List("h1"))

      swr.commit().raw.map( _=> assert(false))
        .recover( a => {
          println(swr.currentRequestEvent)
          assert(swr.currentRequestEvent == "ERROR_HTTP_REQUEST") } )
    }
  }
}
