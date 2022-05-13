package fr.inrae.metabohub.semantic_web

import fr.inrae.metabohub.semantic_web.SWDiscoveryTest.config
import utest.{TestSuite, Tests, test}

import scala.util.Try

object SWTransactionTest extends TestSuite {
  def tests: Tests = Tests {
    test("console") {
      assert(Try(SWTransaction(SWDiscovery(config).something("h1")).console).isSuccess)
    }

    test("commit - failure because no selection") {
      assert(Try(SWTransaction(SWDiscovery(config).something("h1")).commit()).isFailure)
    }

    test("commit - failure because no selection") {
      assert(Try(SWDiscovery(config).something("h1").select(List("h1")).commit()).isSuccess)
    }

    test("commit - empty projection") {
      assert(Try(SWDiscovery(config).something("h1").select(List()).commit().projection).isSuccess)
    }

    test("commit - empty projection") {
      assert(Try(SWDiscovery(config).something("h1").select(List()).commit().projection(List())).isSuccess)
    }

  }

}
