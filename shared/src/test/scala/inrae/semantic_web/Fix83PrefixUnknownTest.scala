package inrae.semantic_web

import inrae.data.DataTestFactory
import inrae.semantic_web.rdf.URI
import inrae.semantic_web.configuration._
import utest.{TestSuite, Tests, assert, test}

import scala.concurrent.ExecutionContext.Implicits.global

object Fix83PrefixUnknownTest extends TestSuite {
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
    test("order by") {
      insert_data.map(_ => {
        SWDiscovery(config)
          .prefix("skos", "http://www.w3.org/2004/02/skos/core#")
          .graph("https://forum.semantic-metabolomics.org/EnrichmentAnalysis/CID_MESH/2020")
          .graph("https://forum.semantic-metabolomics.org/EnrichmentAnalysis/CHEBI_MESH/2020")
          .graph("https://forum.semantic-metabolomics.org/EnrichmentAnalysis/CHEMONT_MESH/2020")
          .something("compound")
          .set(URI("CID:CID33698"))
          .select(Seq("compound"))
          .commit()
          .raw.map(r => {
             assert(false) }
          ).recover(exception => {
            assert(true)
          })
      }).flatten
    }
  }
}
