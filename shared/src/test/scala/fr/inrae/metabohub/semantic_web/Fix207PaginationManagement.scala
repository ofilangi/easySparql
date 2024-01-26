package fr.inrae.metabohub.semantic_web

import fr.inrae.metabohub.data.DataTestFactory
import fr.inrae.metabohub.semantic_web.rdf.{IRI, Literal, PropertyPath, URI}
import fr.inrae.metabohub.semantic_web.configuration._
import utest.{TestSuite, Tests, test}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object Fix207PaginationManagement extends TestSuite {
  /*
  val insert_data = DataTestFactory.insertVirtuoso1(
    """
      <http://aa> <http://bb> 2 .
      <http://aa> <http://bb> 3 .
      <http://aa> <http://bb> 1 .
      <http://aa> <http://bb> 8 .
      <http://aa> <http://bb> 10 .
      """.stripMargin, this.getClass.getSimpleName)

  val config: SWDiscoveryConfiguration = DataTestFactory.getConfigVirtuoso1()*/

  def tests = Tests {
    test("Fix #207 Pagination") {
      val configTemp =
        SWDiscoveryConfiguration.init()
        .sparqlEndpoint("https://forum.semantic-metabolomics.fr/sparql")
        .setPageSize(40)
        .setSizeBatchProcessing(10)

      val cidRequest = "CID1060"
      val meshid = "D005947"
     // insert_data.map(_ => {
        SWDiscovery(configTemp)
          .directive("DEFINE input:inference \"schema-inference-rules\"")
         // .graph(IRI(DataTestFactory.graph1(this.getClass.getSimpleName)))
          .prefix("dcterm", "http://purl.org/dc/terms/")
          .prefix("cid", "http://rdf.ncbi.nlm.nih.gov/pubchem/compound/")
          .prefix("cito", "http://purl.org/spar/cito/")
          .prefix("meshv", "http://id.nlm.nih.gov/mesh/vocab#")
          .prefix("mesh", "http://id.nlm.nih.gov/mesh/")
          .prefix("fabio", "http://purl.org/spar/fabio/")
          .something("cid")
          .set(URI("cid:" + cidRequest))
          .isSubjectOf(URI("cito:isDiscussedBy"), "pmid")
          .datatype(URI("http://purl.org/dc/terms/title"), "title")
            .datatype(URI("http://purl.org/dc/terms/date"), "date")
          .isSubjectOf(
            PropertyPath(
              "fabio:hasSubjectTerm|fabio:hasSubjectTerm/meshv:hasDescriptor"
            ),
            "mesh_ini"
          )
          .isSubjectOf(
            PropertyPath(
              "meshv:treeNumber|meshv:treeNumber/meshv:parentTreeNumber"
            )
          )
          .isObjectOf(URI("meshv:treeNumber"))
          .set(URI("mesh:" + meshid))
          .focus("mesh_ini")
          .isA(URI("meshv:TopicalDescriptor"))
          .focus("mesh_ini")
          .isSubjectOf(URI("meshv:active"))
          .set(Literal(1))
          .focus("pmid")
          .isSubjectOf(URI("dcterm:date"), "dateToOrdered")
          .console
          .selectDistinctByPage(Seq("pmid","title", "date"))
          /*
          .then(( args: [number,(SWTransaction[])] ) => {
          let numberOfPages: number = Object.values(args)[0] as number;
          let lazyPage: SWTransaction[] = Object.values(
            args
          )[1] as Array<SWTransaction>;
          resolve([numberOfPages, lazyPage]);
        })
      .catch((error: any) => {
          reject(error);
        });*/
          .map(args => {
            val nbSolution = args._1
            println(nbSolution)
            val results = args._2
            Future.sequence(results.indices.map(block => {

              results(block).commit().raw.map({
                r => {
                  //if (r("results")("bindings").arr.length != 40)

                      println(r("results")("bindings").arr.length)
                    // println(r("results")("bindings").arr)
                    //  results(block).console


                  //assert(r("results")("bindings").arr.length == 40)

                  //r("results")("bindings").arr.map( json => SparqlBuilder.createLiteral(json("obj")))
                    //.map( lit => lit.toInt )}
                }})
              }))
            })
    //  }).flatten
    }
  }
}

