package inrae.semantic_web.configuration
import inrae.semantic_web.exception.SWStatementConfigurationException


object SourcePath extends Enumeration {
  type SourcePath = Value
  val LocalFile,          /* local file */
  UrlPath,            /* url access  : file url, endpoint */
  Content = Value     /* online definition */

  implicit val rw: OptionPickler.ReadWriter[SourcePath] = OptionPickler.readwriter[String].bimap[SourcePath](
    x => x.toString ,
    {
      case "LocalFile" => LocalFile
      case "UrlPath" => UrlPath
      case "Content" => Content
      case str => throw SWStatementConfigurationException(s"Unknown SourcePath $str")
    }
  )
}