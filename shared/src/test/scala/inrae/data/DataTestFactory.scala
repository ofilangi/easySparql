package inrae.data

import inrae.semantic_web.StatementConfiguration

import scala.concurrent.Future

final case class DataTestFactoryException(private val message: String = "",
                                          private val cause: Throwable = None.orNull) extends Exception(message,cause)

object DataTestFactory  {
  implicit val ec: scala.concurrent.ExecutionContext = scala.concurrent.ExecutionContext.global
  val urlEndpoint = "http://localhost:8890/sparql"

  def graph1(classname: String) = "graph:test:discovery:virtuoso1:" + classname.replace("$","")
  def graph2(classname: String) = "graph:test:discovery:virtuoso2:" + classname.replace("$","")

  private def insert(data : String,
                     graph: String,
                     url_endpoint : String=urlEndpoint) : Future[Any] = {
    PostRequest.put(s"""
        PREFIX owl: <http://www.w3.org/2002/07/owl#>
        PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>
        PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
        PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>

        INSERT {
            GRAPH <${graph}>
              {
                ${data}
              }
          }
        """.stripMargin,url_endpoint)
     // .map( _ => { println(s" ------------- ${graph} is loaded ! -------------------- ") })
     // .recover( _ =>  { throw new Error(s"Can not load graph :${graph}") } )
  }

  def insertVirtuoso1(data : String,
                      classname: String,
                      url_endpoint : String=urlEndpoint) : Future[Any] = insert(data,graph1(classname),url_endpoint)

  def insertVirtuoso2(data : String,
                      classname: String,
                      url_endpoint : String=urlEndpoint): Future[Any]= insert(data,graph2(classname),url_endpoint)

  private def delete(graph: String,
                     url_endpoint : String=urlEndpoint) : Future[Any] = {
    PostRequest.put(s"DROP SILENT GRAPH <${graph}>",url_endpoint)
      .map( _ => { println(s" ------------- ${graph} is deleted ! -------------------- ") })

  }

  def deleteVirtuoso1(classname: String,
                      url_endpoint : String=urlEndpoint) = delete(graph1(classname),url_endpoint)

  def deleteVirtuoso2(classname: String,
                      url_endpoint : String=urlEndpoint) = delete(graph2(classname),url_endpoint)


  def getConfigVirtuoso1() : StatementConfiguration = {
    StatementConfiguration.setConfigString(
      s"""
        {
         "sources" : [{
           "id"       : "configVirtuoso1",
           "url"      : "${DataTestFactory.urlEndpoint}"
         }],
         "settings" : {
            "logLevel" : "off",
            "sizeBatchProcessing" : 100
          }
         }
        """.stripMargin)
  }

  def getConfigVirtuoso2() : StatementConfiguration = {
    StatementConfiguration.setConfigString(
      s"""
        {
         "sources" : [{
           "id"       : "configVirtuoso2",
           "url"      : "${DataTestFactory.urlEndpoint}"
         }],
         "settings" : {
            "logLevel" : "off",
            "sizeBatchProcessing" : 100
          }
         }
        """.stripMargin)
  }

  val dbpedia_config_string = """
            {
             "sources" : [{
               "id"  : "dbpedia",
               "url" : "https://dbpedia.org/sparql"
             }],
            "settings" : {
              "driver" : "inrae.semantic_web.driver.JenaRequestDriver",
              "logLevel" : "trace",
              "sizeBatchProcessing" : 100,
              "cache" : false
             }
            }
            """.stripMargin.stripMargin

  def getDbpediaConfig() : StatementConfiguration = {
    StatementConfiguration.setConfigString(dbpedia_config_string)
  }
  //   "driver" : "inrae.semantic_web.driver.JenaRequestDriver",
}
