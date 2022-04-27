package inrae.semantic_web.driver

import inrae.data.DataTestFactory
import inrae.semantic_web.rdf.{SparqlBuilder, URI}
import inrae.semantic_web.configuration._
import inrae.semantic_web._

import utest.{TestSuite, Tests, test}

object RequestsTest extends TestSuite {
  implicit val ec: scala.concurrent.ExecutionContext = scala.concurrent.ExecutionContext.global

  val insertData = DataTestFactory.insertVirtuoso1(
    """
      <http://aaaaaa> <http://bbbbbb> <http://cc> .
      """.stripMargin, this.getClass.getSimpleName)
  val logLevel = "off"

  val config: SWDiscoveryConfiguration = SWDiscoveryConfiguration.setConfigString(
    s"""
        {
         "sources" : [{
           "id"       : "local_sparql",
           "path"      : "${DataTestFactory.urlEndpoint}",
           "mimetype" : "application/sparql-query"
         }],
         "settings" : {
            "logLevel" : "${logLevel}",
            "sizeBatchProcessing" : 100
          }
         }
        """.stripMargin)

  val config2: SWDiscoveryConfiguration = SWDiscoveryConfiguration.setConfigString(
    s"""
        {
         "sources" : [{
           "id"       : "local_content",
           "path"  : "<http://iiaaaaaa> <http://iibbbbbb2> <http://iicc2> .",
           "mimetype" : "text/turtle",
           "sourcePath" : "Content"
         }],
         "settings" : {
            "logLevel" : "${logLevel}",
            "sizeBatchProcessing" : 100
          }
         }
        """.stripMargin)

  val config3: SWDiscoveryConfiguration = SWDiscoveryConfiguration.setConfigString(
    s"""
        {
         "sources" : [{
           "id"       : "local_content",
           "path"  : "<http://aaaaaa> <http://bbbbbb2> <http://cc2> .",
           "mimetype" : "text/turtle",
           "sourcePath" : "Content"
         },{
           "id"       : "local_content2",
           "path"  : "<http://aaaaaa> <http://bbbbbb2> <http://cc3> .",
           "mimetype" : "text/turtle",
           "sourcePath" : "Content"
         }],
         "settings" : {
            "logLevel" : "${logLevel}",
            "sizeBatchProcessing" : 100
          }
         }
        """.stripMargin)

  val contentXml : String = """<?xml version="1.0" encoding="utf-8"?>
                              |<rdf:RDF xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#" xmlns:dc="http://purl.org/dc/elements/1.1/">
                              |  <rdf:Description rdf:about="http://www.w3.org/TR/rdf-syntax-grammar">
                              |    <dc:title>RDF/XML Syntax Specification (Revised)</dc:title>
                              |    <dc:title xml:lang="en">RDF/XML Syntax Specification (Revised)</dc:title>
                              |    <dc:title xml:lang="en-US">RDF/XML Syntax Specification (Revised)</dc:title>
                              |  </rdf:Description>
                              |
                              |  <rdf:Description rdf:about="http://example.org/buecher/baum" xml:lang="de">
                              |    <dc:title>Der Baum</dc:title>
                              |    <dc:description>Das Buch ist außergewöhnlich</dc:description>
                              |    <dc:title xml:lang="en">The Tree</dc:title>
                              |  </rdf:Description>
                              |</rdf:RDF>""".stripMargin.replace("\"","\\\"").replace("\n","")

  val config4: SWDiscoveryConfiguration = SWDiscoveryConfiguration.setConfigString(
    s"""
        {
         "sources" : [{
           "id"       : "local_content",
           "path"     : "${contentXml}",
           "sourcePath" : "Content",
           "mimetype" : "text/rdf-xml"
         }],
         "settings" : {
            "logLevel" : "off",
            "sizeBatchProcessing" : 100
          }
         }
        """.stripMargin)

  val mixconfig: SWDiscoveryConfiguration = SWDiscoveryConfiguration.setConfigString(
    s"""
        {
         "sources" : [
         {
           "id"       : "local_sparql",
           "path"      : "${DataTestFactory.urlEndpoint}",
           "mimetype" : "application/sparql-query"
         },
         {
           "id"       : "local_content",
           "path"     : "<http://aaaaaa> <http://bbbbbb> <http://cc2> .",
           "mimetype" : "text/turtle"
         }],
         "settings" : {
            "logLevel" : "${logLevel}",
            "sizeBatchProcessing" : 100
          }
         }
        """.stripMargin)


  def tests : Tests = Tests {
/*
    test("federation") {
      insertData.map(_ => {
        SWDiscovery(mixconfig)
          .something("sub")
          .isSubjectOf(URI("http://bbbbbb"), "obj")
          //.console()
          .select(List("sub", "obj"))
          .commit()
          .raw
          .map(result => {
            assert(result("results")("bindings").arr.length == 2)
            assert(SparqlBuilder.createUri(result("results")("bindings")(0)("sub")).localName == "http://aaaaaa")
          })
      }).flatten
    }
*/
    test("inline turtle") {
      insertData.map(_ => {
        SWDiscovery(config2)
          .something("h1")
          .isSubjectOf(URI("http://iibbbbbb2"))
          .select(List("h1"))
          .commit()
          .raw
          .map(result => {
            assert(result("results")("bindings").arr.length == 1)
            assert(SparqlBuilder.createUri(result("results")("bindings")(0)("h1")).localName == "http://iiaaaaaa")
          })
      }).flatten
    }

    test("inline turtle 2") {
      insertData.map(_ => {
        SWDiscovery(config3)
          .something("h1")
          .isSubjectOf(URI("http://bbbbbb2"), "v")
          .select(List("v"))
          .commit()
          .raw
          .map(result => {
            assert(result("results")("bindings").arr.length == 2)
          })
      }).flatten
    }

    /**
     * TODO : Repository are mixed. We can get results from other test....work on the partitioning
     */

    test("inline rdf-xml") {
      insertData.map(_ => {
        SWDiscovery(config4)
          .prefix("dc","http://purl.org/dc/elements/1.1/")
          .something("h1")
          .isSubjectOf(URI("dc:title"), "v")
          .select(List("v"))
          .commit()
          .raw
          .map(result => {
            assert(result("results")("bindings").arr.length == 5)
          })
      }).flatten
    }
  }

}
