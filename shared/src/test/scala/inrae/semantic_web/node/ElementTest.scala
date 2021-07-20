package inrae.semantic_web.node

import inrae.semantic_web.rdf._
import utest._

import scala.util.{Failure, Success, Try}

object ElementTest extends TestSuite {

  def tests = Tests {
    test("Root creation") {
        val v : Root = Root()

        assert(v.toString() != "")
        Try(v.addChildren(Root())) match {
          case Success(_) =>
          case Failure(_) => assert(false)
        }
    }

      test("Root getRdfNode") {
        val v : Root = Root()
        val n = SubjectOf("1234",new URI("test"))

        v.addChildren(n).getRdfNode("1234") match {
          case Some(v) => assert(v == n )
          case None => assert(false)
        }
      }

    test("Something creation with ID") {
      Something("1234")
    }

    test("SubjectOf creation") {
      SubjectOf("1234",URI("test"))
    }

    test("ObjectOf creation") {
      ObjectOf("1234",URI("test"))
    }

    test("LinkTo creation") {
      LinkTo("1234",URI("test"))
    }

    test("LinkFrom creation") {
      LinkFrom("1234",URI("test"))
    }

    test("Value creation with uri") {
      Value(URI("test"))
    }

    test("Value creation with Literal") {
      Value(Literal("test"))
    }

    test("ListValues creation") {
      ListValues(List(URI("test"),URI("test2"),Literal("test")))
    }

    test("Union creation") {
      UnionBlock(Something("test"),"")
    }

    test("Not creation") {
      NotBlock(Something("test"),"")
    }

    test("isBlank creation") {
      isBlank(true,"")

    }

    test("isLiteral creation") {
      isLiteral(true,"")
    }

    test("isURI creation") {
      isURI(true,"")
    }

    test("Contains creation") {
      Contains("h",true,"")
    }

    test("StrStarts creation") {
      StrStarts("",false,"")
    }

    test("StrEnds creation") {
      StrEnds("",false,"")
    }

    test("Equal creation") {
      Equal("h",true,"")
    }

    test("NotEqual creation") {
      NotEqual("h",true,"")
    }

    test("Inf creation") {
      Inf("h",true,"")
    }

    test("InfEqual creation") {
      InfEqual("h",true,"")
    }

    test("Sup creation") {
      Sup("h",true,"")
    }

    test("SupEqual creation") {
      SupEqual("h",true,"")
    }

    test("DatatypeNode creation") {
      DatatypeNode("h",SubjectOf("1234",URI("something_property")),"")

    }

    test("SourcesNode creation") {
      SourcesNode("h",List("source1","source2"),"")

    }

    test("SubStr creation") {
      SubStr(1,1,"")
    }

    test("Bind creation") {
      Bind(SubStr(1,1,""),"")
    }

    test("OrderByAsc creation") {
      OrderByAsc(Seq(),"")
    }

    test("OrderByDesc creation") {
      OrderByDesc(Seq(),"")
    }

    test("Projection creation") {
      Projection(Seq(),"")
    }

    test("reference children") {
      val p = Something("h1",
        Seq(SubjectOf("h2",URI("")),
          SubjectOf("h3",URI(""),Seq(ObjectOf("h4",URI("")))))).referencesChildren()

      assert (p == List("h1","h2","h3","h4"))
    }

    test("addDecoratingAttribute") {
      val p = Something("h1")
      val p2 = p.addDecoratingAttribute("some",List("other_some"))
      assert (p == p2)
    }

  }
}
