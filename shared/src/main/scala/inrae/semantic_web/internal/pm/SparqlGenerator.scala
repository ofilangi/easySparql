package inrae.semantic_web.internal.pm
import inrae.semantic_web.internal._
import inrae.semantic_web.rdf.{IRI, QueryVariable}
import wvlet.log.Logger.rootLogger._


final case class SparqlGeneratorException(private val message: String = "",
                                          private val cause: Throwable = None.orNull) extends Exception(message,cause)
/**
 *
 */
object SparqlGenerator  {

  def prefixes(prefixes : Map[String,IRI]) : String = {
    prefixes.map {
      case (k,v) => "PREFIX "+k+": "+v.sparql
    }.mkString("\n")
  }

  def from(graphs : Seq[IRI]): String = graphs.map( g => "FROM "+g.sparql).mkString("\n")

  def fromNamed(graphs : Seq[IRI]): String = graphs.map( g => "FROM NAMED "+g.sparql).mkString("\n")

  def solutionSequenceModifierStart(root : Root) : String = {

    "SELECT " + {
      root.lSolutionSequenceModifierNode.filter {
        case _ : Distinct => true
        case _ => false
      }.lastOption.map(sparqlNode(_,"","")).getOrElse("")
    } + {
      root.lSolutionSequenceModifierNode.filter {
        case _ : Reduced => true
        case _ => false
      }.lastOption.map(sparqlNode(_,"","")).getOrElse("")
    } + {
      root.lSolutionSequenceModifierNode.filter {
        case _ : Projection => true
        case _ => false
      }.lastOption.map( proj => {
        (sparqlNode(proj,"","")
          + proj.children.map( child => body( child, "")).mkString(""))
      }
      ).getOrElse("")
    } + "\n" +
      from(root.defaultGraph) +"\n"+
      fromNamed(root.namedGraph) +"\n"+
      "WHERE {"
  }

  def solutionSequenceModifierEnd(root : Root) : String = {

    val orderByForm_asc = {
      root.lSolutionSequenceModifierNode.filter {
        case o : OrderByAsc if o.list.length>0 => true
        case _ => false
      }.lastOption.map(sparqlNode(_,"","")).getOrElse("")
    }

    val orderByForm_desc ={
      root.lSolutionSequenceModifierNode.filter {
        case d : OrderByDesc if d.list.length>0 =>  true
        case _ => false
      }.lastOption.map(sparqlNode(_,"","")).getOrElse("")
    }

    val orderByForm = orderByForm_asc + orderByForm_desc match {
      case v if v != "" => "ORDER BY " + orderByForm_asc + " " + orderByForm_desc
      case _ => ""
    }

    "} " + {
      root.lSolutionSequenceModifierNode.filter {
        case l : Limit if l.value>0 => true
        case _ => false
      }.lastOption.map(sparqlNode(_,"","")).getOrElse("")
    } + {
      root.lSolutionSequenceModifierNode.filter {
        case o : Offset if o.value > 0 => true
        case _ => false
      }.lastOption.map(sparqlNode(_,"","")).getOrElse("")
    } +  orderByForm
  }

  def prologCountSelection(varCount : String) : String = {
    "SELECT ( COUNT(*) as ?"+varCount+" )"
  }


  def sparqlNode(n: Node,
                 varIdSire : String,
                 variableName : String) : String = {
    trace(varIdSire+" - "+variableName)
    n match {
      case node : SubjectOf          => "\t?" + varIdSire + " " + node.term.toString + " " + "?"+ variableName + " .\n"
      case node : ObjectOf           => "\t?" + variableName + " " + node.term.toString + " " + "?"+ varIdSire + " .\n"
      case node : LinkTo           => "\t?"+ varIdSire + " " + "?" + variableName + " " + node.term.toString + " .\n"
      case node : LinkFrom           => node.term.toString + " " + "?" + variableName + " " + "?"+ varIdSire + " .\n"
      case node : Value              => node.term match {
        case _ : QueryVariable => "\tBIND ( ?" + varIdSire +  " AS " + node.term.toString + ")"
        case _  =>  "\tVALUES ?" +varIdSire+ " { " + node.term.toString + " } .\n" }
      case node : ListValues         => "\tVALUES ?" +varIdSire+ " { " + node.terms.map(t => t.sparql).mkString(" ") + " } .\n"
      case node : ProjectionExpression  => "(" + sparqlNode(node.expression,node.idRef,variableName) + " AS "+ node.`var` + ") "
      case node : Bind               => "\tBIND (" + sparqlNode(node.expression,varIdSire,variableName) + " AS "+ "?" + node.idRef + ") \n"
      case node : Count              => "COUNT ("+ { if (node.distinct) "DISTINCT" else "" } + " "+ node.varToCount.sparql +")"
      case node : CountAll           => "COUNT ("+ { if (node.distinct) "DISTINCT" else "" } + " * )"
      case _ : Distinct              => "DISTINCT "
      case _ : Reduced               => "REDUCED "
      case node : Projection         => node.variables.mkString(" ")
      case node : Limit              => "LIMIT " + node.value + " "
      case node : Offset             => "OFFSET " + node.value + " "
      case node : OrderByAsc         => node.list.mkString(" ")
      case node : OrderByDesc        => "DESC (" + node.list.mkString(") DESC (") + ")"
      /* Expression Node */
      case node : SubStr             => "SUBSTR (" + "?"+ varIdSire  + "," + node.start.toString + "," + node.length.toString + ")"
      case node : Replace            => "REPLACE (" + "?"+ varIdSire  + "," + node.pattern.sparql + "," + node.replacement.sparql + ","+ node.flags.sparql + ")"
      case _ : Abs                   => "ABS (" + "?"+ varIdSire  +  ")"
      case _ : Round                 => "ROUND (" + "?"+ varIdSire  +  ")"
      case _ : Floor                 => "FLOOR (" + "?"+ varIdSire  +  ")"
      case _ : Ceil                  => "CEIL (" + "?"+ varIdSire  +  ")"
      case _ : Rand                  => "RAND ()"

      case node : FilterNode         => "\tFILTER ( " + {
        if (node.negation) {
          "!"
        } else {
          ""
        }
      } + {
        node match {
          case node : Regex              => "regex (str(" + "?"+ varIdSire  + ")," + node.pattern.sparql + "," + node.flags.sparql + ")"
          case n : Contains           => "contains(str(" + "?" +varIdSire + "),"+ n.value.sparql + ")"
          case n : StrStarts          => "strStarts(str(" + "?" +varIdSire + "),"+ n.value.sparql + ")"
          case n : StrEnds            => "strEnds(str(" + "?" +varIdSire + "),"+ n.value.sparql + ")"
          case n : Equal              => "(?" +varIdSire + "="+ n.value.sparql + ")"
          case n : NotEqual           => "(?" +varIdSire + "!="+ n.value.sparql + ")"
          case n : Inf                => "(?" +varIdSire + "<" + n.value.sparql + ")"
          case n : InfEqual           => "(?" +varIdSire + "<=" + n.value.sparql + ")"
          case n : Sup                => "(?" +varIdSire + ">" + n.value.sparql + ")"
          case n : SupEqual           => "(?" +varIdSire + ">=" + n.value.sparql + ")"
          case _ : isBlank            => "isBlank(" + "?" +varIdSire + ")"
          case _ : isURI              => "isURI(" + "?" +varIdSire + ")"
          case _ : isLiteral          => "isLiteral(" + "?" +varIdSire + ")"
          case _ => throw new Exception("SparqlGenerator::sparqlNode . [Devel error] Node undefined ["+n.toString+"]")
        }
      } + " )\n"
      case root : Root                   => { "" }
      case _ : Something              => ""
      case _                               => throw new Error("Not implemented yet :"+n.getClass.getName)
    }
  }

  def body(n: Node, /* current node to browse with children */
           varIdSire : String = "" /* sire variable */
          )  : String = {
    val variableName : String = n.idRef
    sparqlNode(n,varIdSire,variableName) + n.children.map( child => body( child, variableName)).mkString("")
  }
}
