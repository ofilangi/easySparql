package inrae.semantic_web.configuration

import inrae.semantic_web.configuration.SourcePath.SourcePath
import utest.{TestSuite, Tests, test}

import scala.util.{Failure, Success, Try}

object SourcePathTest extends TestSuite {
  def tests: Tests = Tests {

    test("write SourcePath.LocalFile") {
      assert(OptionPickler.write(SourcePath.LocalFile) == "\"LocalFile\"")
    }

    test("SourcePath.LocalFile") {
      assert(OptionPickler.read[SourcePath](ujson.Str("LocalFile")) == SourcePath.LocalFile)
    }

    test("SourcePath.Content") {
      assert(OptionPickler.read[SourcePath](ujson.Str("Content")) == SourcePath.Content)
    }

    test("SourcePath.UrlPath") {
      assert(OptionPickler.read[SourcePath](ujson.Str("UrlPath")) == SourcePath.UrlPath)
    }

    test("bad SourcePath definition") {
      Try(OptionPickler.read[SourcePath](ujson.Str("some"))) match {
        case Success(_) => assert(false)
        case Failure(_) => assert(true)
      }
    }
  }
}