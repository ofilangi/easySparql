package fr.inrae.metabohub.semantic_web

import fr.inrae.metabohub.data.DataTestFactory
import fr.inrae.metabohub.semantic_web.rdf.{IRI, URI}
import fr.inrae.metabohub.semantic_web.configuration._
import utest.{TestSuite, Tests, test}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object BindSubStrTest extends TestSuite {
  val insertData: Future[Any] = DataTestFactory.insertVirtuoso1(
    """
      <http://aa1> <http://bb> "abcdef" .
      <http://aa2> <http://bb> "abcdefghij" .
      <http://aa3> <http://bb> "abcdefghijklmn" .
      <http://aa1> <http://bb> "defijklm" .
      """.stripMargin, this.getClass.getSimpleName)

  val config: SWDiscoveryConfiguration = DataTestFactory.getConfigVirtuoso1()

  override def utestAfterAll(): Unit = {
    DataTestFactory.deleteVirtuoso1(this.getClass.getSimpleName)
  }

  def tests: Tests = Tests {
    test("bind subStr") {
      insertData.map(_ => {
        SWDiscovery(config)
          .graph(IRI(DataTestFactory.graph1(this.getClass.getSimpleName)))
          .something()
          .isSubjectOf(URI("http://bb"))
          .bind("res").subStr(0,3)
          .select(Seq("res"))
          .distinct
          .commit()
          .raw.map(r => {
          assert(r("results")("bindings").arr.length == 2)
        })
      }).flatten
    }

    test("bind subStr and test") {
      insertData.map(_ => {
        SWDiscovery(config)
          .graph(IRI(DataTestFactory.graph1(this.getClass.getSimpleName)))
          .something()
          .isSubjectOf(URI("http://bb"))
          .bind("res").subStr(0,3)
          .filter.equal("abc")
          .select(Seq("res"))
          .distinct
          .commit()
          .raw.map(r => {
          assert(r("results")("bindings").arr.length == 1)
        })
      }).flatten
    }


  }

}
