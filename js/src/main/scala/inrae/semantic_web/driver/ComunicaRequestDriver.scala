package inrae.semantic_web.driver

import com.github.p2m2.facade._
import inrae.semantic_web.configuration.SourcePath
import inrae.semantic_web.configuration.SourcePath.SourcePath
import inrae.semantic_web.exception.SWDiscoveryException
import inrae.semantic_web.driver.ComunicaRequestDriver.SourceComunica
import inrae.semantic_web.sparql.QueryResult

import scala.concurrent.{Future, Promise}
import scala.scalajs.js
import scala.scalajs.js.|
import scala.util.{Failure, Success, Try}

object ComunicaRequestDriver {
  implicit val ec: scala.concurrent.ExecutionContext = scala.concurrent.ExecutionContext.global
  type SourceComunica = String | SourceDefinitionNewQueryEngine | N3.Store

  def sourceFromUrl(url : String, mimetype:String) : SourceDefinitionNewQueryEngine = {
    SourceDefinitionNewQueryEngine(`type`= mimetype match {
      case "application/sparql-query" => SourceType.sparql
      case "hypermedia" => SourceType.hypermedia
      case _ => SourceType.file
    },url)
  }

  def sourceFromContentN3Parser(content: String, mimetype:String) : Future[N3.Store] = {
    val store = new N3.Store()
    val p = Promise[N3.Store]()

    new N3.Parser(N3Options(baseIRI="http://com.github.p2m2.discovery/",format=(
      mimetype match {
        case "text/turtle" => N3FormatOption.Turtle
        case "text/n3" => N3FormatOption.N3
        case _ => throw SWDiscoveryException(s" ${mimetype} format is not managed")
      })))
      .parse(content, (error : String , quad : js.UndefOr[Quad] , prefixes : js.UndefOr[js.Object] ) => {
        quad.get match {
          case null => {
            p success store
          }
          case q => store.addQuad(q)
        }
      })
    p.future
  }

  def sourceFromContentRdfXml(content: String) : Future[N3.Store] = {
    val store = new N3.Store()
    val p = Promise[N3.Store]()

    val parser = new RdfXmlParser(RdfXmlParserOptions(baseIRI="http://com.github.p2m2.discovery/"))

    parser.on("data", (quad : Quad) => {
      store.addQuad(quad)
    }).on("error", (error : String) => {throw SWDiscoveryException(error)})
      .on("end", () => {
        p success store
      })

    parser.write(content)
    parser.end()

    p.future
  }

  def sourceFromContent(content: String, mimetype:String) : Future[N3.Store] = {
    mimetype match {
      case "text/rdf-xml" =>sourceFromContentRdfXml(content)
      case _ => sourceFromContentN3Parser(content,mimetype)
    }
  }

  def requestOnSWDBWithSources(query: String, sources : List[SourceComunica]): Future[QueryResult] =
    Try(Comunica.newEngine().query(query,
      QueryEngineOptions(
        sources = sources,
        lenient=false,
        queryFormat = QueryFormat.sparql))
      .toFuture.flatMap( (results : IQueryResult) => {
      Comunica.newEngine().resultToString(results,"application/sparql-results+json")
        .toFuture.map( v => {
        val p = Promise[String]()
        var sparql_results = ""
        v.data.on("data", (chunk: js.Object) => {
          sparql_results += chunk.toString
        }).on("end", () => {
          p success sparql_results
        }).on("error", (error: String) => {
          p failure SWDiscoveryException(error)
        })
        p.future
      }).recover(error => {
        throw SWDiscoveryException(error.toString)
      })
        .flatMap(_.map(QueryResult(_)))
    })) match {
      case Success(result) => result
      case Failure(e) => throw SWDiscoveryException(e.toString)
    }
}

case class ComunicaRequestDriver(idName : String,
                                 path: String,
                                 sourcePath : SourcePath,
                                 mimetype: String,
                                 login : Option[String] = None ,
                                 password: Option[String] = None ) extends RequestDriver {



  def requestOnSWDB(query: String): Future[QueryResult] = {
    (sourcePath match {
        case SourcePath.UrlPath => Future { ComunicaRequestDriver.sourceFromUrl(path, mimetype) }
        case SourcePath.Content => ComunicaRequestDriver.sourceFromContent(path, mimetype)
      }).asInstanceOf[Future[SourceComunica]]
      .flatMap( source => ComunicaRequestDriver.requestOnSWDBWithSources(query,List(source)) )
  }
}
