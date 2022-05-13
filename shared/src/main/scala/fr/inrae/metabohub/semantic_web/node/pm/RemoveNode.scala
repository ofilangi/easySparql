package fr.inrae.metabohub.semantic_web.node.pm

import fr.inrae.metabohub.semantic_web.node.{Node, Root}

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
