package inrae.semantic_web.driver

import inrae.data.DataTestFactory
import utest.{TestRunner, TestSuite, Tests, test}
import wvlet.log.{LogLevel, Logger}

import scala.concurrent.Future

object HttpRequestDriverTest extends TestSuite {
  implicit val ec: scala.concurrent.ExecutionContext = scala.concurrent.ExecutionContext.global

  val insertData : Future[Any] = DataTestFactory.insertVirtuoso1(
    """
      <aaRosHttpDriverTest> <bb> <cc> .
      """.stripMargin, this.getClass.getSimpleName)


  Logger.setDefaultLogLevel(LogLevel.OFF)


  val query : String = "select ?b ?c where { <aaRosHttpDriverTest> ?b ?c . } limit 1"

  def tests: Tests = Tests {

    test("AxiosRequestDriver get") {
      insertData.map(_ => {
        AxiosRequestDriver(idName = "test", method = "get", url = DataTestFactory.urlEndpoint, login = "", password = "", token = "", auth = "")
          .request(query)
          .map(qr => {
            assert(qr.json("results")("bindings").arr(0)("b")("value").value == "bb")
            assert(qr.json("results")("bindings").arr(0)("c")("value").value == "cc")
          })
      }).flatten
    }

    test("AxiosRequestDriver get bad request") {
      insertData.map(_ => {
        AxiosRequestDriver(idName = "test", method = "get", url = DataTestFactory.urlEndpoint, login = "", password = "", token = "", auth = "")
          .request("bad request")
          .map(_ => assert(false))
          .recover(_ => assert(true))
      }).flatten
    }
/*
    test("AxiosRequestDriver get malformed endpoint") {
      insertData.map(_ => {
        AxiosRequestDriver(idName = "test", method = "get", url = "bidon", login = "", password = "", token = "", auth = "")
          .request(query)
          .map(qr => assert(false))
          .recover(_ => assert(true))
      }).flatten
    }

    test("AxiosRequestDriver get endpoint does not exist") {
      insertData.map(_ => {
        AxiosRequestDriver(idName = "test", method = "get", url = "http://bidon.com", login = "", password = "", token = "", auth = "")
          .request(query)
          .map(qr => assert(false))
          .recover(_ => assert(true))
      }).flatten
    }
*/
    test("AxiosRequestDriver post") {
      insertData.map(_ => {
        AxiosRequestDriver(idName = "test", method = "post", url = DataTestFactory.urlEndpoint, login = "", password = "", token = "", auth = "")
          .request(query)
          .map(qr => {
            assert(qr.json("results")("bindings").arr(0)("b")("value").value == "bb")
            assert(qr.json("results")("bindings").arr(0)("c")("value").value == "cc")
          })
      }).flatten
    }

    test("AxiosRequestDriver post bad request") {
      insertData.map(_ => {
        AxiosRequestDriver(idName = "test", method = "post", url = DataTestFactory.urlEndpoint, login = "", password = "", token = "", auth = "")
          .request("bad request")
          .map(_ => assert(false))
          .recover(_ => assert(true))
      }).flatten
    }
    /*
    test("AxiosRequestDriver post malformed endpoint") {
      insertData.map(_ => {
        AxiosRequestDriver(idName = "test", method = "post", url = "bidon", login = "", password = "", token = "", auth = "")
          .request(query)
          .map(qr => assert(false))
          .recover(_ => assert(true))
      }).flatten
    }

    test("AxiosRequestDriver post endpoint does not exist") {
      insertData.map(_ => {
        AxiosRequestDriver(idName = "test", method = "post", url = "http://bidon.com", login = "", password = "", token = "", auth = "")
          .post(query)
          .map(qr => assert(false))
          .recover(_ => assert(true))
      }).flatten
    }
 */

    test("ComunicaRequestDriver metabo file") {
     /*
      ComunicaRequestDriver(idName = "test", url = "http://localhost:8080/metabo.ttl", login = "", password = "", sourceType="file")
        .request("select * where { ?a ?b ?c . } limit 5")
        .map(qr => {
          assert(qr.json("results")("bindings").arr.length == 5) })
        .recover(_ => assert(false))*/
    }

    test("ComunicaRequestDriver TTL") {
      val url_file = "http://localhost:8080/animals.ttl"
      ComunicaRequestDriver(idName = "test", url = url_file, content="",mimetype="text/turtle",login = "", password = "", sourceType="file")
        .request("select * where { ?a ?b ?c . } limit 5")
        .map(qr => {

          println(url_file + " --> " + qr.json("results")("bindings").arr.length)
          assert(qr.json("results")("bindings").arr.length == 5)
        })
        .recover(err => {
          println(err)
          assert(false)
        })

    }

    test("ComunicaRequestDriver JSON-LD") {
      val url_file = "http://localhost:8080/animals.jsonld"
      ComunicaRequestDriver(idName = "test", url = url_file, content="",mimetype="application/json+ld", login = "", password = "", sourceType="file")
        .request("select * where { ?a ?b ?c . } limit 5")
        .map(qr => {
          println(url_file + " --> " + qr.json("results")("bindings").arr.length)
          assert(qr.json("results")("bindings").arr.length == 5)
        })
        .recover(_ => {
          assert(false)
        })
    }

    test("ComunicaRequestDriver N3") {
      val url_file = "http://localhost:8080/animals.n3"
      ComunicaRequestDriver(idName = "test", url = url_file, content="",mimetype="text/n3", login = "", password = "", sourceType="file")
        .request("select * where { ?a ?b ?c . } limit 5")
        .map(qr => {
          println(url_file + " --> " + qr.json("results")("bindings").arr.length)
          assert(qr.json("results")("bindings").arr.length == 5)
        })
        .recover(_ => {
          assert(false)
        })
    }

  }


  TestRunner.runAsync(tests).map { _ => DataTestFactory.deleteVirtuoso1(this.getClass.getSimpleName) }
}
