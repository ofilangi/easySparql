package fr.inrae.metabohub.semantic_web.rdf
import fr.inrae.metabohub.semantic_web.configuration.OptionPickler
import fr.inrae.metabohub.semantic_web.exception._
import scala.language.implicitConversions
import scala.scalajs.js.annotation.JSExportTopLevel
import scala.util.{Failure, Success, Try}

case class Graph(triples : Set[Triple])

case class Triple(s: SparqlDefinition, p: SparqlDefinition, o: SparqlDefinition)

sealed abstract class SparqlDefinition {

  def sparql : String
  def naiveLabel : String
}

object SparqlDefinition {

  implicit def fromAny( any : Any ): SparqlDefinition =
    any match {
      case v: SparqlDefinition => v
      case num: Int => Literal(num)
      case dec: Double => Literal(dec)
      case bool: Boolean => Literal(bool)
      case stringVar: String
        if (stringVar.startsWith("?")||stringVar.startsWith("$")) &&
          stringVar.length > 1 => QueryVariable(stringVar.substring(1, stringVar.length))
      case string: String if string.startsWith("<")&&string.endsWith(">") => URI(string)
      case string: String if string.contains(":") && string.matches("\\S+") => URI(string)
      case string: String => Literal(string)

      case _ => throw SWDiscoveryException(any.toString + " can not be cast into Sparql Def type.")
    }


  implicit def fromString(s: String): Literal[String] = Literal(s)
  implicit def fromString(s: Int): Literal[Int] = Literal(s)
  implicit def fromString(s: Boolean): Literal[Boolean] = Literal(s)
  implicit def fromString(s: Double): Literal[Double] = Literal(s)
  implicit def fromString(s: Float): Literal[Float] = Literal(s)

  implicit def fromLiteralDouble(s: Literal[Double]): Literal[String] = Literal(s.value.toString,URI("double","xsd"))

  implicit val rw: OptionPickler.ReadWriter[SparqlDefinition] = OptionPickler.ReadWriter.merge(
    IRI.rw,
    URI.rw,
    Anonymous.rw,
    PropertyPath.rw,
    Literal.rw,
    QueryVariable.rw,
  )

  def cleanString(str : String): String = {
    str.replaceAll("^\"","")
      .replaceAll("\"$","")
      .replaceAll("^<","")
      .replaceAll(">$","")
      .replaceAll("^\\?","")
  }
}

object IRI {

  implicit def fromString(s: String): IRI = IRI(s)
  implicit val rw: OptionPickler.ReadWriter[IRI] = OptionPickler.macroRW
}

@JSExportTopLevel(name="IRI")
case class IRI (var iri : String) extends SparqlDefinition {
  iri = SparqlDefinition.cleanString(iri)
  override def toString : String = {
      "<"+iri+">"
  }
  def sparql : String = toString

  def naiveLabel : String = iri.split("[/#]").last

}

object URI {
  implicit val rw: OptionPickler.ReadWriter[URI] = OptionPickler.macroRW

  implicit def fromString(s: String): URI = URI(s)

  val empty = new URI("")
}


@JSExportTopLevel(name="URI")
case class URI (localNameUser : String,nameSpaceUser : String = "") extends SparqlDefinition {
  val localName: String = nameSpaceUser match {
    case "" if !localNameUser.contains("://") => SparqlDefinition.cleanString(localNameUser.split(":").last)
    case _ => SparqlDefinition.cleanString(localNameUser)
  }

  val nameSpace: String = nameSpaceUser match {
    case "" if !localNameUser.contains("://") =>
      localNameUser.split(":") match {
        case arr if arr.length==2 => arr(0)
        case _ => "" /* something wrong if arity if different that 2 */
      }

    case _ => nameSpaceUser
  }

  override def toString : String = {
    (localName,nameSpace) match {
      case ("a",_) => "a"
      case (_,"") => "<"+localName+">"
      case _ => nameSpace + ":" + localName
    }
  }

  def sparql : String = toString

  def naiveLabel : String = localName.split("[/#]").last
}


object Anonymous {

  implicit val rw: OptionPickler.ReadWriter[Anonymous] = OptionPickler.macroRW

  implicit def fromString(s: String): Anonymous = Anonymous(s)
}

@JSExportTopLevel(name="Anonymous")
case class Anonymous(var value : String) extends SparqlDefinition {
  value = SparqlDefinition.cleanString(value)

  override def toString : String = value

  def sparql : String = toString

  def naiveLabel : String = s"Anonymous[$value]"
}

@JSExportTopLevel(name="PropertyPath")
case class PropertyPath(var value : String) extends SparqlDefinition {
  value = SparqlDefinition.cleanString(value)

  override def toString : String = value

  def sparql : String = toString

  def naiveLabel : String = s"PropertyPath[$value]"
}

object PropertyPath {

  implicit val rw: OptionPickler.ReadWriter[PropertyPath] = OptionPickler.macroRW

  implicit def fromString(s: String): PropertyPath = PropertyPath(s)
}


object Literal {
  implicit val rw: OptionPickler.ReadWriter[Literal[String]] = OptionPickler.macroRW
}

@JSExportTopLevel(name="Literal")
case class Literal[T](value : T,datatype : URI = URI.empty,ta : String="") extends SparqlDefinition {
  private val valueString : String = SparqlDefinition.cleanString(value.toString)
  val tag: String = SparqlDefinition.cleanString(ta)

  override def toString : String = value match {
    case _ : String => "\""+ valueString + "\""+ (datatype match {
        case URI.empty => ""
        case _ if tag == "" => "^^"+datatype.toString()
        case _ => ""

      }) + ( tag match {
        case "" => ""
        case _ => "@"+tag
      })

    case _ => value.toString
  }

  def toInt: Int = valueString.toInt
  def toFloat: Float = valueString.toFloat
  def toDouble: Double = valueString.toDouble
  def toBoolean: Boolean = valueString.toBoolean

  def sparql : String = toString
  def naiveLabel : String = valueString
}


object QueryVariable {
  implicit val rw: OptionPickler.ReadWriter[QueryVariable] = OptionPickler.macroRW
}

@JSExportTopLevel(name="QueryVariable")
case class QueryVariable (var name : String) extends SparqlDefinition {
  name = SparqlDefinition.cleanString(name)
  override def toString : String = {
    if (name != "*") "?"+name else name
  }
  def sparql : String = toString

  def naiveLabel : String = s"Variable[$name]"
}

@JSExportTopLevel(name="SparqlBuilder")
object SparqlBuilder {

  def create(value: ujson.Value): SparqlDefinition = {
    (Try(value("type").value) match {
      case Success(v1) => v1
      case Failure(_) => throw new Error("Can not found key `type` in obj:"+value.toString())
      }) match {
      case "uri" => createUri(value)
      case "literal" | "typed-literal"=> createLiteral(value)
      case _ => throw new Error("unknown type ")
    }
  }

  def createUri(value: ujson.Value): URI = URI(value("value").value.toString)

  def createLiteral(value: ujson.Value): Literal[String] = {
    val datatype = try { SparqlDefinition.cleanString(value("datatype").toString) match {
        case v if v.length<=0 => URI.empty
        case v => URI(v)
      }
    } catch {
      case _ : java.util.NoSuchElementException => URI.empty
    }

    val tag = try {
      SparqlDefinition.cleanString(value("tag").toString)
    } catch {
      case _ : java.util.NoSuchElementException => ""
    }

    Literal(value("value").toString, datatype,tag)
  }

}
