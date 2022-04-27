package inrae.semantic_web.rdf

import inrae.semantic_web.configuration.OptionPickler
import utest._

object SparqlDefinitionTest extends TestSuite {

  def tests: Tests = Tests {
    test("URI object with localename/namespace") {
      val value: URI = URI("local","namespace")

      assert( value.toString == "namespace:local")
      assert( value.sparql == "namespace:local")
      assert( value.naiveLabel == "local")
    }

    test("URI object with uri form") {
      val value: URI = URI("http://test.org/namespace")
      assert( value.toString == "<http://test.org/namespace>")
      assert( value.sparql == "<http://test.org/namespace>")
      assert( value.naiveLabel == "namespace")
    }

    test("URI namespace:object") {
      val value: URI = URI("namespace:obj")
      assert( value.toString == "namespace:obj")
      assert( value.sparql == "namespace:obj")
      assert( value.naiveLabel == "obj")
    }

    test("IRI") {
      val value : IRI = IRI("http://test.org/namespace")
      assert( value.toString == "<http://test.org/namespace>")
      assert( value.sparql == "<http://test.org/namespace>")
      assert( value.naiveLabel == "namespace")
    }

    test("IRI implicit") {
      val v : IRI = "http://test.org/namespace"
      assert(v == IRI("http://test.org/namespace"))
    }

    test("Anonymous") {
      val value : Anonymous = Anonymous("something")
      assert( value.toString == "something")
      assert( value.sparql == "something")
      assert( value.naiveLabel == "Anonymous[something]")
    }

    test("PropertyPath") {
      val value : PropertyPath = PropertyPath("something*/something2+")
      assert( value.toString == "something*/something2+")
      assert( value.sparql == "something*/something2+")
      assert( value.naiveLabel == "PropertyPath[something*/something2+]")
    }

    test("PropertyPath implicit") {
      val v : PropertyPath = "http://test.org/namespace"
      assert(v == PropertyPath("http://test.org/namespace"))
    }

    test("Literal default") {
      val value : Literal[String] = Literal("test")
      assert( value.toString == "\"test\"")
      assert( value.sparql == "\"test\"")
      assert( value.naiveLabel == "test")
    }

    test("Literal xsd:string") {
      val value : Literal[String] = Literal("test","xsd:string")
      assert( value.toString == "\"test\"^^xsd:string")
      assert( value.sparql == "\"test\"^^xsd:string")
      assert( value.naiveLabel == "test")
    }

    test("Literal <something> type") {
      val value : Literal[String] = Literal("test","<something>")
      assert( value.toString() == "\"test\"^^<something>")
      assert( value.sparql == "\"test\"^^<something>")
      assert( value.naiveLabel == "test")
    }

    test("Literal xsd:string and tag") {
      val value : Literal[String] = Literal("test","xsd:string", "fr")
      assert( value.toString == "\"test\"@fr")
      assert( value.sparql == "\"test\"@fr")
      assert( value.naiveLabel == "test")
    }
/*
problem with js generation and round
    test("Literal implicit float") {
      val value : Literal[Float] = 0.3f
      assert( value.toString == "0.3")
      assert( value.sparql == "0.3")
      assert( value.naiveLabel == "0.3")
      assert( value.toFloat == 0.3f)
    }
*/
    test("Literal implicit double") {
      val value : Literal[Double] = 0.5
      assert( value.toString == "0.5")
      assert( value.sparql == "0.5")
      assert( value.naiveLabel == "0.5")
      assert( value.toDouble == 0.5)
    }

    test("Literal implicit int") {
      val value : Literal[Int] = 2
      assert( value.toString == "2")
      assert( value.sparql == "2")
      assert( value.naiveLabel == "2")
      assert( value.toInt == 2)
    }
    test("Literal implicit bool") {
      val value : Literal[Boolean] = true
      assert( value.toString == "true")
      assert( value.sparql == "true")
      assert( value.naiveLabel == "true")
      assert( value.toBoolean )
    }

    test("createUri") {
      val value = SparqlBuilder.createUri(ujson.Value("{ \"value\": \"test\"}"))
      assert( value.toString == "<test>")
      assert( value.sparql == "<test>")
      assert( value.naiveLabel == "test")
    }

    test("createLiteral") {
      val value = SparqlBuilder.createLiteral(ujson.Value("{ \"value\": \"test\" }"))
      assert( value.toString == "\"test\"")
      assert( value.sparql == "\"test\"")
      assert( value.naiveLabel == "test")
    }

    test("createLiteral") {
      val value = SparqlBuilder.createLiteral(ujson.Value("{ \"value\": \"test\" , \"datatype\" : \"\" }"))
      assert( value.toString == "\"test\"")
      assert( value.sparql == "\"test\"")
      assert( value.naiveLabel == "test")
    }

    test("createLiteral tag") {
      val v = ujson.Value("{ \"value\": \"test\" , \"datatype\" : \"\"  , \"tag\":\"fr\"}")
      val value = SparqlBuilder.createLiteral(v)
      assert( value.toString == "\"test\"@fr")
      assert( value.sparql == "\"test\"@fr")
      assert( value.naiveLabel == "test")
    }

    test("create uri") {
      val v = ujson.Value("{  \"type\" : \"uri\" ,\"value\": \"test\"}")
      val value = SparqlBuilder.create(v)
      assert( value.toString == "<test>")
      assert( value.sparql == "<test>")
      assert( value.naiveLabel == "test")
    }

    test("create literal") {
      val v = ujson.Value("{ \"type\" : \"literal\" , \"value\": \"test\" , \"datatype\" : \"\"  , \"tag\":\"fr\"}")
      val value = SparqlBuilder.create(v)
      assert( value.toString == "\"test\"@fr")
      assert( value.sparql == "\"test\"@fr")
      assert( value.naiveLabel == "test")
    }

    test("create literal") {
      val v = ujson.Value("{ \"type\" : \"typed-literal\" , \"value\": \"test\" , \"datatype\" : \"\"  , \"tag\":\"fr\"}")
      val value = SparqlBuilder.create(v)
      assert( value.toString == "\"test\"@fr")
      assert( value.sparql == "\"test\"@fr")
      assert( value.naiveLabel == "test")
    }

    test("any int") {
      val v : Int = 5
      assert(SparqlDefinition.fromAny(v.asInstanceOf[Any]) == Literal(v))
    }

    test("any double") {
      val v : Double = 5.5
      val v2 = SparqlDefinition.fromAny(v.asInstanceOf[Any])
      assert(v2 == Literal(v))
      /*
      val s : String = write(v2)
      val v3 = read[QueryVariable](s)
      assert(v2 == v3)*/
    }

    test("any boolean") {
      val v : Boolean = false
      assert(SparqlDefinition.fromAny(v.asInstanceOf[Any]) == Literal(v))
    }

    test("any uri 1") {
      val v : String = "http://test"
      assert(SparqlDefinition.fromAny(v.asInstanceOf[Any]) == URI(v))
    }

    test("any uri 2") {
      val v : String = "http:test"
      assert(SparqlDefinition.fromAny(v.asInstanceOf[Any]) == URI(v))
    }

    test("any string ") {
      val v : String = "tt p:test  http : http:// "
      assert(SparqlDefinition.fromAny(v.asInstanceOf[Any]) == Literal(v))
    }

    test("any string") {
      val v : String = "aaaa"
      assert(SparqlDefinition.fromAny(v.asInstanceOf[Any]) == Literal(v))
    }

    test("any queryvariable") {
      val v : String = "?aaaa"
      val v2 = SparqlDefinition.fromAny(v.asInstanceOf[Any])
      assert(v2 == QueryVariable(v))
      val s : String = OptionPickler.write(v2)
      val v3 = OptionPickler.read[QueryVariable](s)
      assert(v2 == v3)
    }




  }
}
