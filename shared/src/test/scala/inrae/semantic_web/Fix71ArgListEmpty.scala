package inrae.semantic_web

import inrae.data.DataTestFactory
import inrae.semantic_web.rdf.{IRI, URI}
import inrae.semantic_web.configuration._
import utest.{TestSuite, Tests, test}

import scala.concurrent.ExecutionContext.Implicits.global

object Fix71ArgListEmpty extends TestSuite {
  val insert_data = DataTestFactory.insertVirtuoso1(
    """
      <http://aa> <http://bb> 2 .
      <http://aa> <http://bb> 3 .
      <http://aa> <http://bb> 1 .
      <http://aa> <http://bb> 8 .
      <http://aa> <http://bb> 10 .
      """.stripMargin, this.getClass.getSimpleName)

  val config: SWDiscoveryConfiguration = DataTestFactory.getConfigVirtuoso1()

  def tests = Tests {
    test("Fix #73") {
      insert_data.map(_ => {
        SWDiscovery(config)
          .graph(IRI(DataTestFactory.graph1(this.getClass.getSimpleName)))
          .something()
          .isSubjectOf(URI("http://bb"), "v")
          .select(Seq("*"))
          .commit()
          .raw.map(_ => {
            assert(true)
        })
      }).flatten
    }

    test("Fix #73 - 2") {
      insert_data.map(_ => {
        SWDiscovery(config)
          .graph(IRI(DataTestFactory.graph1(this.getClass.getSimpleName)))
          .something()
          .isSubjectOf(URI("http://bb"), "v")
          .select()
          .commit()
          .raw.map(_ => {
          assert(true)
        })
      }).flatten
    }

    test("Fix #73 - 3") {
      insert_data.map(_ => {
        SWDiscovery(config)
          .graph(IRI(DataTestFactory.graph1(this.getClass.getSimpleName)))
          .something()
          .isSubjectOf(URI("http://bb"), "v")
          .select(List())
          .commit()
          .raw.map(_ => {
          assert(false)
        }).recover(
          _ => assert(true)
        )
      }).flatten
    }

  }
}

