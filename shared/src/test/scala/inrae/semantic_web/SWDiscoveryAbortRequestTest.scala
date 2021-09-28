package inrae.semantic_web

import inrae.data.DataTestFactory
import inrae.semantic_web.rdf._
import utest._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.language.postfixOps

object SWDiscoveryAbortRequestTest extends TestSuite {

  val insertData = DataTestFactory.insertVirtuoso1(
    """
      <aaSWAbortRequestTest> <bb> <cc> .
      <aa> <datatype> "testdatatype" .
      """.stripMargin, this.getClass.getSimpleName)

  val config: StatementConfiguration = DataTestFactory.getConfigVirtuoso1()


  def tests = Tests {
    test("Abort Request steps") {
      insertData.map(_ => {
      val swr =
        SWDiscovery(config).something("h1")
          .graph(IRI(DataTestFactory.graph1(this.getClass.getSimpleName)))
          .isSubjectOf(QueryVariable("h2"),"h3")
          .select(List("h1","h2","h3"))

      swr.abort()

      assert(swr.currentRequestEvent == "ABORTED_BY_THE_USER")

      swr.raw
        .map( v => assert(false))
        .recover( _ => assert(true))
      }).flatten
    }
  }
}
