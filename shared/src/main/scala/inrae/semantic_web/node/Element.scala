package inrae.semantic_web.node

import inrae.semantic_web.SWDiscoveryException
import inrae.semantic_web.rdf._
import upickle.default.{macroRW, ReadWriter => RW}
import wvlet.log.Logger.rootLogger.debug

import java.util.UUID.randomUUID
import scala.reflect.ClassTag
import scala.scalajs.js.annotation.JSExportTopLevel

sealed abstract class Node(val idRef : String,val children: Seq[Node] = Seq[Node](),val decoratingAttributeMap : Map[String,Any] = Map())
{
  def reference(): String = idRef

  def copy(children : Seq[Node]=children,decoratingAttributeMap : Map[String,Any] =decoratingAttributeMap) : Node

  def addDecoratingAttribute(key : String, value : Any) : Node =
    copy(children, decoratingAttributeMap + (key -> value) )

  def addChildren(n: Node): Node =  copy(children :+ n )

  def addChildren(focusId : String, n: Node): Node =  {
    focusId match {
      case _ if focusId == idRef && accept(n) => copy(children.map( _.addChildren(focusId,n) ) :+ n )
      case _ if focusId == idRef && !accept(n) =>
        throw SWDiscoveryException(s"cannot add this child [${n.getClass.getSimpleName}] to the current node [${getClass.getSimpleName}]")
      case _ => copy(children.map( _.addChildren(focusId,n) ))
    }
  }

  def getRdfNode(ref : String,sep : String ="") : Option[RdfNode] = {
    debug(" -- getRdfNode -- ")
    this match {
      case  n : RdfNode if ref == n.reference() => Some(n)
      case _ if children.nonEmpty => children.flatMap(c => c.getRdfNode(ref, sep + "*")).headOption
      case _ =>  None
    }
  }

  override def toString : String = {
    this.getClass.getSimpleName+ "@"+idRef + " - " + { children.length match {
      case l if l>0 => " ["+children.toString()+"]"
      case _ => "**lChildren==0**"
    } }
  }

  /* everything by default*/
  def accept(n: Node): Boolean = true

  def referencesChildren() : Seq[String] = idRef +: children.flatMap( a => { a.reference() +: a.referencesChildren() } ).distinct

  def getChild[SpecializedNodeType <: Node ](that : SpecializedNodeType)(implicit tag: ClassTag[SpecializedNodeType]) : Seq[SpecializedNodeType]  = {
    {
      this.asInstanceOf[SpecializedNodeType] match {
      case _ : SpecializedNodeType => Seq[SpecializedNodeType](this.asInstanceOf[SpecializedNodeType])
      case _ => Seq[SpecializedNodeType]()
      }
    } ++ {
      children.flatMap( c => c.getChild[SpecializedNodeType](that) )
    }
  }

}

object Node {

  implicit val rw: RW[Node] = RW.merge(
    Root.rw,
    RdfNode.rw,
    Value.rw,
    ListValues.rw,
    LogicNode.rw,
    FilterNode.rw,
    DatatypeNode.rw,
    SourcesNode.rw,
    Bind.rw,
    ExpressionNode.rw,
    SolutionSequenceModifierNode.rw
  )
}

object Root {
  implicit val rw: RW[Root] = macroRW
}

/* Node case */
@JSExportTopLevel(name="Root")
case class Root(
                 override val idRef : String=randomUUID.toString,
                 prefixes : Map[String,IRI] = Map(
                   "owl" -> IRI("http://www.w3.org/2002/07/owl#"),
                   "rdf" -> IRI("http://www.w3.org/1999/02/22-rdf-syntax-ns#"),
                   "rdfs"->IRI("http://www.w3.org/2000/01/rdf-schema#"),
                   "xsd"->IRI("http://www.w3.org/2001/XMLSchema#")
                 ),
                 defaultGraph : Seq[IRI]    = List[IRI](),
                 namedGraph : Seq[IRI]      = List[IRI](),
                 lDatatypeNode : Seq[DatatypeNode] = List[DatatypeNode](),
                 lSourcesNodes : Seq[SourcesNode] = List[SourcesNode](),
                 lBindNode : Seq[Bind] = List[Bind](),
                 lSolutionSequenceModifierNode : Seq[SolutionSequenceModifierNode] = List[SolutionSequenceModifierNode](),
                 override val children: Seq[Node] = Seq[Node](),
               ) extends Node(idRef,children) {
  /* prefix management */

  def addPrefix(short : String,long : IRI) : Root = {
    Root(idRef,prefixes + (short -> long ),defaultGraph,namedGraph,lDatatypeNode,lSourcesNodes,lBindNode,lSolutionSequenceModifierNode,children)
  }

  def getPrefix(short : String) : IRI = prefixes.getOrElse(short,IRI(""))

  def getPrefixes() : Map[String,IRI] = prefixes

  def addDefaultGraph(graph : IRI) : Root = {
    Root(idRef,prefixes,defaultGraph :+ graph,namedGraph,lDatatypeNode,lSourcesNodes,lBindNode,lSolutionSequenceModifierNode,children)
  }

  def addNamedGraph(graph : IRI) : Root = {
    Root(idRef,prefixes,defaultGraph,namedGraph :+ graph,lDatatypeNode,lSourcesNodes,lBindNode,lSolutionSequenceModifierNode,children)
  }

  private def addSourceNode(s : SourcesNode) : Root = {
    Root(idRef,prefixes,defaultGraph,namedGraph,lDatatypeNode,lSourcesNodes :+ s,lBindNode,lSolutionSequenceModifierNode,children)
  }

  private def addDatatype(d : DatatypeNode) : Root = {
    Root(idRef,prefixes,defaultGraph,namedGraph,lDatatypeNode :+ d,lSourcesNodes,lBindNode,lSolutionSequenceModifierNode,children)
  }

  private def addBindNode(b : Bind) : Root = {
    Root(idRef,prefixes,defaultGraph,namedGraph,lDatatypeNode ,lSourcesNodes,lBindNode :+ b,lSolutionSequenceModifierNode ,children)
  }

  private def addSolutionSequenceModifierNode(s : SolutionSequenceModifierNode) : Root = {
    Root(idRef,prefixes,defaultGraph,namedGraph,lDatatypeNode ,lSourcesNodes,lBindNode,lSolutionSequenceModifierNode :+ s,children)
  }

  override def getChild[SpecializedNodeType <: Node ](that : SpecializedNodeType)(implicit tag: ClassTag[SpecializedNodeType]) : Seq[SpecializedNodeType] = {

    { super.getChild(that) } ++
      { lSourcesNodes.flatMap( _.getChild[SpecializedNodeType](that) ) } ++
      { lDatatypeNode.flatMap( _.getChild[SpecializedNodeType](that) ) } ++
      { lBindNode.flatMap( _.getChild[SpecializedNodeType](that) ) } ++
      { lSolutionSequenceModifierNode.flatMap( _.getChild[SpecializedNodeType](that) ) } ++
      { children.flatMap( _.getChild[SpecializedNodeType](that) ) }

  }

  def sourcesNode(n : RdfNode) : Option[SourcesNode] = {
    lSourcesNodes.find( p => p.refNode == n.reference() )
  }

  override def addChildren(n: Node): Root = {

    n match {
      case s : SourcesNode => addSourceNode(s)
      case d : DatatypeNode => addDatatype(d)
      case b : Bind => addBindNode(b)
      case s : SolutionSequenceModifierNode => addSolutionSequenceModifierNode(s)
      case _ => super.addChildren(n).asInstanceOf[Root]
    }
  }

  override def addChildren(focusId : String, n: Node): Root = {
    if ( focusId == idRef)
      addChildren(n)
    else {

      Root(
        idRef,
        prefixes,
        defaultGraph,
        namedGraph,
        lDatatypeNode.map(_.addChildren(focusId,n).asInstanceOf[DatatypeNode]) ,
        lSourcesNodes.map(_.addChildren(focusId,n).asInstanceOf[SourcesNode]),
        lBindNode.map(_.addChildren(focusId,n).asInstanceOf[Bind]),
        lSolutionSequenceModifierNode.map(_.addChildren(focusId,n).asInstanceOf[SolutionSequenceModifierNode]),
        children.map(_.addChildren(focusId,n)))
    }
  }


  def copy(children : Seq[Node]) : Node = {
    Root(idRef,prefixes,defaultGraph,namedGraph,lDatatypeNode,lSourcesNodes,lBindNode,lSolutionSequenceModifierNode,children)
  }

  /* Accept only something on the root */
  override def accept(n: Node): Boolean = n match {
    case _ : Something => true
    case _ : SourcesNode => true
    case _ : DatatypeNode => true
    case _ : Bind => true
    case _ : SolutionSequenceModifierNode => true
    case _ => false
  }

  override def toString : String = {
    super.toString + "\n" +
    "* lDatatypeNode@"+ { lDatatypeNode.length match {
      case l if l>0 => " ["+lDatatypeNode.toString()+"]"
      case _ => ""
    } } + "\n" +
      "* lSourcesNodes@"+ { lSourcesNodes.length match {
      case l if l>0 => " ["+lSourcesNodes.toString()+"]"
      case _ => ""
    } } + "\n" +
      "* lBindNode@"+ { lBindNode.length match {
      case l if l>0 => " ["+lBindNode.toString()+"]"
      case _ => ""
    } }  + "\n" +
      "* lSolutionSequenceModifierNode@"+ { lSolutionSequenceModifierNode.length match {
      case l if l>0 => " ["+lSolutionSequenceModifierNode.toString()+"]"
      case _ => ""
    } }
  }
}

object RdfNode {
  implicit val rw: RW[RdfNode] = RW.merge(
    Something.rw,
    SubjectOf.rw,
    ObjectOf.rw,
    LinkTo.rw,
    LinkFrom.rw
  )
}

/* triplets */
abstract class RdfNode(override val idRef : String,override val children: Seq[Node] = Seq[Node]()) extends Node(idRef,children) {
  /* everything by default*/
  override def accept(n: Node): Boolean = n match {
    case _ : Something  => false
    case _ : URIRdfNode => true
    case _ : FilterNode => true
    case _ : Value      => true
    case _ : ListValues => true
    case _ : Bind       => true
    case _              => false
  }
}


abstract class URIRdfNode(override val idRef : String,val term : SparqlDefinition,override val children: Seq[Node] = Seq[Node]())
  extends RdfNode(idRef,children)


object Something {
  implicit val rw: RW[Something] = macroRW
}

@JSExportTopLevel(name="Something")
case class Something(override val idRef: String,override val children: Seq[Node] = Seq[Node]()) extends RdfNode(idRef,children) {

  def copy(children : Seq[Node]) : Node = {
    Something(idRef,children)
  }
}

object SubjectOf {
  implicit val rw: RW[SubjectOf] = macroRW
}


case class SubjectOf(
                      override val idRef : String = randomUUID.toString,
                      override val term : SparqlDefinition,
                      override val children: Seq[Node] = Seq[Node]()) extends URIRdfNode(idRef,term,children) {

  def copy(children : Seq[Node]) : Node = {
    SubjectOf(idRef,term,children)
  }
}

object ObjectOf {
  implicit val rw: RW[ObjectOf] = macroRW
}

@JSExportTopLevel(name="ObjectOf")
case class ObjectOf(
                     override val idRef : String,
                     override val term : SparqlDefinition,
                     override val children: Seq[Node] = Seq[Node]()) extends URIRdfNode(idRef,term,children) {

  def copy(children : Seq[Node]) : Node = {
    ObjectOf(idRef,term,children)
  }
}


object LinkTo {
  implicit val rw: RW[LinkTo] = macroRW
}

@JSExportTopLevel(name="LinkTo")
case class LinkTo(
                   override val idRef : String,
                   override val term : SparqlDefinition,
                   override val children: Seq[Node] = Seq[Node]()) extends URIRdfNode(idRef,term,children) {
  def copy(children : Seq[Node]) : Node = {
    LinkTo(idRef,term,children)
  }
}

object LinkFrom {
  implicit val rw: RW[LinkFrom] = macroRW
}

@JSExportTopLevel(name="LinkFrom")
case class LinkFrom(
                     override val idRef : String,
                     override val term : SparqlDefinition,
                     override val children: Seq[Node] = Seq[Node]()) extends URIRdfNode(idRef,term,children) {

  def copy(children : Seq[Node]) : Node = {
    LinkFrom(idRef,term,children)
  }
}

object Value {
  implicit val rw: RW[Value] = macroRW
}

case class Value(
                  var term : SparqlDefinition,
                  override val idRef : String=randomUUID.toString,
                  override val children: Seq[Node] = Seq[Node]()) extends Node(idRef,children) {

  override def toString : String = "VALUE("+term.toString+")"

  override def accept(n: Node): Boolean = n match {
    case _ : Something  => false
    case _ : URIRdfNode => true
    case _              => false
  }

  def copy(children : Seq[Node]) : Node = Value(term,idRef,children)
}

object ListValues {
  implicit val rw: RW[ListValues] = macroRW
}

case class ListValues(var terms : Seq[SparqlDefinition],override val idRef : String=randomUUID.toString,override val children: Seq[Node] = Seq[Node]()) extends Node(idRef,children) {

  override def toString : String = "VALUES("+terms.toString+")"

  override def accept(n: Node): Boolean = n match {
    case _ : Something  => false
    case _ : URIRdfNode => true
    case _              => false
  }

  def copy(children : Seq[Node]) : ListValues = ListValues(terms,idRef,children)

}

/* Logic */

object LogicNode {
  implicit val rw: RW[LogicNode] = RW.merge(
    UnionBlock.rw,
    NotBlock.rw
  )
}

sealed abstract class LogicNode(val sire : Node,idRef : String=randomUUID.toString,override val children: Seq[Node] = Seq[Node]()) extends Node(idRef,children) {
  implicit val rw: RW[LogicNode] = RW.merge(
    UnionBlock.rw,
    NotBlock.rw)
}

object UnionBlock {
  implicit val rw: RW[UnionBlock] = macroRW
}

case class UnionBlock(s : Node,override val idRef : String=randomUUID.toString,override val children: Seq[Node] = Seq[Node]()) extends LogicNode(s,idRef,children) {
  def copy(children : Seq[Node]) : Node = UnionBlock(s,idRef,children)
}

object NotBlock {
  implicit val rw: RW[NotBlock] = macroRW
}

case class NotBlock(s : Node,override val idRef : String,override val children: Seq[Node] = Seq[Node]()) extends LogicNode(s,idRef,children) {
  def copy(children : Seq[Node]) : NotBlock = NotBlock(s,idRef,children)
}


object FilterNode {
  implicit val rw: RW[FilterNode] = RW.merge(
    isBlank.rw,
    isLiteral.rw,
    isURI.rw,
    isBlank.rw,
    Regex.rw,
    Contains.rw,
    StrStarts.rw,
    StrEnds.rw,
    Equal.rw,
    NotEqual.rw,
    Inf.rw,
    InfEqual.rw,
    Sup.rw,
    SupEqual.rw
  )
}

/* filter */
sealed abstract class FilterNode(val negation: Boolean,
                          idRef : String,
                          override val children: Seq[Node] = Seq[Node]()) extends Node(idRef,children) {
  override def accept(n: Node): Boolean = n match {
    case _ : FilterNode => true
    case _ => false
  }
}

object isBlank {
  implicit val rw: RW[isBlank] = macroRW
}

case class isBlank(
                   override val negation: Boolean,
                   override val idRef : String,
                   override val children: Seq[Node] = Seq[Node]()) extends FilterNode(negation,idRef,children) {
  override def toString : String = negation.toString + " isBlank"

  def copy(children : Seq[Node]) : isBlank = isBlank(negation,idRef,children)
}

object isLiteral {
  implicit val rw: RW[isLiteral] = macroRW
}

case class isLiteral(
                      override val negation: Boolean,
                      override val idRef : String,
                      override val children: Seq[Node] = Seq[Node]()) extends FilterNode(negation,idRef,children) {
  override def toString : String = negation.toString + " isLiteral"

  def copy(children : Seq[Node]) : Node = isLiteral(negation,idRef,children)
}

object isURI {
  implicit val rw: RW[isURI] = macroRW
}

case class isURI(
                  override val negation: Boolean,
                  override val idRef : String,
                  override val children: Seq[Node] = Seq[Node]()) extends FilterNode(negation,idRef,children) {
  override def toString : String = negation.toString + " isURI"

  def copy(children : Seq[Node]) : isURI = isURI(negation,idRef,children)
}

object Regex {
  implicit val rw: RW[Regex] = macroRW
}


case class Regex(
                  pattern : SparqlDefinition,
                  flags : SparqlDefinition,
                  override val negation: Boolean,
                  override val idRef : String) extends FilterNode(negation,idRef) {
  override def copy(children: Seq[Node]): Node = Regex(pattern,flags,negation,idRef)
}

object Contains {
  implicit val rw: RW[Contains] = macroRW
}

case class Contains(
                     value :SparqlDefinition,
                     override val negation: Boolean,
                     override val idRef : String,
                     override val children: Seq[Node] = Seq[Node]())  extends FilterNode(negation,idRef,children) {
  override def toString : String =  negation.toString + " Contains ("+value+")"

  def copy(children : Seq[Node]) : Contains = Contains(value,negation,idRef,children)
}

object StrStarts {
  implicit val rw: RW[StrStarts] = macroRW
}

case class StrStarts(
                      value :SparqlDefinition,
                      override val negation: Boolean,
                      override val idRef : String,
                      override val children: Seq[Node] = Seq[Node]())  extends FilterNode(negation,idRef,children) {
  override def toString : String =  negation.toString + " StrStarts ("+value+")"

  def copy(children : Seq[Node]) : StrStarts = StrStarts(value,negation,idRef,children)
}

object StrEnds {
  implicit val rw: RW[StrEnds] = macroRW
}

case class StrEnds(
                    value :SparqlDefinition,
                    override val negation: Boolean,
                    override val idRef : String,
                    override val children: Seq[Node] = Seq[Node]())  extends FilterNode(negation,idRef,children) {
  override def toString : String =  negation.toString + " StrEnds ("+value+")"

  def copy(children : Seq[Node]) : StrEnds = StrEnds(value,negation,idRef,children)
}

object Equal {
  implicit val rw: RW[Equal] = macroRW
}

case class Equal(
                  value :SparqlDefinition,
                  override val negation: Boolean,
                  override val idRef : String,
                  override val children: Seq[Node] = Seq[Node]())  extends FilterNode(negation,idRef,children) {
  override def toString : String = negation.toString + " == "+value

  def copy(children : Seq[Node]) : Equal = Equal(value,negation,idRef,children)
}

object NotEqual {
  implicit val rw: RW[NotEqual] = macroRW
}

case class NotEqual(
                     value :SparqlDefinition,
                     override val negation: Boolean,
                     override val idRef : String,
                     override val children: Seq[Node] = Seq[Node]())  extends FilterNode(negation,idRef,children) {
  override def toString : String = negation.toString + " == "+value

  def copy(children : Seq[Node]) : NotEqual = NotEqual(value,negation,idRef,children)
}

object Inf {
  implicit val rw: RW[Inf] = macroRW
}


case class Inf(
                value :SparqlDefinition,
                override val negation: Boolean,
                override val idRef : String,
                override val children: Seq[Node] = Seq[Node]())  extends FilterNode(negation,idRef,children) {
  override def toString : String = negation.toString + " < "+value

  def copy(children : Seq[Node]) : NotEqual = NotEqual(value,negation,idRef,children)
}

object InfEqual {
  implicit val rw: RW[InfEqual] = macroRW
}

case class InfEqual(
                     value :SparqlDefinition,
                     override val negation: Boolean,
                     override val idRef : String,
                     override val children: Seq[Node] = Seq[Node]())  extends FilterNode(negation,idRef,children) {
  override def toString : String = negation.toString + " <= "+value

  def copy(children : Seq[Node]) : InfEqual = InfEqual(value,negation,idRef,children)
}

object Sup {
  implicit val rw: RW[Sup] = macroRW
}

case class Sup(
                value :SparqlDefinition,
                override val negation: Boolean,
                override val idRef : String,
                override val children: Seq[Node] = Seq[Node]())  extends FilterNode(negation,idRef,children) {
  override def toString : String = negation.toString + " > "+value

  def copy(children : Seq[Node]) : Sup = Sup(value,negation,idRef,children)
  def duplicateWithoutChildren(): Sup = Sup(value,negation,idRef,Seq())
}

object SupEqual {
  implicit val rw: RW[SupEqual] = macroRW
}

case class SupEqual(
                     value :SparqlDefinition,
                     override val negation: Boolean,
                     override val idRef : String,
                     override val children: Seq[Node] = Seq[Node]())  extends FilterNode(negation,idRef,children) {
  override def toString : String = negation.toString + " >= "+value

  def copy(children : Seq[Node]) : SupEqual = SupEqual(value,negation,idRef,children)
  def duplicateWithoutChildren(): SupEqual = SupEqual(value,negation,idRef,Seq())
}

object DatatypeNode {
  implicit val rw: RW[DatatypeNode] = macroRW
}

/* Datatype Node */
case class DatatypeNode(refNode : String, property : SubjectOf,override val idRef : String,override val children: Seq[Node] = Seq[Node]()) extends Node(idRef,children) {
  def copy(children : Seq[Node]) : DatatypeNode = DatatypeNode(refNode,property,idRef,children)
  def duplicateWithoutChildren(): DatatypeNode = DatatypeNode(refNode,property,idRef,Seq())
}

object SourcesNode {
  implicit val rw: RW[SourcesNode] = macroRW
}

/* SourcesNode */
case class SourcesNode(refNode : String, sources : Seq[String],override val idRef : String,override val children: Seq[Node] = Seq[Node]()) extends Node(idRef,children) {
  def copy(children : Seq[Node]) : SourcesNode = SourcesNode(refNode,sources,idRef,children)
  def duplicateWithoutChildren(): SourcesNode = SourcesNode(refNode,sources,idRef,Seq())
}

/* ----------------------------------------------------------------------------------------------------------------------------- */
/* Solution Sequence Modifier */
object SolutionSequenceModifierNode {
  implicit val rw: RW[SolutionSequenceModifierNode] = RW.merge(
    OrderByAsc.rw,
    OrderByDesc.rw,
    Projection.rw,
    Distinct.rw,
    Reduced.rw,
    Offset.rw,
    Limit.rw
  )
}

sealed abstract class SolutionSequenceModifierNode(
                                                    idRef : String,
                                                    override val children: Seq[Node]) extends Node(idRef,children) {
  override def accept(n: Node): Boolean = false
}

/**
 * put the solutions in order
 */

object OrderByAsc {
  implicit val rw: RW[OrderByAsc] = macroRW
}

case class OrderByAsc(list : Seq[QueryVariable],
                      override val idRef : String,
                      override val children: Seq[Node]=Seq[Node]()) extends SolutionSequenceModifierNode(idRef,children) {
  override def copy(children: Seq[Node]): Node = OrderByAsc(list,idRef,children)
}

object OrderByDesc {
  implicit val rw: RW[OrderByDesc] = macroRW
}

case class OrderByDesc(list : Seq[QueryVariable],
                       override val idRef : String,
                       override val children: Seq[Node] = Seq[Node]()) extends SolutionSequenceModifierNode(idRef,children) {
  override def copy(children: Seq[Node]): Node = OrderByDesc(list,idRef,children)
}


/**
 * choose certain variables
 */

object Projection {
  implicit val rw: RW[Projection] = macroRW
}

case class Projection(variables : Seq[QueryVariable],
                      override val idRef : String,
                      override val children: Seq[Node]=Seq()) extends SolutionSequenceModifierNode(idRef,children) {

  override def accept(n: Node): Boolean = n match {
    case _ : ProjectionExpression => true
    case _ => false
  }

  override def copy(children: Seq[Node]): Node = Projection(variables,idRef,children)
}

/**
 * ensure solutions in the sequence are unique
 */

object Distinct {
  implicit val rw: RW[Distinct] = macroRW
}

case class Distinct(override val idRef : String,
                    override val children: Seq[Node] = Seq[Node]()) extends SolutionSequenceModifierNode(idRef,children) {
  override def copy(children: Seq[Node]): Node = Distinct(idRef,children)
}

/**
 * permit elimination of some non-distinct solutions
 */

object Reduced {
  implicit val rw: RW[Reduced] = macroRW
}

case class Reduced(override val idRef : String,
                   override val children: Seq[Node] = Seq[Node]()) extends SolutionSequenceModifierNode(idRef,children) {
  override def copy(children: Seq[Node]): Node = Reduced(idRef,children)
}

/**
 * control where the solutions start from in the overall sequence of solutions
 */

object Offset {
  implicit val rw: RW[Offset] = macroRW
}

case class Offset(value : Int,
                  override val idRef : String,
                  override val children: Seq[Node] = Seq[Node]()) extends SolutionSequenceModifierNode(idRef,children) {
  override def copy(children: Seq[Node]): Node = Offset(value,idRef,children)
}

/**
 * restrict the number of solutions
 */

object Limit {
  implicit val rw: RW[Limit] = macroRW
}


case class Limit(value : Int,
                 override val idRef : String,
                 override val children: Seq[Node]=Seq[Node]()) extends SolutionSequenceModifierNode(idRef,children) {
  override def copy(children: Seq[Node]): Node = Limit(value,idRef,children)
}
//--------------------------------------------------------------------------------------------------------------------------




/* ----------------------------------------------------------------------------------------------------------------------------- */
/* Expression */

object Bind {
  implicit val rw: RW[Bind] = macroRW
}

case class Bind(expression : ExpressionNode,
                override val idRef : String,
                override val children: Seq[Node] = Seq[Node]()) extends Node(idRef,children) {
  override def copy(children: Seq[Node]): Node = Bind(expression,idRef,children)

  override def accept(n: Node): Boolean = n match {
    case _ : Something  => false
    case _ : URIRdfNode => true
    case _ : FilterNode => true
    case _ : Value      => true
    case _ : ListValues => true
    case _              => false
  }
}

object ExpressionNode {
  implicit val rw: RW[ExpressionNode] =
    RW.merge(
      FunctionStringNode.rw,
      FunctionNumericNode.rw,
      FunctionUriNode.rw,
      BuiltInCallNode.rw
    )
}


sealed abstract class ExpressionNode(
                             override val idRef : String
                           ) extends Node(idRef,Seq[Node]())


sealed abstract class ConditionalOrExpression(
                                               listAndExpression : Seq[ConditionalAndExpression],
                                               override val idRef : String
                                             ) extends ExpressionNode(idRef)

sealed abstract class ConditionalAndExpression(
                                               listValueLogical : Seq[ValueLogical],
                                               override val idRef : String
                                             ) extends ExpressionNode(idRef)

sealed abstract class ValueLogical(
                                                relationExpression : relationExpression,
                                                override val idRef : String
                                  ) extends ExpressionNode(idRef)

sealed trait OpNumericExpression

class EqualNumericExpression extends OpNumericExpression // =
class DiffNumericExpression extends OpNumericExpression // !=
class InfNumericExpression extends OpNumericExpression // <
class SupNumericExpression extends OpNumericExpression // >
class InfEqualNumericExpression extends OpNumericExpression // <=
class SupEqualNumericExpression extends OpNumericExpression // >=
/* note : NOT and IN not implemented */

sealed abstract class relationExpression(
                                          ne : NumericExpression,
                                          listNextOpExp : Seq[(OpNumericExpression,NumericExpression)],
                                          override val idRef : String
                                        ) extends ExpressionNode(idRef)

sealed abstract class NumericExpression(
                                          exp : AdditiveExpression,
                                          override val idRef : String
                                        ) extends ExpressionNode(idRef)

sealed trait OpMultiplicativeExpression

class AddMultiplicativeExpression( v : MultiplicativeExpression) extends OpMultiplicativeExpression // '+' MultiplicativeExpression

class MinusMultiplicativeExpression( v : MultiplicativeExpression) extends OpMultiplicativeExpression //  '-' MultiplicativeExpression

// ( NumericLiteralPositive | NumericLiteralNegative )  '*' UnaryExpression
//l numeric
class MulMultiplicativeExpression[T]( l : Literal[T],
                            v : MultiplicativeExpression,
                            u: UnaryExpression) extends OpMultiplicativeExpression
// ( NumericLiteralPositive | NumericLiteralNegative )  '*' UnaryExpression
class DivMultiplicativeExpression[T]( l : Literal[T],
                            v : MultiplicativeExpression,
                            u: UnaryExpression) extends OpMultiplicativeExpression


sealed abstract class AdditiveExpression(
                                          exp : MultiplicativeExpression,
                                          listNextOpExp : Seq[(OpMultiplicativeExpression,NumericExpression)],
                                          override val idRef : String
                                        ) extends ExpressionNode(idRef)

sealed abstract class MultiplicativeExpression (
                                                 exp : MultiplicativeExpression,
                                                 override val idRef : String
                                               )extends ExpressionNode(idRef)

sealed trait OpUnaryExpression
class NotUnaryExpression extends OpUnaryExpression // =
class AddUnaryExpression extends OpUnaryExpression // !=
class MinusUnaryExpression extends OpUnaryExpression // <

sealed abstract class UnaryExpression(
                                        op : Option[OpUnaryExpression],
                                        p : PrimaryExpression,
                                        override val idRef : String
                                      ) extends ExpressionNode(idRef)


sealed abstract class PrimaryExpression(override val idRef : String) extends ExpressionNode(idRef)


case class SparqlDefinitionExpression(sd : SparqlDefinition,override val idRef : String ) extends PrimaryExpression(idRef) {
  override def copy(children: Seq[Node]): Node = SparqlDefinitionExpression(sd,idRef)
}

object FunctionStringNode {
  implicit val rw: RW[FunctionStringNode] =  RW.merge(
    SubStr.rw,
    Replace.rw
  )
}

sealed abstract class FunctionStringNode(override val idRef : String) extends PrimaryExpression(idRef)

object SubStr {
  implicit val rw: RW[SubStr] = macroRW
}

case class SubStr(
                  start : SparqlDefinition,
                  length : SparqlDefinition,
                  override val idRef : String) extends FunctionStringNode(idRef) {
  override def copy(children: Seq[Node]): Node = SubStr(start,length,idRef)
}

object Replace {
  implicit val rw: RW[Replace] = macroRW
}


case class Replace(
                    pattern : SparqlDefinition,
                    replacement : SparqlDefinition,
                    flags : SparqlDefinition,
                  override val idRef : String) extends FunctionStringNode(idRef) {
  override def copy(children: Seq[Node]): Node = Replace(pattern,replacement,flags,idRef)
}

object FunctionNumericNode {
  implicit val rw: RW[FunctionNumericNode] =  RW.merge(
    Abs.rw,
    Round.rw,
    Ceil.rw,
    Floor.rw,
    Rand.rw
  )
}

sealed abstract class FunctionNumericNode(override val idRef : String) extends PrimaryExpression(idRef)


object Abs {
  implicit val rw: RW[Abs] = macroRW
}


case class Abs(
                override val idRef : String) extends FunctionNumericNode(idRef) {
  override def copy(children: Seq[Node]): Node = Abs(idRef)
}

object Round {
  implicit val rw: RW[Round] = macroRW
}


case class Round(
                override val idRef : String) extends FunctionNumericNode(idRef) {
  override def copy(children: Seq[Node]): Node = Round(idRef)
}

object Ceil {
  implicit val rw: RW[Ceil] = macroRW
}

case class Ceil(
                  override val idRef : String) extends FunctionNumericNode(idRef) {
  override def copy(children: Seq[Node]): Node = Ceil(idRef)
}

object Floor {
  implicit val rw: RW[Floor] = macroRW
}

case class Floor(
                 override val idRef : String) extends FunctionNumericNode(idRef) {
  override def copy(children: Seq[Node]): Node = Floor(idRef)
}

object Rand {
  implicit val rw: RW[Rand] = macroRW
}

case class Rand(override val idRef : String) extends FunctionNumericNode(idRef) {
  override def copy(children: Seq[Node]): Node = Rand(idRef)
}
/* ---------------------------------------------------------------------------------------------------  */

sealed abstract class FunctionUriNode(override val idRef : String) extends PrimaryExpression(idRef)

object FunctionUriNode {
  implicit val rw: RW[FunctionUriNode] =  RW.merge(
    Datatype.rw
  )
}

object Datatype {
  implicit val rw: RW[Datatype] = macroRW
}

case class Datatype(override val idRef : String) extends FunctionUriNode(idRef) {
  override def copy(children: Seq[Node]): Node = Datatype(idRef)
}

/* ---------------------------------------------------------------------------------------------------  */


object AggregateNode {
  implicit val rw: RW[AggregateNode] = RW.merge(
    Count.rw,
    CountAll.rw
  )
}

object ProjectionExpression {
  implicit val rw: RW[ProjectionExpression] = macroRW
}

case class ProjectionExpression(`var` : QueryVariable,
                                expression : AggregateNode,
                                override val idRef : String,
                                override val children: Seq[Node] = Seq[Node]()) extends Node(idRef,children) {
  override def copy(children: Seq[Node]): Node = ProjectionExpression(`var`,expression,idRef,children)
  override def accept(n: Node): Boolean = false
}

/*
 * ------------------------------------  Aggregate
 */
sealed abstract class AggregateNode(idRef : String) extends Node(idRef,Seq()) {
  override def accept(n: Node): Boolean = false
}

object Count {
  implicit val rw: RW[Count] = macroRW
}

case class Count(
                 varToCount : QueryVariable,
                 distinct : Boolean = false,
                 override val idRef : String,
                 ) extends AggregateNode(idRef) {
  override def copy(children: Seq[Node]): Node = Count(varToCount,distinct,idRef)
}

object CountAll {
  implicit val rw: RW[CountAll] = macroRW
}

case class CountAll(distinct : Boolean = false,override val idRef : String) extends AggregateNode(idRef) {
  override def copy(children: Seq[Node]): Node = CountAll(distinct,idRef)
}

object BuiltInCallNode {
  implicit val rw: RW[BuiltInCallNode] = RW.merge(
    Str.rw
  )
}

/*
 * ------------------------------------  BuiltInCallNode
 */

sealed abstract class BuiltInCallNode(idRef : String) extends PrimaryExpression(idRef){
  override def accept(n: Node): Boolean = false
}

object Str {
  implicit val rw: RW[Str] = macroRW
}

case class Str(term: SparqlDefinition,
               override val idRef : String) extends BuiltInCallNode(idRef) {
  override def copy(children: Seq[Node]): Node = Str(term,idRef)
}

case class Lang(term: SparqlDefinition,
               override val idRef : String,
               override val children: Seq[Node] = Seq[Node]()) extends BuiltInCallNode(idRef) {
  override def copy(children: Seq[Node]): Node = Lang(term,idRef,children)
}

case class LangMatches(term: SparqlDefinition,
               override val idRef : String) extends BuiltInCallNode(idRef) {
  override def copy(children: Seq[Node]): Node = LangMatches(term,idRef)
}
