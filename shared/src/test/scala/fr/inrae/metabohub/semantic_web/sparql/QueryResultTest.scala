package fr.inrae.metabohub.semantic_web.sparql

import fr.inrae.metabohub.semantic_web.rdf.{Literal, URI}
import utest._

import scala.util.{Failure, Success, Try}

object QueryResultTest extends TestSuite {

  def check_base(json : String) = {

    Try(QueryResult(json).json("head")("vars")) match {
      case Success(_) => assert(true)
      case Failure(_) => assert(false)
    }

    Try(QueryResult(json).json("results")("bindings")) match {
      case Success(_) => assert(true)
      case Failure(_) => assert(false)
    }
  }

  override def utestBeforeEach(path: Seq[String]): Unit = {

  }

  def tests = Tests {
    test("QueryResultTest bad def") {
      check_base("bad")
    }

    test("QueryResultTest empty def") {
      val json = """{ "head": { "vars": [ "book" , "title" ] } ,"results": { "bindings": []} }""".stripMargin
      check_base(json)

      val jsonV = QueryResult(json).json
      assert(jsonV("results")("bindings").arr.length == 0)
    }

    val json = """{ "head": { "vars": [ "book" , "title" ] } ,"results": { "bindings": [{
                 |        "book": { "type": "uri" , "value": "http://example.org/book/book6" } ,
                 |        "title": { "type": "literal" , "value": "Harry Potter and the Half-Blood Prince" }
                 |      }]} }""".stripMargin

    test("QueryResultTest one value") {
      check_base(json)
      assert(QueryResult(json).json("results")("bindings").arr.length == 1)
      assert(QueryResult(json).json("results")("bindings")(0)("book")("type") == ujson.Value("\"uri\""))
      assert(QueryResult(json).json("results")("bindings")(0)("book")("value") == ujson.Value("\"http://example.org/book/book6\""))
      assert(QueryResult(json).json("results")("bindings")(0)("title")("type") == ujson.Value("\"literal\""))
      assert(QueryResult(json).json("results")("bindings")(0)("title")("value") == ujson.Value("\"Harry Potter and the Half-Blood Prince\""))
    }

    test("QueryResultTest getValues ") {

      Try(println(QueryResult(json).getValues("badVar"))) match {
        case Success(_) => assert(false)
        case Failure(_) => assert(true)
      }

      assert(QueryResult(json).getValues("book") == Seq(URI("http://example.org/book/book6")))
      assert(QueryResult(json).getValues("title") == Seq(Literal("\"Harry Potter and the Half-Blood Prince\"")))

    }

    test("QueryResultTest setDatatype ") {
      val qr = QueryResult(json)
      qr.setDatatype("label",Map("http://example.org/book/book6" -> ujson.Value("\"Book\"")))

      Try(qr.json("results")("datatypes")) match {
        case Success(_) => assert(true)
        case Failure(_) => assert(false)
      }
      Try(qr.json("results")("datatypes")("label")) match {
        case Success(_) => assert(true)
        case Failure(_) => assert(false)
      }

      assert(qr.json("results")("datatypes")("label")("http://example.org/book/book6")(0).toString() == "\"Book\"" )
    }
  }
}
