package inrae.semantic_web.internal.pm

import inrae.semantic_web.internal.{Node, Root}

case object RemoveNode {
  def run ( root: Root, focus : String ) : Root = {
    if ( root.idRef == focus ) {
      Root(focus)
    } else {
      root.copy(root.children.map( run(_,focus) ).filter( n => n.idRef != focus)).asInstanceOf[Root]
    }
  }

  def run ( node: Node, focus : String ) : Node =
    node.copy(node.children.map( run(_,focus) ).filter( n => n.idRef != focus))

}
