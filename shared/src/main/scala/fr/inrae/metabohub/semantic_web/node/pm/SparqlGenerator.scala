package fr.inrae.metabohub.semantic_web.node.pm
import fr.inrae.metabohub.semantic_web.node._
import fr.inrae.metabohub.semantic_web.rdf.{IRI, QueryVariable}
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
        case _: Projection => true
        case _ => false
      }.lastOption
        .map {
          case proj: Projection => {
            /* get All variables and check if variables asking by the user is present */
            val allVariables = pm.NodeVisitor.getAllAncestorsRef(root)
            val variables =
              proj
                .variables
                .filter(queryVariable => allVariables.contains(queryVariable.name) || queryVariable.name == "*")

            Projection(variables, proj.idRef, proj.children, proj.decorations)
          }
          case solutionSequenceModifierNode => solutionSequenceModifierNode
        }
        .map( proj => {
        (sparqlNode(proj,"","")
          + proj.children.map( child => body( child, "")).mkString(""))
      }
      ).getOrElse("*")
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

    "} " +  orderByForm +"\n" + {
      root.lSolutionSequenceModifierNode.filter {
        case l : Limit if l.value>0 => true
        case _ => false
      }.lastOption.map(sparqlNode(_,"","")).getOrElse("")
    } + {
      root.lSolutionSequenceModifierNode.filter {
        case o : Offset if o.value > 0 => true
        case _ => false
      }.lastOption.map(sparqlNode(_,"","")).getOrElse("")
    }
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
      case node : Count              => "COUNT ("+ { if (node.distinct) "DISTINCT" else "" } +
        " concat("+ node.listVarToCount.map("str("+_.sparql+")").mkString(",") +"))"
   //   case node : CountAll           => "COUNT ("+ { if (node.distinct) "DISTINCT" else "" } + " * )"
      case _ : Distinct              => "DISTINCT "
      case _ : Reduced               => "REDUCED "
      case node : Projection if node.variables.length>0     => node.variables.mkString(" ")
      case node : Projection if node.variables.length == 0  => ""
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

      case _ : Datatype              => "DATATYPE ( " + "?"+ varIdSire  + " )"
      case _ : Str    if varIdSire.length>0  => "STR ( " + "?"+ varIdSire  + " )"
      case n : Str                    => "STR ( " + "?"+ n.term.sparql  + " )"
      case n : Lang                   => "LANG ( " + "?"+ n.term.sparql  + " )"
      case n : LangMatches            => "LANGMATCHES ( " + "LANG(?"+ n.term.sparql+"),"+ n.term  + " )"

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
      case root : Root                            => { "" }
      case s : Something if s.children.length>0   => ""
      case s : Something if s.children.length==0  => "{ " +
                                            "{ " + "?"+ variableName + " " + "?property_"+variableName+" "+ "?object_"+variableName +
                                           " } UNION { [] " + "?"+ variableName + " [] " + "} UNION { "+
                                                             " "+ "?subject_"+variableName + " "+ "?property_"+variableName+ " ?"+ variableName  + " }" + " }"
      case u : UnionBlock    if u.children.length>0 => "{ " +
        u.children.map( block => {  sparqlNode(block,u.s.idRef,variableName) + " }" }).mkString(" } UNION { ") +" }"
      case _ : UnionBlock                           => ""
      case _ : NotBlock                             => "???????????????"
      case _ : DatatypeNode                         => ""
      case _ : SourcesNode                          => ""
      case _ :   SparqlDefinitionExpression         => "???????????????"
      case _                                        => throw new Error("Not implemented yet :"+n.getClass.getName)
    }
  }

  def body(n: Node, /* current node to browse with children */
           varIdSire : String = "" /* sire variable */
          )  : String = {
    val variableName : String = n.idRef
    sparqlNode(n,varIdSire,variableName) + n.children.map( child => body( child, variableName)).mkString("")
  }
}
