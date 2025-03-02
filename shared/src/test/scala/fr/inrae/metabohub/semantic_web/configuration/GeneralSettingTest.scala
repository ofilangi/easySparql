package fr.inrae.metabohub.semantic_web.configuration

import utest.{TestSuite, Tests, test}

object GeneralSettingTest extends TestSuite {
  def tests: Tests = Tests {
    test("GeneralSetting default") {
      GeneralSetting()
    }

    test("cache true") {
      GeneralSetting(cache=true)
    }

    test("cache false") {
      GeneralSetting(cache=false)
    }

    test("cache logLevel") {
      GeneralSetting(logLevel="some")
    }
  }
}
