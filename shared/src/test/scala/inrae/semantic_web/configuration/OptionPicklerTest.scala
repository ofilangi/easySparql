package inrae.semantic_web.configuration

import utest.{TestSuite, Tests, test}

object OptionPicklerTest extends TestSuite {
  def tests: Tests = Tests {
    test("Case None => string 'null' ") {
      assert(OptionPickler.write(None) == "null")
    }
    test("string 'null' => None ") {
      val s : Option[String] = OptionPickler.read[Option[String]]("null")
      assert(s.isEmpty)
    }
    test("string 'some' => None ") {
      val s : Option[String] = OptionPickler.read[Option[String]](ujson.Str("some"))
      assert(s.contains("some"))
    }
  }
}