package fr.inrae.metabohub.semantic_web
import fr.inrae.metabohub.data.DataTestFactory
import fr.inrae.metabohub.semantic_web.rdf.{IRI, URI}
import fr.inrae.metabohub.semantic_web.configuration._
import utest.{TestSuite, Tests, test}

import scala.concurrent.ExecutionContext.Implicits.global

object SolutionModifierTest extends TestSuite {
  val insertData = DataTestFactory.insertVirtuoso1(
    """
      <http://p1>    <http://xmlns.com/foaf/0.1/name> "Alice" .
      <http://p1>    <http://xmlns.com/foaf/0.1/mbox>  <mailto:alice@example.com> .

      <http://p2>    <http://xmlns.com/foaf/0.1/name>  "Alice" .
      <http://p2>    <http://xmlns.com/foaf/0.1/mbox>   <mailto:asmith@example.com> .

      <http://p3>    <http://xmlns.com/foaf/0.1/name> "Alice" .
      <http://p3>    <http://xmlns.com/foaf/0.1/mbox>   <mailto:alice.smith@example.com> .
      """.stripMargin, this.getClass.getSimpleName)

  val config: SWDiscoveryConfiguration = DataTestFactory.getConfigVirtuoso1()

  val basereq : SWTransaction = SWDiscovery(config)
    .graph(IRI(DataTestFactory.graph1(this.getClass.getSimpleName)))
    .prefix("foaf","http://xmlns.com/foaf/0.1/")
    .something()
    .isSubjectOf(URI("name","foaf"), "name")
    .select(Seq("name"))

  def tests = Tests {
    test("no modifier") {
      insertData.map(_ => {
        basereq.commit()
          .raw.map(r => {
          assert(r("results")("bindings").arr.length == 3)
        })
      }).flatten
    }

    test("limit") {
      insertData.map(_ => {
        basereq
          .limit(1)
          .commit()
          .raw.map(r => {
          assert(r("results")("bindings").arr.length == 1)
        })
      }).flatten
    }

    test("offset") {
      insertData.map(_ => {
        basereq
          .limit(2)
          .offset(1)
          .commit()
          .raw.map(r => {
            assert(true)
        })
      }).flatten
    }

    test("distinct") {
      insertData.map(_ => {
        basereq
          .distinct
          .commit()
          .raw.map(r => {
          assert(r("results")("bindings").arr.length == 1)
        })
      }).flatten
    }

    test("reduced") {
      insertData.map(_ => {
        basereq
          .reduced
          .commit()
          .raw.map(r => {
          assert(r("results")("bindings").arr.length <= 3)
        })
      }).flatten
    }
  }
}
