package inrae.semantic_web.node

import inrae.semantic_web.configuration.OptionPickler
import inrae.semantic_web.exception._
import inrae.semantic_web.rdf._
import wvlet.log.Logger.rootLogger.debug

import java.util.UUID.randomUUID
import scala.reflect.ClassTag
import scala.scalajs.js.annotation.JSExportTopLevel

sealed abstract class Node(
                            val idRef : String,
                            val children: Seq[Node],
                            val decorations : Map[String,String]
                          )
{
  def reference(): String = idRef

  def copy(children : Seq[Node]=children,decoratingAttributeMap : Map[String,String]=decorations) : Node

  def addDecoratingAttribute(key : String, value : String) : Node = copy(children,decorations + (key -> value))

  def addChildren(n: Node): Node =  copy(children :+ n)

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
      case _ => ""
    } } + { decorations.size match {
      case i if i>0 => " { " + decorations.toString + "}"
      case _ => ""
    }
    }
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
  implicit val rw: OptionPickler.ReadWriter[Node] = OptionPickler.ReadWriter.merge(
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
    SolutionSequenceModifierNode.rw,
    ProjectionExpression.rw,
    AggregateNode.rw
  )
}

object Root {
  implicit val rw: OptionPickler.ReadWriter[Root] = OptionPickler.macroRW
}

/* Node case */
@JSExportTopLevel(name="Root")
final case class Root(
                 override val idRef : String=randomUUID.toString,
                 prefixes : Map[String,IRI] = Map(
                   "owl" -> IRI("http://www.w3.org/2002/07/owl#"),
                   "rdf" -> IRI("http://www.w3.org/1999/02/22-rdf-syntax-ns#"),
                   "rdfs"-> IRI("http://www.w3.org/2000/01/rdf-schema#"),
                   "xsd" -> IRI("http://www.w3.org/2001/XMLSchema#")
                 ),
                 directives: Seq[String]      = Seq(),
                 defaultGraph : Seq[IRI]    = List[IRI](),
                 namedGraph : Seq[IRI]      = List[IRI](),
                 lDatatypeNode : Seq[DatatypeNode] = List[DatatypeNode](),
                 lSourcesNodes : Seq[SourcesNode] = List[SourcesNode](),
                 lBindNode : Seq[Bind] = List[Bind](),
                 lSolutionSequenceModifierNode : Seq[SolutionSequenceModifierNode] = List[SolutionSequenceModifierNode](),
                 override val children: Seq[Node] = Seq[Node](),
                 override val decorations: Map[String,String] = Map()
               ) extends Node(idRef,children,decorations) {
  /* prefix management */

  def addPrefix(short : String,long : IRI) : Root = {
    Root(idRef,prefixes + (short -> long ),directives,defaultGraph,namedGraph,lDatatypeNode,lSourcesNodes,lBindNode,lSolutionSequenceModifierNode,children,decorations)
  }
  def getPrefix(short : String) : IRI = prefixes.getOrElse(short,IRI(""))

  def getPrefixes : Map[String,IRI] = prefixes

  def addDirective(directive : String) : Root =
    Root(idRef,prefixes,directives :+ directive ,defaultGraph,namedGraph,lDatatypeNode,lSourcesNodes,lBindNode,lSolutionSequenceModifierNode,children,decorations)

  def addDefaultGraph(graph : IRI) : Root =
    Root(idRef,prefixes,directives,defaultGraph :+ graph,namedGraph,lDatatypeNode,lSourcesNodes,lBindNode,lSolutionSequenceModifierNode,children,decorations)

  def addNamedGraph(graph : IRI) : Root =
    Root(idRef,prefixes,directives,defaultGraph,namedGraph :+ graph,lDatatypeNode,lSourcesNodes,lBindNode,lSolutionSequenceModifierNode,children,decorations)

  private def addSourceNode(s : SourcesNode) : Root =
    Root(idRef,prefixes,directives,defaultGraph,namedGraph,lDatatypeNode,lSourcesNodes :+ s,lBindNode,lSolutionSequenceModifierNode,children,decorations)

  private def addDatatype(d : DatatypeNode) : Root =
    Root(idRef,prefixes,directives,defaultGraph,namedGraph,lDatatypeNode :+ d,lSourcesNodes,lBindNode,lSolutionSequenceModifierNode,children,decorations)


  private def addBindNode(b : Bind) : Root =
    Root(idRef,prefixes,directives,defaultGraph,namedGraph,lDatatypeNode ,lSourcesNodes,lBindNode :+ b,lSolutionSequenceModifierNode ,children,decorations)

  private def addSolutionSequenceModifierNode(s : SolutionSequenceModifierNode) : Root =
    Root(idRef,prefixes,directives,defaultGraph,namedGraph,lDatatypeNode ,lSourcesNodes,lBindNode,lSolutionSequenceModifierNode :+ s,children,decorations)


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
    if ( focusId == idRef) {
      addChildren(n)
    } else {
      Root(
        idRef,
        prefixes,
        directives,
        defaultGraph,
        namedGraph,
        lDatatypeNode.map(_.addChildren(focusId,n).asInstanceOf[DatatypeNode]) ,
        lSourcesNodes.map(_.addChildren(focusId,n).asInstanceOf[SourcesNode]),
        lBindNode.map(_.addChildren(focusId,n).asInstanceOf[Bind]),
        lSolutionSequenceModifierNode.map(_.addChildren(focusId,n).asInstanceOf[SolutionSequenceModifierNode]),
        children.map(_.addChildren(focusId,n)),
        decorations
      )
    }
  }


  def copy(children : Seq[Node],decoratingAttributeMap : Map[String,String]=decorations) : Node = {
    Root(idRef,prefixes,directives,defaultGraph,namedGraph,lDatatypeNode,
      lSourcesNodes,lBindNode,lSolutionSequenceModifierNode,children,decoratingAttributeMap)
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
  implicit val rw: OptionPickler.ReadWriter[RdfNode] = OptionPickler.ReadWriter.merge(
    Something.rw,
    SubjectOf.rw,
    ObjectOf.rw,
    LinkTo.rw,
    LinkFrom.rw
  )
}

/* triplets */
abstract class RdfNode(
                        override val idRef : String,
                        override val children: Seq[Node],
                        override val decorations: Map[String,String]
                      ) extends Node(idRef,children,decorations) {
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


abstract class URIRdfNode(
                           override val idRef : String,
                           val term : SparqlDefinition,
                           override val children: Seq[Node],
                           override val decorations: Map[String,String])
  extends RdfNode(idRef,children,decorations)


object Something {
  implicit val rw: OptionPickler.ReadWriter[Something] = OptionPickler.macroRW
}

@JSExportTopLevel(name="Something")
final case class Something(
                      override val idRef: String,
                      override val children: Seq[Node] = Seq[Node](),
                      override val decorations: Map[String,String] = Map()
                    ) extends RdfNode(idRef,children,decorations) {

  def copy(children : Seq[Node]=children,decoratingAttributeMap : Map[String,String]=decorations) : Node = {
    Something(idRef,children,decoratingAttributeMap)
  }
}

object SubjectOf {
  implicit val rw: OptionPickler.ReadWriter[SubjectOf] = OptionPickler.macroRW
}


final case class SubjectOf(
                      override val idRef : String = randomUUID.toString,
                      override val term : SparqlDefinition,
                      override val children: Seq[Node] = Seq[Node](),
                      override val decorations: Map[String,String] = Map()
                    ) extends URIRdfNode(idRef,term,children,decorations) {

  def copy(children : Seq[Node]=children,decoratingAttributeMap : Map[String,String]=decorations) : Node = {
    SubjectOf(idRef,term,children,decoratingAttributeMap)
  }
}

object ObjectOf {
  implicit val rw: OptionPickler.ReadWriter[ObjectOf] = OptionPickler.macroRW
}

@JSExportTopLevel(name="ObjectOf")
final case class ObjectOf(
                     override val idRef : String,
                     override val term : SparqlDefinition,
                     override val children: Seq[Node] = Seq[Node](),
                     override val decorations: Map[String,String] = Map()
                         ) extends URIRdfNode(idRef,term,children,decorations) {

  def copy(children : Seq[Node]=children,decoratingAttributeMap : Map[String,String]=decorations) : Node = {
    ObjectOf(idRef,term,children,decoratingAttributeMap)
  }
}


object LinkTo {
  implicit val rw: OptionPickler.ReadWriter[LinkTo] = OptionPickler.macroRW
}

@JSExportTopLevel(name="LinkTo")
final case class LinkTo(
                   override val idRef : String,
                   override val term : SparqlDefinition,
                   override val children: Seq[Node] = Seq[Node](),
                   override val decorations: Map[String,String] = Map()
                       ) extends URIRdfNode(idRef,term,children,decorations)
{
  def copy(children : Seq[Node]=children,decoratingAttributeMap : Map[String,String]=decorations) : Node = {
    LinkTo(idRef,term,children,decoratingAttributeMap)
  }
}

object LinkFrom {
  implicit val rw: OptionPickler.ReadWriter[LinkFrom] = OptionPickler.macroRW
}

@JSExportTopLevel(name="LinkFrom")
final case class LinkFrom(
                     override val idRef : String,
                     override val term : SparqlDefinition,
                     override val children: Seq[Node] = Seq[Node](),
                     override val decorations: Map[String,String] = Map()
                         ) extends URIRdfNode(idRef,term,children,decorations) {

  def copy(children : Seq[Node]=children,decoratingAttributeMap : Map[String,String]=decorations) : Node = {
    LinkFrom(idRef,term,children,decoratingAttributeMap)
  }
}

object Value {
  implicit val rw: OptionPickler.ReadWriter[Value] = OptionPickler.macroRW
}

final case class Value(
                  term : SparqlDefinition,
                  override val idRef : String=randomUUID.toString,
                  override val children: Seq[Node] = Seq[Node](),
                  override val decorations: Map[String,String] = Map()
                      ) extends Node(idRef,children,decorations) {

  override def toString : String = "VALUE("+term.toString+")"

  override def accept(n: Node): Boolean = n match {
    case _ : Something  => false
    case _ : URIRdfNode => true
    case _              => false
  }

  def copy(children : Seq[Node]=children,decoratingAttributeMap : Map[String,String]=decorations) : Node =
    Value(term,idRef,children,decoratingAttributeMap)
}

object ListValues {
  implicit val rw: OptionPickler.ReadWriter[ListValues] = OptionPickler.macroRW
}

final case class ListValues(
                             terms : Seq[SparqlDefinition],
                             override val idRef : String=randomUUID.toString,
                             override val children: Seq[Node] = Seq[Node](),
                             override val decorations: Map[String,String] = Map()
                           ) extends Node(idRef,children,decorations) {

  override def toString : String = "VALUES("+terms.toString+")"

  override def accept(n: Node): Boolean = n match {
    case _ : Something  => false
    case _ : URIRdfNode => true
    case _              => false
  }

  def copy(children : Seq[Node]=children,decoratingAttributeMap : Map[String,String]=decorations) : ListValues =
    ListValues(terms,idRef,children,decoratingAttributeMap)

}

/* Logic */

object LogicNode {
  implicit val rw: OptionPickler.ReadWriter[LogicNode] = OptionPickler.ReadWriter.merge(
    UnionBlock.rw,
    NotBlock.rw
  )
}

sealed abstract class LogicNode(
                                 val sire : Node,idRef : String=randomUUID.toString,
                                 override val children: Seq[Node],
                                 override val decorations: Map[String,String]
                               ) extends Node(idRef,children,decorations)

object UnionBlock {
  implicit val rw: OptionPickler.ReadWriter[UnionBlock] = OptionPickler.macroRW
}

final case class UnionBlock(
                             override val idRef : String=randomUUID.toString,
                             s : Node,
                             override val children: Seq[Node] = Seq[Node](),
                             override val decorations: Map[String,String] = Map()
                           ) extends LogicNode(s,idRef,children,decorations) {
  def copy(children : Seq[Node]=children,decoratingAttributeMap : Map[String,String]=decorations) : Node =
    UnionBlock(idRef,s,children,decoratingAttributeMap)
}

object NotBlock {
  implicit val rw: OptionPickler.ReadWriter[NotBlock] = OptionPickler.macroRW
}

final case class NotBlock(
                           override val idRef : String,
                           s : Node,
                           override val children: Seq[Node] = Seq[Node](),
                           override val decorations: Map[String,String] = Map()
                         ) extends LogicNode(s,idRef,children,decorations) {
  def copy(children : Seq[Node]=children,decoratingAttributeMap : Map[String,String]=decorations) : NotBlock =
    NotBlock(idRef,s,children,decoratingAttributeMap)
}


object FilterNode {
  implicit val rw: OptionPickler.ReadWriter[FilterNode] = OptionPickler.ReadWriter.merge(
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
sealed abstract class FilterNode(
                                  val negation: Boolean,
                                  override val idRef : String,
                                  override val children: Seq[Node],
                                  override val decorations: Map[String,String]
                                ) extends Node(idRef,children,decorations) {
  override def accept(n: Node): Boolean = n match {
    case _ : FilterNode => true
    case _ => false
  }
}

object isBlank {
  implicit val rw: OptionPickler.ReadWriter[isBlank] = OptionPickler.macroRW
}

final case class isBlank(
                   override val negation: Boolean,
                   override val idRef : String,
                   override val children: Seq[Node] = Seq[Node](),
                   override val decorations: Map[String,String] = Map()
                  ) extends FilterNode(negation,idRef,children,decorations) {
  override def toString : String = negation.toString + " isBlank"

  def copy(children : Seq[Node]=children,decoratingAttributeMap : Map[String,String]=decorations) : isBlank =
    isBlank(negation,idRef,children)
}

object isLiteral {
  implicit val rw: OptionPickler.ReadWriter[isLiteral] = OptionPickler.macroRW
}

final case class isLiteral(
                      override val negation: Boolean,
                      override val idRef : String,
                      override val children: Seq[Node] = Seq[Node](),
                      override val decorations: Map[String,String] = Map()
                    ) extends FilterNode(negation,idRef,children,decorations) {
  override def toString : String = negation.toString + " isLiteral"

  def copy(children : Seq[Node]=children,decoratingAttributeMap : Map[String,String]=decorations) : Node =
    isLiteral(negation,idRef,children,decoratingAttributeMap)
}

object isURI {
  implicit val rw: OptionPickler.ReadWriter[isURI] = OptionPickler.macroRW
}

final case class isURI(
                  override val negation: Boolean,
                  override val idRef : String,
                  override val children: Seq[Node] = Seq[Node](),
                  override val decorations: Map[String,String] = Map()
                      ) extends FilterNode(negation,idRef,children,decorations) {
  override def toString : String = negation.toString + " isURI"

  def copy(children : Seq[Node]=children,decoratingAttributeMap : Map[String,String]=decorations) : isURI =
    isURI(negation,idRef,children)
}

object Regex {
  implicit val rw: OptionPickler.ReadWriter[Regex] = OptionPickler.macroRW
}

final case class Regex(
                  pattern : SparqlDefinition,
                  flags : SparqlDefinition,
                  override val negation: Boolean,
                  override val idRef : String,
                  override val children: Seq[Node] = Seq[Node](),
                  override val decorations: Map[String,String] = Map()
                      ) extends FilterNode(negation,idRef,children,decorations) {
  override def copy(children: Seq[Node]=children,decoratingAttributeMap : Map[String,String]=decorations): Node
  = Regex(pattern,flags,negation,idRef,children,decoratingAttributeMap)
}

object Contains {
  implicit val rw: OptionPickler.ReadWriter[Contains] = OptionPickler.macroRW
}

final case class Contains(
                     value :SparqlDefinition,
                     override val negation: Boolean,
                     override val idRef : String,
                     override val children: Seq[Node] = Seq[Node](),
                     override val decorations: Map[String,String] = Map()
                         )  extends FilterNode(negation,idRef,children,decorations) {
  override def toString : String =  negation.toString + " Contains ("+value+")"

  def copy(children : Seq[Node]=children,decoratingAttributeMap : Map[String,String]=decorations) : Contains =
    Contains(value,negation,idRef,children,decoratingAttributeMap)
}

object StrStarts {
  implicit val rw: OptionPickler.ReadWriter[StrStarts] = OptionPickler.macroRW
}

final case class StrStarts(
                      value :SparqlDefinition,
                      override val negation: Boolean,
                      override val idRef : String,
                      override val children: Seq[Node] = Seq[Node](),
                      override val decorations: Map[String,String] = Map()
                          )  extends FilterNode(negation,idRef,children,decorations) {
  override def toString : String =  negation.toString + " StrStarts ("+value+")"

  def copy(children : Seq[Node]=children,decoratingAttributeMap : Map[String,String]=decorations) : StrStarts =
    StrStarts(value,negation,idRef,children,decoratingAttributeMap)
}

object StrEnds {
  implicit val rw: OptionPickler.ReadWriter[StrEnds] = OptionPickler.macroRW
}

final case class StrEnds(
                    value :SparqlDefinition,
                    override val negation: Boolean,
                    override val idRef : String,
                    override val children: Seq[Node] = Seq[Node](),
                    override val decorations: Map[String,String] = Map()
                        )  extends FilterNode(negation,idRef,children,decorations) {
  override def toString : String =  negation.toString + " StrEnds ("+value+")"

  def copy(children : Seq[Node]=children,decoratingAttributeMap : Map[String,String]=decorations) : StrEnds =
    StrEnds(value,negation,idRef,children,decoratingAttributeMap)
}

object Equal {
  implicit val rw: OptionPickler.ReadWriter[Equal] = OptionPickler.macroRW
}

final case class Equal(
                  value :SparqlDefinition,
                  override val negation: Boolean,
                  override val idRef : String,
                  override val children: Seq[Node] = Seq[Node](),
                    override val decorations: Map[String,String] = Map()
                        )  extends FilterNode(negation,idRef,children,decorations) {
  override def toString : String = negation.toString + " == "+value

  def copy(children : Seq[Node]=children,decoratingAttributeMap : Map[String,String]=decorations) : Equal =
    Equal(value,negation,idRef,children,decoratingAttributeMap)
}

object NotEqual {
  implicit val rw: OptionPickler.ReadWriter[NotEqual] = OptionPickler.macroRW
}

final case class NotEqual(
                     value :SparqlDefinition,
                     override val negation: Boolean,
                     override val idRef : String,
                     override val children: Seq[Node] = Seq[Node](),
                    override val decorations: Map[String,String] = Map()
                        )  extends FilterNode(negation,idRef,children,decorations) {
  override def toString : String = negation.toString + " == "+value

  def copy(children : Seq[Node]=children,decoratingAttributeMap : Map[String,String]=decorations) : NotEqual =
    NotEqual(value,negation,idRef,children,decoratingAttributeMap)
}

object Inf {
  implicit val rw: OptionPickler.ReadWriter[Inf] = OptionPickler.macroRW
}


final case class Inf(
                value :SparqlDefinition,
                override val negation: Boolean,
                override val idRef : String,
                override val children: Seq[Node] = Seq[Node](),
                    override val decorations: Map[String,String] = Map()
                        )  extends FilterNode(negation,idRef,children,decorations) {
  override def toString : String = negation.toString + " < "+value

  def copy(children : Seq[Node]=children,decoratingAttributeMap : Map[String,String]=decorations) : Inf =
    Inf(value,negation,idRef,children,decoratingAttributeMap)
}

object InfEqual {
  implicit val rw:  OptionPickler.ReadWriter[InfEqual] = OptionPickler.macroRW
}

final case class InfEqual(
                     value :SparqlDefinition,
                     override val negation: Boolean,
                     override val idRef : String,
                     override val children: Seq[Node] = Seq[Node](),
                    override val decorations: Map[String,String] = Map()
                        )  extends FilterNode(negation,idRef,children,decorations) {
  override def toString : String = negation.toString + " <= "+value

  def copy(children : Seq[Node]=children,decoratingAttributeMap : Map[String,String]=decorations) : InfEqual =
    InfEqual(value,negation,idRef,children,decoratingAttributeMap)
}

object Sup {
  implicit val rw: OptionPickler.ReadWriter[Sup] = OptionPickler.macroRW
}

final case class Sup(
                value :SparqlDefinition,
                override val negation: Boolean,
                override val idRef : String,
                override val children: Seq[Node] = Seq[Node](),
                    override val decorations: Map[String,String] = Map()
                        )  extends FilterNode(negation,idRef,children,decorations) {
  override def toString : String = negation.toString + " > "+value

  def copy(children : Seq[Node]=children,decoratingAttributeMap : Map[String,String]=decorations) : Sup =
    Sup(value,negation,idRef,children,decoratingAttributeMap)
  def duplicateWithoutChildren(): Sup = Sup(value,negation,idRef,Seq())
}

object SupEqual {
  implicit val rw: OptionPickler.ReadWriter[SupEqual] = OptionPickler.macroRW
}

final case class SupEqual(
                     value :SparqlDefinition,
                     override val negation: Boolean,
                     override val idRef : String,
                     override val children: Seq[Node] = Seq[Node](),
                    override val decorations: Map[String,String] = Map()
                        )  extends FilterNode(negation,idRef,children,decorations) {
  override def toString : String = negation.toString + " >= "+value

  def copy(children : Seq[Node]=children,decoratingAttributeMap : Map[String,String]=decorations) : SupEqual =
    SupEqual(value,negation,idRef,children,decoratingAttributeMap)

  def duplicateWithoutChildren(): SupEqual = SupEqual(value,negation,idRef,Seq())
}

object DatatypeNode {
  implicit val rw: OptionPickler.ReadWriter[DatatypeNode] = OptionPickler.macroRW
}

/* Datatype Node */
final case class DatatypeNode(
                               refNode : String,
                               property : SubjectOf,
                               override val idRef : String,
                               override val children: Seq[Node] = Seq[Node](),
                                override val decorations: Map[String,String] = Map()
                        ) extends Node(idRef,children,decorations) {
  def copy(children : Seq[Node]=children,decoratingAttributeMap : Map[String,String]=decorations) : DatatypeNode
  = DatatypeNode(refNode,property,idRef,children,decoratingAttributeMap)

  def duplicateWithoutChildren(): DatatypeNode = DatatypeNode(refNode,property,idRef,Seq())
}

object SourcesNode {
  implicit val rw: OptionPickler.ReadWriter[SourcesNode] = OptionPickler.macroRW
}

/* SourcesNode */
final case class SourcesNode(
                              refNode : String,
                              sources : Seq[String],
                              override val idRef : String,
                              override val children: Seq[Node] = Seq[Node](),
                              override val decorations: Map[String,String] = Map()
                        ) extends Node(idRef,children,decorations) {
  def copy(
            children : Seq[Node]=children,
            decoratingAttributeMap : Map[String,String]=decorations
          ) : SourcesNode = SourcesNode(refNode,sources,idRef,children,decoratingAttributeMap)

  def duplicateWithoutChildren(): SourcesNode = SourcesNode(refNode,sources,idRef,Seq())
}

/* ----------------------------------------------------------------------------------------------------------------------------- */
/* Solution Sequence Modifier */
object SolutionSequenceModifierNode {
  implicit val rw: OptionPickler.ReadWriter[SolutionSequenceModifierNode] = OptionPickler.ReadWriter.merge(
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
                                                    override val children: Seq[Node],
                                                    override val decorations: Map[String,String]
                                                  ) extends Node(idRef,children,decorations) {
  override def accept(n: Node): Boolean = false
}

/**
 * put the solutions in order
 */

object OrderByAsc {
  implicit val rw: OptionPickler.ReadWriter[OrderByAsc] = OptionPickler.macroRW
}

final case class OrderByAsc(
                             list : Seq[QueryVariable],
                             override val idRef : String,
                             override val children: Seq[Node]=Seq[Node](),
                             override val decorations: Map[String,String] = Map()
                           ) extends SolutionSequenceModifierNode(idRef,children,decorations) {
  override def copy(
                     children: Seq[Node]=children,
                     decoratingAttributeMap : Map[String,String]=decorations
                   ): Node = OrderByAsc(list,idRef,children,decoratingAttributeMap)
}

object OrderByDesc {
  implicit val rw: OptionPickler.ReadWriter[OrderByDesc] = OptionPickler.macroRW
}

final case class OrderByDesc(
                        list : Seq[QueryVariable],
                        override val idRef : String,
                        override val children: Seq[Node] = Seq[Node](),
                        override val decorations: Map[String,String] = Map()
                        ) extends SolutionSequenceModifierNode(idRef,children,decorations) {


  override def copy(
                     children: Seq[Node]=children,
                     decoratingAttributeMap : Map[String,String]=decorations
                   ): Node = OrderByDesc(list,idRef,children)
}


/**
 * choose certain variables
 */

object Projection {
  implicit val rw: OptionPickler.ReadWriter[Projection] = OptionPickler.macroRW
}

final case class Projection(
                             variables : Seq[QueryVariable],
                             override val idRef : String,
                             override val children: Seq[Node]=Seq(),
                             override val decorations: Map[String,String] = Map()
                           ) extends SolutionSequenceModifierNode(idRef,children,decorations) {

  override def accept(n: Node): Boolean = n match {
    case _ : ProjectionExpression => true
    case _ => false
  }

  override def copy(
                     children: Seq[Node]=children,
                      decoratingAttributeMap : Map[String,String]=decorations
                   ): Node = Projection(variables,idRef,children,decoratingAttributeMap)
}

/**
 * ensure solutions in the sequence are unique
 */

object Distinct {
  implicit val rw: OptionPickler.ReadWriter[Distinct] = OptionPickler.macroRW
}

final case class Distinct(
                           override val idRef : String,
                           override val children: Seq[Node] = Seq[Node](),
                           override val decorations: Map[String,String] = Map()
                        ) extends SolutionSequenceModifierNode(idRef,children,decorations) {
  override def copy(
                     children: Seq[Node]=children,
                     decoratingAttributeMap : Map[String,String]=decorations
                   ): Node = Distinct(idRef,children,decoratingAttributeMap)
}

/**
 * permit elimination of some non-distinct solutions
 */

object Reduced {
  implicit val rw: OptionPickler.ReadWriter[Reduced] = OptionPickler.macroRW
}

final case class Reduced(
                          override val idRef : String,
                          override val children: Seq[Node] = Seq[Node](),
                          override val decorations: Map[String,String] = Map()
                        ) extends SolutionSequenceModifierNode(idRef,children,decorations) {
  override def copy(
                     children: Seq[Node]=children,
                     decoratingAttributeMap : Map[String,String]=decorations
                   ): Node = Reduced(idRef,children,decoratingAttributeMap)
}

/**
 * control where the solutions start from in the overall sequence of solutions
 */

object Offset {
  implicit val rw: OptionPickler.ReadWriter[Offset] = OptionPickler.macroRW
}

final case class Offset(
                         value : Int,
                         override val idRef : String,
                         override val children: Seq[Node] = Seq[Node](),
                         override val decorations: Map[String,String] = Map()
                        ) extends SolutionSequenceModifierNode(idRef,children,decorations) {
  override def copy(
                     children: Seq[Node]=children,
                     decoratingAttributeMap : Map[String,String]=decorations
                   ): Node = Offset(value,idRef,children,decoratingAttributeMap)
}

/**
 * restrict the number of solutions
 */

object Limit {
  implicit val rw: OptionPickler.ReadWriter[Limit] = OptionPickler.macroRW
}


final case class Limit(
                        value : Int,
                        override val idRef : String,
                        override val children: Seq[Node]=Seq[Node](),
                        override val decorations: Map[String,String] = Map()
                      ) extends SolutionSequenceModifierNode(idRef,children,decorations) {
  override def copy(
                     children: Seq[Node]=children,
                     decoratingAttributeMap : Map[String,String]=decorations
                   ): Node = Limit(value,idRef,children,decoratingAttributeMap)
}
//--------------------------------------------------------------------------------------------------------------------------




/* ----------------------------------------------------------------------------------------------------------------------------- */
/* Expression */

object Bind {
  implicit val rw: OptionPickler.ReadWriter[Bind] = OptionPickler.macroRW
}

final case class Bind(
                       expression : ExpressionNode,
                       override val idRef : String,
                       override val children: Seq[Node] = Seq[Node](),
                       override val decorations: Map[String,String] = Map()
                     ) extends Node(idRef,children,decorations) {
  override def copy(
                     children: Seq[Node]=children,
                     decoratingAttributeMap : Map[String,String]=decorations
                   ): Node = Bind(expression,idRef,children,decoratingAttributeMap)

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
  implicit val rw: OptionPickler.ReadWriter[ExpressionNode] = OptionPickler.ReadWriter.merge(
/*      ConditionalOrExpression.rw,
      ConditionalAndExpression.rw,
      ValueLogical.rw,
      relationExpression.rw,
      NumericExpression.rw,
      AdditiveExpression.rw,
      MultiplicativeExpression.rw,
      UnaryExpression.rw,*/
      PrimaryExpression.rw
    )
}


sealed abstract class ExpressionNode(
                             override val idRef : String,
                             override val children: Seq[Node],
                             override val decorations: Map[String,String]
                           ) extends Node(idRef,children,decorations)

/*
sealed abstract class ConditionalOrExpression(
                                               val listAndExpression : Seq[ConditionalAndExpression],
                                               override val idRef : String,
                                               override val children: Seq[Node],
                                               override val decoratingAttributeMap: Map[String,String]
                                             ) extends ExpressionNode(idRef,children,decoratingAttributeMap)

sealed abstract class ConditionalAndExpression(
                                               val listValueLogical : Seq[ValueLogical],
                                               override val idRef : String,
                                               override val children: Seq[Node],
                                               override val decoratingAttributeMap: Map[String,String]
                                             ) extends ExpressionNode(idRef,children,decoratingAttributeMap)

sealed abstract class ValueLogical(
                                                val relationExpression : relationExpression,
                                                override val idRef : String,
                                                override val children: Seq[Node],
                                                override val decoratingAttributeMap: Map[String,String]
                                  ) extends ExpressionNode(idRef,children,decoratingAttributeMap)

sealed trait OpNumericExpression

class EqualNumericExpression extends OpNumericExpression // =
class DiffNumericExpression extends OpNumericExpression // !=
class InfNumericExpression extends OpNumericExpression // <
class SupNumericExpression extends OpNumericExpression // >
class InfEqualNumericExpression extends OpNumericExpression // <=
class SupEqualNumericExpression extends OpNumericExpression // >=
// note : NOT and IN not implemented

sealed abstract class relationExpression(
                                          val ne : NumericExpression,
                                          val listNextOpExp : Seq[(OpNumericExpression,NumericExpression)],
                                          override val idRef : String,
                                          override val children: Seq[Node],
                                          override val decoratingAttributeMap: Map[String,String]
                                        ) extends ExpressionNode(idRef,children,decoratingAttributeMap)

sealed abstract class NumericExpression(
                                          val exp : AdditiveExpression,
                                          override val idRef : String,
                                          override val children: Seq[Node],
                                          override val decoratingAttributeMap: Map[String,String]
                                        ) extends ExpressionNode(idRef,children,decoratingAttributeMap)

sealed trait OpMultiplicativeExpression

class AddMultiplicativeExpression(
                                   val v : MultiplicativeExpression) extends OpMultiplicativeExpression // '+' MultiplicativeExpression

class MinusMultiplicativeExpression( val v : MultiplicativeExpression) extends OpMultiplicativeExpression //  '-' MultiplicativeExpression

// ( NumericLiteralPositive | NumericLiteralNegative )  '*' UnaryExpression
//l numeric
class MulMultiplicativeExpression[T](
                                      val l : Literal[T],
                                      val v : MultiplicativeExpression,
                                      val u: UnaryExpression) extends OpMultiplicativeExpression
// ( NumericLiteralPositive | NumericLiteralNegative )  '*' UnaryExpression
class DivMultiplicativeExpression[T](
                                      val l : Literal[T],
                                      val v : MultiplicativeExpression,
                                      val u: UnaryExpression) extends OpMultiplicativeExpression


sealed abstract class AdditiveExpression(
                                          val exp : MultiplicativeExpression,
                                          val listNextOpExp : Seq[(OpMultiplicativeExpression,NumericExpression)],
                                          override val idRef : String,
                                          override val children: Seq[Node],
                                          override val decoratingAttributeMap: Map[String,String]
                                        ) extends ExpressionNode(idRef,children,decoratingAttributeMap)

sealed abstract class MultiplicativeExpression (
                                                 val exp : MultiplicativeExpression,
                                                 override val idRef : String,
                                                 override val children: Seq[Node],
                                                 override val decoratingAttributeMap: Map[String,String]
                                               )extends ExpressionNode(idRef,children,decoratingAttributeMap)

sealed trait OpUnaryExpression
class NotUnaryExpression extends OpUnaryExpression // =
class AddUnaryExpression extends OpUnaryExpression // !=
class MinusUnaryExpression extends OpUnaryExpression // <

sealed abstract class UnaryExpression(
                                        val op : Option[OpUnaryExpression],
                                        val p : PrimaryExpression,
                                        override val idRef : String,
                                        override val children: Seq[Node],
                                        override val decoratingAttributeMap: Map[String,String]
                                      ) extends ExpressionNode(idRef,children,decoratingAttributeMap)
*/

object PrimaryExpression {
  implicit val rw: OptionPickler.ReadWriter[PrimaryExpression] = OptionPickler.ReadWriter.merge(
      SparqlDefinitionExpression.rw,
      FunctionStringNode.rw,
      FunctionNumericNode.rw,
      FunctionUriNode.rw,
      BuiltInCallNode.rw
    )
}


sealed abstract class PrimaryExpression(
                                         override val idRef : String,
                                         override val children: Seq[Node],
                                         override val decorations: Map[String,String]
                                       ) extends ExpressionNode(idRef,children,decorations)


object SparqlDefinitionExpression {
  implicit val rw: OptionPickler.ReadWriter[SparqlDefinitionExpression] = OptionPickler.macroRW
}

final case class SparqlDefinitionExpression(
                                             sd : SparqlDefinition,
                                             override val idRef : String,
                                             override val children: Seq[Node]=Seq[Node](),
                                             override val decorations: Map[String,String]= Map()
                                           ) extends PrimaryExpression(idRef,children,decorations) {
  override def copy(
                     children: Seq[Node]=children,
                     decoratingAttributeMap : Map[String,String]=decorations
                   ): Node = SparqlDefinitionExpression(sd,idRef,children,decoratingAttributeMap)
}

object FunctionStringNode {
  implicit val rw: OptionPickler.ReadWriter[FunctionStringNode] = OptionPickler.ReadWriter.merge(
    SubStr.rw,
    Replace.rw
  )
}

sealed abstract class FunctionStringNode(
                                          override val idRef : String,
                                          override val children: Seq[Node],
                                          override val decorations: Map[String,String]
                                        ) extends PrimaryExpression(idRef,children,decorations)

object SubStr {
  implicit val rw: OptionPickler.ReadWriter[SubStr] = OptionPickler.macroRW
}

final case class SubStr(
                  start : SparqlDefinition,
                  length : SparqlDefinition,
                  override val idRef : String,
                  override val children: Seq[Node]=Seq[Node](),
                  override val decorations: Map[String,String]=Map()
                       ) extends FunctionStringNode(idRef,children,decorations) {
  override def copy(
                     children: Seq[Node]=children,
                     decoratingAttributeMap : Map[String,String]=decorations
                   ): Node = SubStr(start,length,idRef,children,decoratingAttributeMap)
}

object Replace {
  implicit val rw: OptionPickler.ReadWriter[Replace] = OptionPickler.macroRW
}


final case class Replace(
                    pattern : SparqlDefinition,
                    replacement : SparqlDefinition,
                    flags : SparqlDefinition,
                    override val idRef : String,
                    override val children: Seq[Node]=Seq[Node](),
                    override val decorations: Map[String,String]=Map()
                        ) extends FunctionStringNode(idRef,children,decorations) {
  override def copy(
                     children: Seq[Node]=children,
                     decoratingAttributeMap : Map[String,String]=decorations
                   ): Node = Replace(pattern,replacement,flags,idRef,children,decoratingAttributeMap)
}

object FunctionNumericNode {
  implicit val rw: OptionPickler.ReadWriter[FunctionNumericNode] = OptionPickler.ReadWriter.merge(
    Abs.rw,
    Round.rw,
    Ceil.rw,
    Floor.rw,
    Rand.rw
  )
}

sealed abstract class FunctionNumericNode(
                                           override val idRef : String,
                                           override val children: Seq[Node],
                                           override val decorations: Map[String,String]
                                         ) extends PrimaryExpression(idRef,children,decorations)

object Abs {
  implicit val rw: OptionPickler.ReadWriter[Abs] = OptionPickler.macroRW
}


final case class Abs(
                      override val idRef : String,
                      override val children: Seq[Node]=Seq[Node](),
                      override val decorations: Map[String,String]= Map()
                    ) extends FunctionNumericNode(idRef,children,decorations) {
  override def copy(
                     children: Seq[Node]=children,
                     decoratingAttributeMap : Map[String,String]=decorations
                   ): Node = Abs(idRef,children,decoratingAttributeMap)
}

object Round {
  implicit val rw: OptionPickler.ReadWriter[Round] = OptionPickler.macroRW
}


final case class Round(
                        override val idRef : String,
                        override val children: Seq[Node]=Seq[Node](),
                        override val decorations: Map[String,String]= Map()
                      ) extends FunctionNumericNode(idRef,children,decorations) {
  override def copy(
                     children: Seq[Node]=children,
                     decoratingAttributeMap : Map[String,String]=decorations
                   ): Node = Round(idRef,children,decoratingAttributeMap)
}

object Ceil {
  implicit val rw: OptionPickler.ReadWriter[Ceil] = OptionPickler.macroRW
}

final case class Ceil(
                       override val idRef : String,
                       override val children: Seq[Node]=Seq[Node](),
                       override val decorations: Map[String,String]= Map()
                     ) extends FunctionNumericNode(idRef,children,decorations) {
  override def copy(
                     children: Seq[Node]=children,
                     decoratingAttributeMap : Map[String,String]=decorations
                   ): Node = Ceil(idRef,children,decoratingAttributeMap)
}

object Floor {
  implicit val rw: OptionPickler.ReadWriter[Floor] = OptionPickler.macroRW
}

final case class Floor(
                  override val idRef : String,
                  override val children: Seq[Node]=Seq[Node](),
                  override val decorations: Map[String,String]= Map()
                ) extends FunctionNumericNode(idRef,children,decorations) {
  override def copy(
                     children: Seq[Node]=children,
                     decoratingAttributeMap : Map[String,String]=decorations
                   ): Node = Floor(idRef,children,decoratingAttributeMap)
}

object Rand {
  implicit val rw: OptionPickler.ReadWriter[Rand] = OptionPickler.macroRW
}

final case class Rand(
                 override val idRef : String,
                 override val children: Seq[Node]=Seq[Node](),
                 override val decorations: Map[String,String]= Map()
               ) extends FunctionNumericNode(idRef,children,decorations) {
  override def copy(
                     children: Seq[Node]=children,
                     decoratingAttributeMap : Map[String,String]=decorations
                   ): Node = Rand(idRef,children,decoratingAttributeMap)
}
/* ---------------------------------------------------------------------------------------------------  */

sealed abstract class FunctionUriNode(
                                       override val idRef : String,
                                       override val children: Seq[Node],
                                       override val decorations: Map[String,String]
                                     ) extends PrimaryExpression(idRef,children,decorations)

object FunctionUriNode {
  implicit val rw: OptionPickler.ReadWriter[FunctionUriNode] = OptionPickler.ReadWriter.merge(
    Datatype.rw
  )
}

object Datatype {
  implicit val rw: OptionPickler.ReadWriter[Datatype] = OptionPickler.macroRW
}

final case class Datatype(
                           override val idRef : String,
                           override val children: Seq[Node]=Seq[Node](),
                           override val decorations: Map[String,String]= Map()
                         ) extends FunctionUriNode(idRef,children,decorations) {
  override def copy(
                     children: Seq[Node]=children,
                     decoratingAttributeMap : Map[String,String]=decorations
                   ): Node = Datatype(idRef,children,decoratingAttributeMap)
}

/* ---------------------------------------------------------------------------------------------------  */


object AggregateNode {
  implicit val rw: OptionPickler.ReadWriter[AggregateNode] = OptionPickler.ReadWriter.merge(
    Count.rw
  )
}

object ProjectionExpression {
  implicit val rw: OptionPickler.ReadWriter[ProjectionExpression] = OptionPickler.macroRW
}

final case class ProjectionExpression(
                                 `var` : QueryVariable,
                                 expression : AggregateNode,
                                 override val idRef : String,
                                 override val children: Seq[Node] = Seq[Node](),
                                 override val decorations: Map[String,String] = Map()
                        ) extends Node(idRef,children,decorations) {
  override def copy(
                     children: Seq[Node]=children,
                     decoratingAttributeMap : Map[String,String]=decorations
                   ): Node = ProjectionExpression(`var`,expression,idRef,children,decoratingAttributeMap)

  override def accept(n: Node): Boolean = false
}

/*
 * ------------------------------------  Aggregate
 */
sealed abstract class AggregateNode(
                                     override val idRef : String,
                                     override val children: Seq[Node],
                                     override val decorations: Map[String,String]
                                   ) extends Node(idRef,children,decorations) {
  override def accept(n: Node): Boolean = false
}

object Count {
  implicit val rw: OptionPickler.ReadWriter[Count] = OptionPickler.macroRW
}

final case class Count(
                 listVarToCount : Seq[QueryVariable],
                 distinct : Boolean = false,
                 override val idRef : String,
                 override val children: Seq[Node] = Seq[Node](),
                 override val decorations: Map[String,String] = Map()
                 ) extends AggregateNode(idRef,children,decorations) {
  override def copy(
                     children: Seq[Node]=children,
                     decoratingAttributeMap : Map[String,String]=decorations
                   ): Node = Count(listVarToCount,distinct,idRef,children,decoratingAttributeMap)
}

object BuiltInCallNode {
  implicit val rw: OptionPickler.ReadWriter[BuiltInCallNode] = OptionPickler.ReadWriter.merge(
    Str.rw,
    Lang.rw,
    LangMatches.rw
  )
}

/*
 * ------------------------------------  BuiltInCallNode
 */

sealed abstract class BuiltInCallNode(
                                       override val idRef : String,
                                       override val children: Seq[Node],
                                       override val decorations: Map[String,String]
                                     ) extends PrimaryExpression(idRef,children,decorations){
  override def accept(n: Node): Boolean = false
}

object Str {
  implicit val rw: OptionPickler.ReadWriter[Str] = OptionPickler.macroRW
}

final case class Str(
                      term: SparqlDefinition,
                      override val idRef : String,
                      override val children: Seq[Node] = Seq[Node](),
                      override val decorations: Map[String,String] = Map()
                    ) extends BuiltInCallNode(idRef,children,decorations) {
  override def copy(
                     children: Seq[Node]=children,
                     decoratingAttributeMap : Map[String,String]=decorations
                   ): Node = Str(term,idRef,children,decoratingAttributeMap)
}

object Lang {
  implicit val rw: OptionPickler.ReadWriter[Lang] = OptionPickler.macroRW
}

final case class Lang(
                       term: SparqlDefinition,
                       override val idRef : String,
                       override val children: Seq[Node] = Seq[Node](),
                       override val decorations: Map[String,String] = Map()
                        ) extends BuiltInCallNode(idRef,children,decorations) {
  override def copy(
                     children: Seq[Node]=children,
                     decoratingAttributeMap : Map[String,String]=decorations
                   ): Node = Lang(term,idRef,children,decoratingAttributeMap)
}

object LangMatches {
  implicit val rw: OptionPickler.ReadWriter[LangMatches] = OptionPickler.macroRW
}

final case class LangMatches(
                              term: SparqlDefinition,
                              override val idRef : String,
                              override val children: Seq[Node] = Seq[Node](),
                              override val decorations: Map[String,String] = Map()
                            ) extends BuiltInCallNode(idRef,children,decorations) {
  override def copy(
                     children: Seq[Node]=children,
                     decoratingAttributeMap : Map[String,String]=decorations
                   ): Node = LangMatches(term,idRef,children,decoratingAttributeMap)
}
