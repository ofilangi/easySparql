package fr.inrae.metabohub.semantic_web.node.pm
import fr.inrae.metabohub.semantic_web.node.{Root, SubjectOf}
import fr.inrae.metabohub.semantic_web.rdf.URI
import utest.{TestSuite, Tests, test}
object RemoveNodeTest extends TestSuite {

  def tests = Tests {
    test("remove root") {
      assert(RemoveNode.run(Root("test"),"test").idRef == Root("test").idRef)
    }

    test("one child") {
      val tree = Root("test").addChildren(SubjectOf("t1",URI("http://test")))
      assert(RemoveNode.run(tree,"t1") == Root("test"))
    }

    test("one child") {
      val tree = Root("test").addChildren(SubjectOf("t1",URI("http://test")))
      assert(RemoveNode.run(tree,"test") == Root("test"))
    }

    test("one child") {
      val tree1 = Root("test")
         .addChildren(SubjectOf("t1",URI("http://test")))
         .addChildren(SubjectOf("t2",URI("http://test2")))

      val tree2 = Root("test")
        .addChildren(SubjectOf("t1",URI("http://test")))

      val tree3 = Root("test")
        .addChildren(SubjectOf("t2",URI("http://test2")))

      assert(RemoveNode.run(tree1,"test") == Root("test"))
      assert(RemoveNode.run(tree1,"t1") == tree3)
      assert(RemoveNode.run(tree1,"t2") == tree2)
    }

  }
}
