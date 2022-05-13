package fr.inrae.metabohub.semantic_web

import fr.inrae.metabohub.semantic_web.node.Root
import fr.inrae.metabohub.semantic_web.configuration._
import utest.{TestSuite, Tests, test}

object SparqlQueryBuilderTest extends TestSuite {

  def tests = Tests {
    test("baseQuery empty Root") {
      assert(SparqlQueryBuilder.baseQuery(Root()).nonEmpty)
    }
    test("selectQueryString empty Root") {
      assert(SparqlQueryBuilder.selectQueryString(Root()).nonEmpty)
    }

    test("selectQueryString Root directive") {
      assert(SparqlQueryBuilder.selectQueryString(Root(directives = Seq("test"))).contains("test"))
    }

  }
}

