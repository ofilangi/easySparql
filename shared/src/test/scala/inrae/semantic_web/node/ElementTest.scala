package inrae.semantic_web.node

import inrae.data.ApplyAllNode
import inrae.semantic_web.rdf._
import upickle.default.{read, write}
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

    test("ListValues creation") {
      ListValues(List(URI("test"),URI("test2"),Literal("test")))
    }

    test("reference children") {
      val p = Something("h1",
        Seq(SubjectOf("h2",URI("")),
          SubjectOf("h3",URI(""),Seq(ObjectOf("h4",URI("")))))).referencesChildren()

      assert (p == List("h1","h2","h3","h4"))
    }

    test("addDecoratingAttribute") {
      val p = Something("h1")
      val p2 = p.addDecoratingAttribute("some",List("other_some").toString)
      assert (p != p2)
    }

    test("all") {
      ApplyAllNode.listNodes.map(n => {
        write(n)
      }).map( n => read[Node](n) )
    }
  }
}
