package inrae.semantic_web

import inrae.data.DataTestFactory
import inrae.semantic_web.rdf.{IRI, URI}
import utest.{TestSuite, Tests, test}

import scala.concurrent.ExecutionContext.Implicits.global

object Fix72CuriUri extends TestSuite {
  val insert_data = DataTestFactory.insert_virtuoso1(
    """
      <http://aa> <http://bb> <http://cc> .
      <http://aa> <http://test#dd> "test" .
      """.stripMargin, this.getClass.getSimpleName)

  val config: StatementConfiguration = DataTestFactory.getConfigVirtuoso1()

  override def utestAfterAll(): Unit = {
    DataTestFactory.delete_virtuoso1(this.getClass.getSimpleName)
  }

  def tests = Tests {
    test("Fix #73") {
      insert_data.map(_ => {
        SWDiscovery(config)
          .graph(IRI(DataTestFactory.graph1(this.getClass.getSimpleName)))
          .prefix("test","http://test#")
          .something("h1")
          .datatype(URI("dd","test"),"dt")
          .console
          .select(Seq("h1","dt"))
          .commit()
          .raw.map(r => {
            println(r)
            assert(r("results")("bindings").arr.length == 6)
        })
      }).flatten
    }
  }
}

