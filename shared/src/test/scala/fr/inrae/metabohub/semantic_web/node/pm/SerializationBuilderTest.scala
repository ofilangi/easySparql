package fr.inrae.metabohub.semantic_web.node.pm

import fr.inrae.metabohub.data.{ApplyAllNode, DataTestFactory}
import fr.inrae.metabohub.semantic_web.{SWDiscovery, SWTransaction}
import fr.inrae.metabohub.semantic_web.node.Root
import fr.inrae.metabohub.semantic_web.rdf.{Literal, QueryVariable, URI}
import upickle.default.{read, write}
import utest.{TestSuite, Tests, test}


object SerializationBuilderTest extends TestSuite  {
  def tests: Tests = Tests {
    test("serialization basic 1") {
      val sw = SWDiscovery( DataTestFactory.getConfigVirtuoso1(),Root(),Some("test"))
      assert(SWDiscovery().setSerializedString(sw.getSerializedString) == sw)
      val swt : SWTransaction = sw.select()
      assert(SWTransaction().setSerializedString(swt.getSerializedString) == swt)
    }

    test("serialization basic 2") {
      val sw = SWDiscovery( DataTestFactory.getConfigVirtuoso1())
      assert(SWDiscovery().setSerializedString(sw.getSerializedString) == sw)
      val swt : SWTransaction = sw.select()
      assert(SWTransaction().setSerializedString(swt.getSerializedString) == swt)
    }

    test("serialization Something") {
      val sw =
        SWDiscovery( DataTestFactory.getConfigVirtuoso1())
          .something ("h1" )

      assert(SWDiscovery().setSerializedString(sw.getSerializedString) == sw)
      val swt : SWTransaction = sw.select()
      assert(SWTransaction().setSerializedString(swt.getSerializedString) == swt)
    }

    test("serialization SubjectOf") {
      val sw =
        SWDiscovery( DataTestFactory.getConfigVirtuoso1())
          .something ("h1" )
          .isSubjectOf(URI("bb"))

      assert(SWDiscovery().setSerializedString(sw.getSerializedString) == sw)
      val swt : SWTransaction = sw.select()
      assert(SWTransaction().setSerializedString(swt.getSerializedString) == swt)
    }

    test("serialization ObjectOf") {
      val sw =
        SWDiscovery( DataTestFactory.getConfigVirtuoso1())
          .something ("h1" )
          .isObjectOf(URI("bb"))

      assert(SWDiscovery().setSerializedString(sw.getSerializedString) == sw)
      val swt : SWTransaction = sw.select()
      assert(SWTransaction().setSerializedString(swt.getSerializedString) == swt)
    }

    test("serialization LinkTo") {
      val sw =
        SWDiscovery( DataTestFactory.getConfigVirtuoso1())
          .something ("h1" )
          .isLinkTo(URI("bb"))

      assert(SWDiscovery().setSerializedString(sw.getSerializedString) == sw)
      val swt : SWTransaction = sw.select()
      assert(SWTransaction().setSerializedString(swt.getSerializedString) == swt)
    }

    test("serialization LinkFrom") {
      val sw =
        SWDiscovery( DataTestFactory.getConfigVirtuoso1())
          .something ("h1" )
          .isLinkFrom(QueryVariable("bb"))

      assert(SWDiscovery().setSerializedString(sw.getSerializedString) == sw)
      val swt : SWTransaction = sw.select()
      assert(SWTransaction().setSerializedString(swt.getSerializedString) == swt)
    }

    test("serialization Value QueryVariable") {
      val sw =
        SWDiscovery( DataTestFactory.getConfigVirtuoso1())
          .something ("h1" )
          .set(QueryVariable("bb"))

      assert(SWDiscovery().setSerializedString(sw.getSerializedString) == sw)
      val swt : SWTransaction = sw.select()
      assert(SWTransaction().setSerializedString(swt.getSerializedString) == swt)
    }

    test("serialization Value URI") {
      val sw =
        SWDiscovery( DataTestFactory.getConfigVirtuoso1())
          .something ("h1" )
          .set(URI("bb"))

      assert(SWDiscovery().setSerializedString(sw.getSerializedString) == sw)
      val swt : SWTransaction = sw.select()
      assert(SWTransaction().setSerializedString(swt.getSerializedString) == swt)
    }

    test("serialization Literal URI") {
      val sw =
        SWDiscovery( DataTestFactory.getConfigVirtuoso1())
          .something ("h1" )
          .set(Literal("bb"))

      assert(SWDiscovery().setSerializedString(sw.getSerializedString) == sw)
      val swt : SWTransaction = sw.select()
      assert(SWTransaction().setSerializedString(swt.getSerializedString) == swt)
    }

    test("serialization ListValue URI") {
      val sw =
        SWDiscovery( DataTestFactory.getConfigVirtuoso1())
          .something ("h1" )
          .setList(Seq(URI("bb")))

      assert(SWDiscovery().setSerializedString(sw.getSerializedString) == sw)
      val swt : SWTransaction = sw.select()
      assert(SWTransaction().setSerializedString(swt.getSerializedString) == swt)
    }

    test("serialization graph") {
      val sw =
        SWDiscovery( DataTestFactory.getConfigVirtuoso1())
          .graph("h")
          .something ("h1" )
          .setList(Seq(URI("bb")))

      assert(SWDiscovery().setSerializedString(sw.getSerializedString) == sw)
      val swt : SWTransaction = sw.select()
      assert(SWTransaction().setSerializedString(swt.getSerializedString) == swt)
    }

    test("serialization filter") {
      val sw =
        SWDiscovery( DataTestFactory.getConfigVirtuoso1())
          .something ("h1" )
          .isLinkTo(URI("bb"))
          .filter.not.contains("filter")

      assert(SWDiscovery().setSerializedString(sw.getSerializedString) == sw)
      val swt : SWTransaction = sw.select()
      assert(SWTransaction().setSerializedString(swt.getSerializedString) == swt)
    }

    test("serialization datatype") {
      val sw =
        SWDiscovery(DataTestFactory.getConfigVirtuoso1())
          .something ("h1" )
          .isLinkTo(URI("bb"))
          .datatype(URI("some"),"v")

      assert(SWDiscovery().setSerializedString(sw.getSerializedString) == sw)
      val swt : SWTransaction = sw.select()
      assert(SWTransaction().setSerializedString(swt.getSerializedString) == swt)
    }
  }
}
