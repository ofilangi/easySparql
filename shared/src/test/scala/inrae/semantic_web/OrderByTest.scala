package inrae.semantic_web

import inrae.data.DataTestFactory
import inrae.semantic_web.rdf.{IRI, SparqlBuilder, URI}
import inrae.semantic_web.configuration._
import utest.{TestSuite, Tests, test}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success, Try}

object OrderByTest extends TestSuite {
  val insertData = DataTestFactory.insertVirtuoso1(
    """
      <http://aa> <http://bb> 2 .
      <http://aa> <http://bb> 3 .
      <http://aa> <http://bb> 1 .
      <http://aa> <http://bb> 8 .
      <http://aa> <http://bb> 10 .
      """.stripMargin, this.getClass.getSimpleName)

  val config: StatementConfiguration = DataTestFactory.getConfigVirtuoso1()

  def tests = Tests {
    test("order by") {
      insertData.map(_ => {
        SWDiscovery(config)
          .graph(IRI(DataTestFactory.graph1(this.getClass.getSimpleName)))
          .something()
          .isSubjectOf(URI("http://bb"), "v")
          .select(Seq("v"))
          .orderByAsc("v")
          .commit()
          .raw.map(r => {
          assert(r("results")("bindings").arr.length == 5)

          val tab = r("results")("bindings").arr.map( arrow => {
            SparqlBuilder.createLiteral(arrow("v")).toInt
          })
          assert(tab.sorted == tab)
        })
      }).flatten
    }

    test("order by with wrong variable") {
        Try(SWDiscovery(config)
          .graph(IRI(DataTestFactory.graph1(this.getClass.getSimpleName)))
          .something()
          .isSubjectOf(URI("http://bb"), "v")
          .select(Seq("v"))
          .orderByAsc("v_bad")) match {
          case Success(_) => println("Success");assert(false)
          case Failure(_) => println("Failure");assert(true)
        }
    }

    test("order by with list") {
      insertData.map(_ => {
        SWDiscovery(config)
          .graph(IRI(DataTestFactory.graph1(this.getClass.getSimpleName)))
          .something("h")
          .isSubjectOf(URI("http://bb"), "v")
          .select(Seq("v"))
          .orderByAsc(Seq("v","h"))
          .commit()
          .raw.map(r => {
          assert(r("results")("bindings").arr.length == 5)
          val tab = r("results")("bindings").arr.map( arrow => SparqlBuilder.createLiteral(arrow("v")).toInt)
          assert(tab.sorted == tab)
        })
      }).flatten
    }

    test("order by desc") {
      insertData.map(_ => {
        SWDiscovery(config)
          .graph(IRI(DataTestFactory.graph1(this.getClass.getSimpleName)))
          .something()
          .isSubjectOf(URI("http://bb"), "v")
          .select(Seq("v"))
          .orderByDesc("v")
          .commit()
          .raw.map(r => {
          assert(r("results")("bindings").arr.length == 5)
          val tab = r("results")("bindings").arr.map( arrow => SparqlBuilder.createLiteral(arrow("v")).toInt)
          assert(tab.sorted.reverse == tab)
        })
      }).flatten
    }

    test("order by desc with list") {
      insertData.map(_ => {
        SWDiscovery(config)
          .graph(IRI(DataTestFactory.graph1(this.getClass.getSimpleName)))
          .something("h")
          .isSubjectOf(URI("http://bb"), "v")
          .select(Seq("v"))
          .orderByDesc(Seq("v","h"))
          .commit()
          .raw.map(r => {
          assert(r("results")("bindings").arr.length == 5)
          val tab = r("results")("bindings").arr.map( arrow => SparqlBuilder.createLiteral(arrow("v")).toInt)
          assert(tab.sorted.reverse == tab)
        })
      }).flatten
    }

    test("mix order by asc/desc with list") {
      insertData.map(_ => {
        SWDiscovery(config)
          .graph(IRI(DataTestFactory.graph1(this.getClass.getSimpleName)))
          .something("h")
          .isSubjectOf(URI("http://bb"), "v")
          .select(Seq("v"))
          .orderByDesc(Seq("v"))
          .orderByAsc(Seq("h"))
          .commit()
          .raw.map(r => {
          assert(r("results")("bindings").arr.length == 5)
          val tab = r("results")("bindings").arr.map( arrow => SparqlBuilder.createLiteral(arrow("v")).toInt)
          assert(tab.sorted.reverse == tab)
        })
      }).flatten
    }

    test("order by desc with wrong variable") {
      Try(SWDiscovery(config)
        .graph(IRI(DataTestFactory.graph1(this.getClass.getSimpleName)))
        .something()
        .isSubjectOf(URI("http://bb"), "v")
        .select(Seq("v"))
        .orderByDesc("v_bad")) match {
        case Success(_) => println("Success");assert(false)
        case Failure(_) => println("Failure");assert(true)
      }
    }

  }
}
