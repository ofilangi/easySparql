package fr.inrae.metabohub.semantic_web
import fr.inrae.metabohub.semantic_web.node.{Root, pm}
import wvlet.log.Logger.rootLogger._

object SparqlQueryBuilder {
  /**
   *
   * @param n : Node to transform
   * @param refToIdentifier : mapping user variable name to internal variable name
   * @param limit : upper bound on the number of solutions returned
   * @param offset : solution are generated after this offset
   * @return
   */
  def baseQuery(n: Root) : String = {
    (pm.SparqlGenerator.solutionSequenceModifierStart(n)) + "\n" +
      pm.SparqlGenerator.body(n) + "\n" +
      pm.SparqlGenerator.solutionSequenceModifierEnd(n)
  }

  /**
   *
   *
   * @param n : Node to transform
   * @param refToIdentifier : mapping user variable name to internal variable name
   * @param listVariables : selected variable
   * @param limit : upper bound on the number of solutions returned
   * @param offset : solution are generated after this offset
   * @return
   */
  def selectQueryString(n: Root): String = {
    debug(" -- selectQueryString -- ")

    (n.directives.map( x => x ).mkString(" \n") + "\n" +
      pm.SparqlGenerator.prefixes(n.prefixes) + "\n" +
      baseQuery(n)).replace("\n\n","\n")
  }

}
