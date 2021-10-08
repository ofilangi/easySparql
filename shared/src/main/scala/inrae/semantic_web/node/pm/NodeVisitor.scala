package inrae.semantic_web.node.pm

import inrae.semantic_web.node._
import inrae.semantic_web.rdf.QueryVariable

object NodeVisitor  {

  /**
   * Find a Node with the reference id
   * @param ref
   * @param n
   * @return
   */
  def getNodeWithRef( ref : String, n: Node ) : Array[RdfNode] = n match {
            case node : RdfNode  if node.reference() == ref => Array[RdfNode](node)
            case _   => n.children.toArray.flatMap( child => getNodeWithRef( ref, child ))
  }


  def getNodeUsingQueryVariable( ref : String, n : Node ): Seq[Node] = ( n match {
    case node: URIRdfNode if node.term == QueryVariable(ref) => Seq(node)
    case node: Value if node.term == QueryVariable(ref) => Seq(node)
    case lNodes: ListValues if lNodes.terms.contains(QueryVariable(ref)) => Seq(lNodes)
    case _ => Seq()
  } ) ++ n.children.flatMap(getNodeUsingQueryVariable(ref, _))

  /**
   * Get All ancestors croissant order
   * @param childRef
   * @return
   */
  def getAncestorsRef( childRef: String, n : Node ) : Seq[String] =  n match {
    case node : RdfNode  if (node.reference() == childRef) => Seq(childRef)
    case node   => n.children.flatMap( child => {
      getAncestorsRef( childRef, child ) match {
        case listAncestor if listAncestor.length>0 => Seq(node.idRef) ++ listAncestor
        case _ => Seq()
      }
    })
  }

  /**
   * Give all variables using in the query
   * @param n
   * @return
   */
  def getAllAncestorsRef( n : Node ) : Seq[String] = n match {
    case node : Root => {
      Seq(node.reference())++:
        node.children.flatMap(getAllAncestorsRef(_)) ++:
        node.lBindNode.flatMap(getAllAncestorsRef(_))
    }
    case node : RdfNode  => Seq(node.reference()) ++: node.children.flatMap(getAllAncestorsRef(_))
    case node : Bind => Seq(node.reference()) ++: node.children.flatMap(getAllAncestorsRef(_))
    case node : FilterNode => Seq(node.reference()) ++: node.children.flatMap(getAllAncestorsRef(_))
    case _ => Seq()
  }

  /**
   * Apply a Visitor on the Node and the children element recursively
   * @param n
   * @param deep
   * @param visitor
   * @tparam A
   * @return
   */

  def map[A](  n : Root, deep : Integer , visitor : (Node,Integer) => A )  : Seq[A] =
    Seq(visitor(n,deep)) ++:
      n.children.flatMap( nc => NodeVisitor.map(nc, deep+1, visitor) ) ++:
      n.lBindNode.flatMap( nc => NodeVisitor.map(nc, deep+1, visitor) ) ++:
      n.lDatatypeNode.flatMap( nc => NodeVisitor.map(nc, deep+1, visitor) ) ++:
      n.lSolutionSequenceModifierNode.flatMap( nc => NodeVisitor.map(nc, deep+1, visitor) ) ++:
      n.lSourcesNodes.flatMap( nc => NodeVisitor.map(nc, deep+1, visitor) )

  def map[A](  n : Node, deep : Integer , visitor : (Node,Integer) => A )  : Seq[A] =
    Seq(visitor(n,deep)) ++: n.children.flatMap( nc => NodeVisitor.map(nc, deep+1, visitor) )

}
