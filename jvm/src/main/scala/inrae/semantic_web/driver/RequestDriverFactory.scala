package inrae.semantic_web.driver

import inrae.semantic_web.exception._
import inrae.semantic_web.configuration._
import org.eclipse.rdf4j.repository.sail.SailRepository
import org.eclipse.rdf4j.repository.{Repository, RepositoryConnection}
import org.eclipse.rdf4j.rio.RDFFormat
import org.eclipse.rdf4j.sail.nativerdf.NativeStore

import java.io.File
import java.net.URL
import java.nio.file.Files
import scala.util.{Failure, Success, Try}

object RequestDriverFactory  {

  lazy val dataDir: File = Files.createTempDirectory("rdf4j-discovery").toFile
  lazy val repository = new SailRepository(new NativeStore(dataDir))
  var lCon : Seq[RepositoryConnection] = Seq()

  repository.init()

  def close() : Unit = {
    lCon.foreach(_.close())
    repository.shutDown()
  }

  def build( source : Source ) : RequestDriver = {
    val r = build_withRepository(source,repository)
    lCon = lCon :+ r._2
    r._1
  }

  def build_withRepository( source : Source, repository : Repository ) : (RequestDriver, RepositoryConnection) =
  {
    source.mimetype match {
      case "application/sparql-query"  =>
        val r = Rdf4jSparqlRequestDriver(
          source.id,
          source.path,
          source.login,
          source.password,
          source.token,
          source.auth)

        (r,r.con)
      case "text/turtle" | "text/n3" | "text/rdf-xml" | "application/rdf+xml" =>

        lazy val con = repository.getConnection
        source.sourcePath match {
          case SourcePath.UrlPath =>
            /* name file is the graph name  */
            Try(con.add(new URL(source.path), source.path, RequestDriverFactory.mimetypeToRdfFormat(source.mimetype))) match {
              case Success(_) =>
              case Failure(e) => throw SWDiscoveryException(e.getMessage)
            }
          case SourcePath.LocalFile  =>
            Try(con.add(new File(source.path), source.path, RequestDriverFactory.mimetypeToRdfFormat(source.mimetype))) match {
              case Success(_) =>
              case Failure(e) =>
                throw SWDiscoveryException(e.getMessage)
            }
          case SourcePath.Content =>
            val targetStream = new java.io.ByteArrayInputStream(source.path.getBytes(java.nio.charset.StandardCharsets.UTF_8.name))
            Try(con.add(targetStream, s"http://${source.id}/graph", RequestDriverFactory.mimetypeToRdfFormat(source.mimetype))) match {
              case Success(_) =>
              case Failure(e) => throw SWDiscoveryException(e.getMessage)
            }
          case _ => throw SWDiscoveryException("Bad definition of source configuration :"+source.toString)
          }

        (Rdf4jLocalRequestDriver(con),con)
      case _ =>
          throw SWDiscoveryException("Bad definition of source configuration :"+source.toString)
      }
  }

  def mimetypeToRdfFormat( mimetype : String ): RDFFormat = mimetype match {
    case "text/turtle" => RDFFormat.TURTLE
    case "text/n3" => RDFFormat.N3
    case "text/rdf-xml" => RDFFormat.RDFXML
    case _ => throw SWDiscoveryException(mimetype + " : this format is not supported")
  }

}
