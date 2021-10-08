package inrae.semantic_web.node.pm

import inrae.semantic_web.SWDiscovery
import inrae.semantic_web.node.{FilterNode, Something}
import inrae.semantic_web.rdf.Anonymous

case object ManageSWConsistency {

  def manage(sw: SWDiscovery) : SWDiscovery = fillSomethingAlone(sw)

  /* is something is defined but never used , something should be anything */
  def fillSomethingAlone ( sw : SWDiscovery ) : SWDiscovery = {

    /* get All ref something without children/ref */
    sw.rootNode.getChild[Something](Something("")).distinct
      .flatMap {
        case something if !something.children.exists {
          case _: FilterNode => false
          case _ => true
        } =>
          println("HIIIIIIIIIIIIIIIIIIIIIII")
          println(something)
          /* check if no reference */
          if ( NodeVisitor.getNodeUsingQueryVariable(something.idRef, sw.rootNode).nonEmpty ) {
            None
          } else {
            Some(something.idRef)
          }
        case _ => None

      }
  }.foldLeft( sw ) {
    (disco : SWDiscovery , idRef : String ) => {
      disco.focus(idRef).isSubjectOf(Anonymous()).focus(idRef).isObjectOf(Anonymous())
    }
  }
}
