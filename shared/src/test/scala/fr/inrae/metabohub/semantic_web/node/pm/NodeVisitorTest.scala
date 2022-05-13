package fr.inrae.metabohub.semantic_web.node.pm
import fr.inrae.metabohub.semantic_web.node.{RdfNode, Something}
import utest.{TestSuite, Tests, test}


object NodeVisitorTest extends TestSuite {

  def tests = Tests {
    test("getNodeWithRef") {
      val listNodes : Array[RdfNode] = NodeVisitor.getNodeWithRef("test",Something("test"))
      assert(listNodes.length==1)
      assert(listNodes(0).idRef == "test")
    }
    test("getNodeWithRef empty") {
      val listNodes : Array[RdfNode] = NodeVisitor.getNodeWithRef("test",Something("autre"))
      assert(listNodes.length==0)
    }

    test("getNodeWithRef deep") {
      val listNodes : Array[RdfNode] = NodeVisitor.getNodeWithRef("test",Something("autre",List(Something("test"))))
      assert(listNodes.length==1)
    }

    test("getAncestorsRef - basic") {
      assert(NodeVisitor.getAncestorsRef("h1",Something("h1")) == Seq("h1"))
    }

    test("getAncestorsRef - basic 2") {
      assert(NodeVisitor.getAncestorsRef("h2",Something("h1")) == Seq())
    }

    def checkOneBranch(n : RdfNode) = {
      assert(NodeVisitor.getAncestorsRef("none",n) == Seq())
      assert(NodeVisitor.getAncestorsRef("s1",n) == Seq("s1"))
      assert(NodeVisitor.getAncestorsRef("s2",n) == Seq("s1","s2"))
      assert(NodeVisitor.getAncestorsRef("s3",n) == Seq("s1","s2","s3"))
    }

    test("getAncestorsRef - test one branch tree") {
      val n : RdfNode = Something("s1",List(Something("s2",List(Something("s3")))))
      checkOneBranch(n)
    }

    def checkSecondBranch(n : RdfNode) = {
      assert(NodeVisitor.getAncestorsRef("s1",n) == Seq("s1"))
      assert(NodeVisitor.getAncestorsRef("d2",n) == Seq("s1","d2"))
      assert(NodeVisitor.getAncestorsRef("d3",n) == Seq("s1","d2","d3"))
    }

    test("getAncestorsRef - test several branch tree") {
      val n : RdfNode = Something("s1",
        List(Something("s2",List(Something("s3"))),Something("d2",List(Something("d3")))))

      checkOneBranch(n)
      checkSecondBranch(n)
    }



    test("browse") {
      assert(NodeVisitor.map(Something("test"),0,  (n,p ) => {
        assert(n.idRef == "test")
        assert(p == 0)
        n.idRef->p
      }) == List( "test" -> 0 ) )
    }

    test("browse deep") {
      assert(NodeVisitor.map(Something("autre",List(Something("test"))), 0, (n,p) => {
        n.idRef->p
      }) == List( "autre" -> 0 , "test" -> 1 ) )
    }
  }
}
