package inrae.semantic_web

import inrae.semantic_web.SWDiscoveryTest.config
import inrae.semantic_web.configuration._
import utest.{TestSuite, Tests, test}

object SWTransactionTest extends TestSuite {
  def tests = Tests {
    test("console") {
      SWTransaction(SWDiscovery(config).something("h1")).console
    }


  }

}
