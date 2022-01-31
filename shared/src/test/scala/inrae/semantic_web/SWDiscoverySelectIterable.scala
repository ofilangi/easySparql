package inrae.semantic_web

import inrae.data.DataTestFactory
import inrae.semantic_web.rdf._
import utest._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.language.postfixOps

object SWDiscoverySelectIterable extends TestSuite {

  val data = """
      <http://aa> <http://bb> 1 .
      <http://aa> <http://bb> 2 .
      <http://aa> <http://bb> 3 .
      <http://aa> <http://bb> 4 .
      <http://aa> <http://bb> 5 .
      <http://aa> <http://bb> 6 .
      <http://aa> <http://bb> 7 .
      <http://aa> <http://bb> 8 .
      <http://aa> <http://bb> 9 .
      <http://aa> <http://bb> 10 .
      <http://aa> <http://bb> 11 .
      <http://aa> <http://bb> 12 .
      <http://aa> <http://bb> 13 .
      """.stripMargin

  val insertData = DataTestFactory.insertVirtuoso1(data, this.getClass.getSimpleName)

  val nbValues = data.split(" ").filter( _ == "<http://aa>").length

  val pageSize = 5

  val nblock = (nbValues / pageSize) + 1


  val config: StatementConfiguration = StatementConfiguration.setConfigString(
    s"""
        {
         "sources" : [{
           "id"       : "local",
           "url"      : "${DataTestFactory.urlEndpoint}"
         }],
         "settings" : {
            "logLevel" : "info",
            "sizeBatchProcessing" : 100,
            "pageSize" : ${pageSize}
          }
         }
        """.stripMargin)

  def tests = Tests {

    test("something") {
      insertData.map(_ => {
        SWDiscovery(config)
          .graph(IRI(DataTestFactory.graph1(this.getClass.getSimpleName)))
          .something()
          .set(URI("http://aa"))
          .isSubjectOf(URI("http://bb"), "obj")
          .selectDistinctByPage( List("obj"))
          .map(args => {
            val nb = args._1
            val results = args._2
            assert(nb == nblock)
              Future.sequence((0 to nblock-1).map( iblock => {
                results(iblock).commit().raw.map({
                  r => {
                    assert(r("results")("bindings").arr.length<=pageSize)
                    r("results")("bindings").arr.map( json => SparqlBuilder.createLiteral(json("obj")))
                                                .map( lit => lit.toInt )}
                })
              })).map( list => {
                  assert(list.flatten.sorted == List(1,2,3,4,5,6,7,8,9,10,11,12))
                })
          })
      }).flatten
    }
  }

}
