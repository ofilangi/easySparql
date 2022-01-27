package inrae.semantic_web

import inrae.semantic_web.node.Root
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

