package inrae.semantic_web.node.pm
import inrae.data.ApplyAllNode
import utest.{TestSuite, Tests, test}
object SimpleConsoleTest extends TestSuite {

  def all(consoleColor : Boolean = true,displayRootStyle : Boolean = true) = {
    ApplyAllNode.listNodes.map( n => (SimpleConsole(consoleColor,displayRootStyle).get(n)))
  }

  def tests = Tests {
    test("console 1") {
      all(false,false)
    }
    test("console 2") {
      all(true,false)
    }
    test("console 3") {
      all(false,true)
    }
    test("console 4") {
      all(true,true)
    }
  }
}
