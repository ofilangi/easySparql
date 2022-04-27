package inrae.semantic_web.node.pm

import inrae.data.ApplyAllNode
import inrae.semantic_web.node._
import inrae.semantic_web.rdf._
import utest.{TestSuite, Tests, assert, test}

object SparqlGeneratorTest extends TestSuite {
  def tests: Tests = Tests {

    test("prefixes") {
      val m : Map[String,IRI] = Map("some"-> "http://something","some2"->"http://something2")
      assert(SparqlGenerator.prefixes(m).toLowerCase().contains("prefix"))
      assert(SparqlGenerator.prefixes(m).toLowerCase().contains("some:"))
      assert(SparqlGenerator.prefixes(m).toLowerCase().contains("some2:"))
      assert(SparqlGenerator.prefixes(m).toLowerCase().contains("http://something"))
      assert(SparqlGenerator.prefixes(m).toLowerCase().contains("http://something2"))
    }

    test("from") {
      val l : Seq[IRI] = List("http://something","http://something2")
      assert(SparqlGenerator.from(l).toLowerCase().contains("from"))
    }

    test("fromNamed") {
      val l : Seq[IRI] = List("http://something","http://something2")
      assert(SparqlGenerator.fromNamed(l).toLowerCase().contains("from named"))
    }
/*
    test("Sparql Prolog - Variable list empty") {
      val v = SparqlGenerator.queryFormSelect(Seq[String]()).toLowerCase()
      assert(v.contains("*"))//assert(SparqlGenerator.prolog(Seq[String]().contains("*")) == "SELECT * WHERE {")
      assert(v.contains("select"))
    }

    test("Sparql Prolog - One Variable ") {
      val v = SparqlGenerator.queryFormSelect(Seq[String]("test")).toLowerCase()
      assert(v.contains("?test"))
      assert(v.contains("select"))
    }

    test("Sparql Prolog - Two Variables ") {
      val v = SparqlGenerator.queryFormSelect(Seq[String]("test","test2")).toLowerCase()
      assert(v.contains("?test"))
      assert(v.contains("?test2"))
      assert(v.contains("select"))
    }

    test("start_where") {
      assert(SparqlGenerator.start_where.toLowerCase().contains("where"))
    }

    test("end_where") {
      assert(SparqlGenerator.start_where.toLowerCase().contains("}"))
    }

    test("solutionModifier") {
      assert(SparqlGenerator.solutionModifier(0,0).contains("}"))
      assert(!SparqlGenerator.solutionModifier(0,0).toLowerCase().contains("limit"))
      assert(!SparqlGenerator.solutionModifier(0,0).toLowerCase().contains("offset"))
    }

    test("solutionModifier limit") {
      assert(SparqlGenerator.solutionModifier(10,0).contains("}"))
      assert(SparqlGenerator.solutionModifier(10,0).toLowerCase().contains("limit 10"))
      assert(!SparqlGenerator.solutionModifier(10,0).toLowerCase().contains("offset"))
    }

    test("solutionModifier offset") {
      assert(SparqlGenerator.solutionModifier(0,10).contains("}"))
      assert(!SparqlGenerator.solutionModifier(0,10).toLowerCase().contains("limit"))
      assert(SparqlGenerator.solutionModifier(0,10).toLowerCase().contains("offset 10"))
    }
*/
    test("prologCountSelection") {
      assert(SparqlGenerator.prologCountSelection("myvar").toLowerCase().contains("count"))
      assert(SparqlGenerator.prologCountSelection("myvar").toLowerCase().contains("myvar"))
    }

    test("all") {
      ApplyAllNode.listNodes.map(n => {
        SparqlGenerator.sparqlNode(n,"varSire","varId")
      })
    }

    test("sparqlNode - SubjectOf") {
      val v = SparqlGenerator.sparqlNode(SubjectOf("id",URI("http://test")),"varSire","varId")
      assert(v.contains("?varSire <http://test> ?varId"))
    }

    test("sparqlNode Something") {
      val v = SparqlGenerator.sparqlNode(Something("1234"),"nothingSire","nothingVar")
      assert(v.toLowerCase() != "")
    }
    test("sparqlNode Something") {
      val v = SparqlGenerator.sparqlNode(Something("1234", List(SubjectOf("test",URI("http://test")))),"nothingSire","nothingVar")
      assert(v.toLowerCase() == "")
    }

    test("sparqlNode SubjectOf") {
      val v = SparqlGenerator.sparqlNode(SubjectOf("1234",URI("test")),"nothingSire","nothingVar")
      assert(v.trim().split(" ").toList == List("?nothingSire","<test>","?nothingVar","."))
    }

    test("sparqlNode ObjectOf") {
      val v = SparqlGenerator.sparqlNode(ObjectOf("1234",URI("test")),"nothingSire","nothingVar")
      assert(v.trim().split(" ").toList == List("?nothingVar","<test>","?nothingSire","."))
    }

    test("sparqlNode LinkTo") {
      val v = SparqlGenerator.sparqlNode(LinkTo("1234",URI("test")),"nothingSire","nothingVar")
      assert(v.trim().split(" ").toList == List("?nothingSire","?nothingVar","<test>","."))
    }

    test("sparqlNode LinkFrom") {
      val v = SparqlGenerator.sparqlNode(LinkFrom("1234",URI("test")),"nothingSire","nothingVar")
      assert(v.trim().split(" ").toList == List("<test>","?nothingVar","?nothingSire","."))
    }

    test("sparqlNode Value") {
      val v = SparqlGenerator.sparqlNode(Value(URI("test")),"nothingSire","nothingVar")
      assert(v.trim().split(" ").toList == List("VALUES","?nothingSire","{","<test>","}","."))
    }

    test("sparqlNode ListValues") {
      val v = SparqlGenerator.sparqlNode(ListValues(List(URI("test"),URI("test2"))),"nothingSire","nothingVar")
      assert(v.trim().split(" ").toList == List("VALUES","?nothingSire","{","<test>","<test2>","}","."))
    }

    test("sparqlNode isBlank neg") {
      val v = SparqlGenerator.sparqlNode(isBlank(negation = true,""),"nothingSire","nothingVar")
      assert(v.trim().split(" ").toList == List("FILTER","(","!isBlank(?nothingSire)",")"))
    }

    test("sparqlNode isBlank") {
      val v = SparqlGenerator.sparqlNode(isBlank(negation = false,""),"nothingSire","nothingVar")
      assert(v.trim().split(" ").toList == List("FILTER","(","isBlank(?nothingSire)",")"))
    }

    test("sparqlNode isLiteral neg") {
      val v = SparqlGenerator.sparqlNode(isLiteral(negation = true,""),"nothingSire","nothingVar")
      assert(v.trim().split(" ").toList == List("FILTER","(","!isLiteral(?nothingSire)",")"))
    }

    test("sparqlNode isLiteral") {
      val v = SparqlGenerator.sparqlNode(isLiteral(negation = false,""),"nothingSire","nothingVar")
      assert(v.trim().split(" ").toList == List("FILTER","(","isLiteral(?nothingSire)",")"))
    }


    test("sparqlNode isURI") {
      val v = SparqlGenerator.sparqlNode(isURI(negation = false,""),"nothingSire","nothingVar")
      assert(v.trim().split(" ").toList == List("FILTER","(","isURI(?nothingSire)",")"))
    }

    test("sparqlNode isURI neg") {
      val v = SparqlGenerator.sparqlNode(isURI(negation = true,""),"nothingSire","nothingVar")
      assert(v.trim().split(" ").toList == List("FILTER","(","!isURI(?nothingSire)",")"))
    }

    test("sparqlNode Contains") {

      val v = SparqlGenerator.sparqlNode(Contains("h",negation = false,""),"nothingSire","nothingVar")
      assert(v.trim().split(" ").toList == List("FILTER","(","contains(str(?nothingSire),\"h\")",")"))
    }

    test("sparqlNode Contains neg") {
      val v = SparqlGenerator.sparqlNode(Contains("h",negation = true,""),"nothingSire","nothingVar")
      assert(v.trim().split(" ").toList == List("FILTER","(","!contains(str(?nothingSire),\"h\")",")"))
    }

    test("sparqlNode StrStarts") {

      val v = SparqlGenerator.sparqlNode(StrStarts("h",negation = false,""),"nothingSire","nothingVar")
      assert(v.trim().split(" ").toList == List("FILTER","(","strStarts(str(?nothingSire),\"h\")",")"))
    }

    test("sparqlNode StrEnds") {

      val v = SparqlGenerator.sparqlNode(StrEnds("h",negation = false,""),"nothingSire","nothingVar")
      assert(v.trim().split(" ").toList == List("FILTER","(","strEnds(str(?nothingSire),\"h\")",")"))
    }

    test("sparqlNode Equal") {
      val v = SparqlGenerator.sparqlNode(Equal("h",negation = false,""),"nothingSire","nothingVar")
      assert(v.trim().split(" ").toList == List("FILTER","(","(?nothingSire=\"h\")",")"))
    }

    test("sparqlNode Equal neg") {
      val v = SparqlGenerator.sparqlNode(Equal("h",negation = true,""),"nothingSire","nothingVar")
      assert(v.trim().split(" ").toList == List("FILTER","(","!(?nothingSire=\"h\")",")"))
    }

    test("sparqlNode NotEqual") {
      val v = SparqlGenerator.sparqlNode(NotEqual("h",negation = false,""),"nothingSire","nothingVar")
      assert(v.trim().split(" ").toList == List("FILTER","(","(?nothingSire!=\"h\")",")"))
    }

    test("sparqlNode NotEqual neg") {
      val v = SparqlGenerator.sparqlNode(NotEqual("h",negation = true,""),"nothingSire","nothingVar")
      assert(v.trim().split(" ").toList == List("FILTER","(","!(?nothingSire!=\"h\")",")"))
    }

    test("sparqlNode Inf") {
      val v = SparqlGenerator.sparqlNode(Inf(Literal("0.5","xsd:double"),negation = false,""),"nothingSire","nothingVar")
      assert(v.trim().split(" ").toList == List("FILTER","(","(?nothingSire<\"0.5\"^^xsd:double)",")"))
    }

    test("sparqlNode Inf") {
      val v = SparqlGenerator.sparqlNode(Inf(0.5,negation = false,""),"nothingSire","nothingVar")
      assert(v.trim().split(" ").toList == List("FILTER","(","(?nothingSire<0.5)",")"))
    }

    test("sparqlNode Inf with Literal without type ") {
      val v = SparqlGenerator.sparqlNode(Inf(Literal("0.5"),negation = false,""),"nothingSire","nothingVar")
      assert(v.trim().split(" ").toList == List("FILTER","(","(?nothingSire<\"0.5\")",")"))
    }

    test("sparqlNode InfEqual neg") {
      val v = SparqlGenerator.sparqlNode(Inf(Literal("0.5","xsd:double"),negation = true,""),"nothingSire","nothingVar")
      assert(v.trim().split(" ").toList == List("FILTER","(","!(?nothingSire<\"0.5\"^^xsd:double)",")"))
    }

    test("sparqlNode InfEqual neg") {
      val v = SparqlGenerator.sparqlNode(Inf(0.5,negation = true,""),"nothingSire","nothingVar")
      assert(v.trim().split(" ").toList == List("FILTER","" + "(","!(?nothingSire<0.5)",")"))
    }

    test("sparqlNode InfEqual") {
      val v = SparqlGenerator.sparqlNode(InfEqual(Literal("0.5","xsd:double"),negation = false,""),"nothingSire","nothingVar")
      assert(v.trim().split(" ").toList == List("FILTER","(","(?nothingSire<=\"0.5\"^^xsd:double)",")"))
    }

    test("sparqlNode InfEqual") {
      val v = SparqlGenerator.sparqlNode(InfEqual(0.5,negation = false,""),"nothingSire","nothingVar")
      assert(v.trim().split(" ").toList == List("FILTER","(","(?nothingSire<=0.5)",")"))
    }

    test("sparqlNode InfEqual neg") {
      val v = SparqlGenerator.sparqlNode(InfEqual(Literal("0.5","xsd:double"),negation = true,""),"nothingSire","nothingVar")
      assert(v.trim().split(" ").toList == List("FILTER","(","!(?nothingSire<=\"0.5\"^^xsd:double)",")"))
    }

    test("sparqlNode InfEqual neg") {
      val v = SparqlGenerator.sparqlNode(InfEqual(Literal("0.5","xsd:double"),negation = true,""),"nothingSire","nothingVar")
      assert(v.trim().split(" ").toList == List("FILTER","(","!(?nothingSire<=\"0.5\"^^xsd:double)",")"))
    }

    test("sparqlNode Sup") {
      val v = SparqlGenerator.sparqlNode(Sup(0.5,negation = false,""),"nothingSire","nothingVar")
      assert(v.trim().split(" ").toList == List("FILTER","(","(?nothingSire>0.5)",")"))
    }

    test("sparqlNode Sup neg") {
      val v = SparqlGenerator.sparqlNode(Sup(Literal("0.5","xsd:double"),negation = true,""),"nothingSire","nothingVar")
      assert(v.trim().split(" ").toList == List("FILTER","(","!(?nothingSire>\"0.5\"^^xsd:double)",")"))
    }

    test("sparqlNode SupEqual") {
      val v = SparqlGenerator.sparqlNode(SupEqual(Literal("0.5","xsd:double"),negation = false,""),"nothingSire","nothingVar")
      assert(v.trim().split(" ").toList == List("FILTER","(","(?nothingSire>=\"0.5\"^^xsd:double)",")"))
    }

    test("sparqlNode SupEqual neg") {
      val v = SparqlGenerator.sparqlNode(SupEqual(0.5,negation = true,""),"nothingSire","nothingVar")
      assert(v.trim().split(" ").toList == List("FILTER","(","!(?nothingSire>=0.5)",")"))
    }

    test("sparqlNode Datatype") {
      val v = SparqlGenerator.sparqlNode(Datatype(""),"nothingSire","nothingVar")
      assert(v.trim().split(" ").toList == List("DATATYPE","(","?nothingSire",")"))
    }

    test("sparqlNode Str") {
      val v = SparqlGenerator.sparqlNode(Str(URI("test"),""),"nothingSire","nothingVar")
      assert(v.trim().split(" ").toList == List("STR","(","?nothingSire",")"))
    }

    test(" == basic test all element == ") {
      ApplyAllNode.listNodes.map(n => {
        SparqlGenerator.sparqlNode(n,"nothingSire","nothingVar")
      })
    }

  }
}
