package inrae.semantic_web.strategy

import inrae.data.DataTestFactory
import inrae.semantic_web.SWDiscovery
import inrae.semantic_web.rdf.URI
import utest.{TestSuite, Tests, test}

object ProxyStrategyRequestTest extends TestSuite {
  implicit val ec: scala.concurrent.ExecutionContext = scala.concurrent.ExecutionContext.global

  val insertData = DataTestFactory.insertVirtuoso1(
    """
      <aaRosHttpDriverTest> <bb> <cc> .
      """.stripMargin, this.getClass.getSimpleName)
  def tests = Tests {
    test("test") {
      val swt =
        SWDiscovery()
          .something("h1")
          .isSubjectOf(URI("http://something_else"))
          .select(List("h1"))

    //  ProxyStrategyRequest("http://localhost/test").execute(swt)
    }
  }
}
