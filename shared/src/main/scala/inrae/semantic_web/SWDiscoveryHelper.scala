package inrae.semantic_web

import inrae.semantic_web.rdf.{QueryVariable, SparqlBuilder, URI}
import wvlet.log.Logger.rootLogger.debug

import scala.concurrent.Future

case class SWDiscoveryHelper(sw : SWDiscovery) {
  implicit val ec: scala.concurrent.ExecutionContext = scala.concurrent.ExecutionContext.global

  val regex_avoid_prefix : String = "^("+ List(
    "http://www.openlinksw.com/schemas/virtrdf#",
    "http://www.w3.org/2002/07/owl#",
    "http://www.w3.org/2000/01/rdf-schema#",
    "http://www.w3.org/1999/02/22-rdf-syntax-ns"
  ).mkString("|") + ")"

  def count(lRef : Seq[String],distinct : Boolean = false) : Future[Int] = {
    sw
      .transaction
      .projection
      .aggregate("count")
      .count(lRef,distinct)
      .console
      .commit()
      .raw
      .map( json => {
        SparqlBuilder.createLiteral(json("results")("bindings")(0)("count")).toInt
      })
  }

  /**
   * Discovery search functionalities
   *
   */

  def classes(regex : String="", motherClass: URI = URI(""), page : Int =0) : Future[Seq[URI]] = {
    debug(" -- findClasses -- ")
    val query = (motherClass match {
      case uri : URI if uri == URI("")  => sw.isSubjectOf(URI("a"),"_esp___type")
      case _ : URI =>  sw.isSubjectOf(URI("a"),"_esp___type")
        .isSubjectOf(URI("a"))
        .set(motherClass)
    }).focus("_esp___type")
      .filter.not.regex(regex_avoid_prefix)

    (if ( regex.trim != "")
          query.focus("_esp___type").filter.regex(regex)
      else
        query)
      .selectByPage(List("_esp___type"))
      .flatMap(  v => {
        val futurePages : Seq[SWTransaction] = v._2

        if ( futurePages.length > page ) {
          futurePages(page)
            .commit()
            .raw
            .map( json => {
              json("results")("bindings").arr.map(
                row => SparqlBuilder.createUri(row("_esp___type"))
              ).toSeq.distinct
            })
        } else {
          Future { Seq[URI]() }
        }
      })
  }

  def properties(regex : String="", motherClassProperties: URI = URI(""), kind : String, page : Int) : Future[Seq[URI]] = {
    debug(" -- findProperties -- ")

    /* inherited from something ??? */
    val state = if (motherClassProperties != URI("")) {
      sw.root
        .something("_esp___type")
        .focus(sw.focusNode)
        .isLinkTo(QueryVariable("_esp___type"),"_esp___property").isSubjectOf(URI("a"))
        .set(motherClassProperties)
    } else {
      sw.root
        .something("_esp___type")
        .focus(sw.focusNode)
        .isLinkTo(QueryVariable("_esp___type"),"_esp___property")
    }

    /* object or datatype properties owl def. */
    val query = ( kind  match {
      case "objectProperty" => state.focus("_esp___type").filter.isUri
      case "datatypeProperty" => state.focus("_esp___type").filter.isLiteral
      case _ => state
    }).focus("_esp___property")
      .filter.not.regex(regex_avoid_prefix)

    (if ( regex.trim != "")
      query.focus("_esp___property")
        .filter.regex(regex)
    else
      query)
      .selectByPage(List("_esp___property"))
      .flatMap(  v => {
        val futurePages : Seq[SWTransaction] = v._2
        if ( futurePages.length > page ) {
          futurePages(page)
            .distinct
            .commit()
            .raw
            .map( json => {
              json("results")("bindings").arr.map(
                row => {
                  SparqlBuilder.createUri(row("_esp___property")) }
              ).toSeq.distinct
            })
        } else {
          Future { Seq[URI]() }
        }
      })


  }

  def objectProperties(regex : String="", motherClassProperties: URI = URI(""), page : Int = 0 ) : Future[Seq[URI]] = {
    debug(" -- findObjectProperties -- ")
    properties(regex,motherClassProperties,"objectProperty",page)
  }

  def datatypeProperties(regex : String="", motherClassProperties: URI = URI(""), page : Int = 0 ) : Future[Seq[URI]] = {
    debug(" -- findDatatypeProperties -- ")
    properties(regex,motherClassProperties,"datatypeProperty",page)
  }

  /* backward */
  def subjectProperties(regex : String="", motherClassProperties: URI = URI(""), page : Int = 0 ) : Future[Seq[URI]] = {
    debug(" -- findSubjectProperties -- ")

    val query = (if (motherClassProperties != URI("")) {
      sw.root
        .something("_esp___type")
        .focus(sw.focusNode)
        .isLinkFrom(QueryVariable("_esp___type"),"_esp___property").isSubjectOf(URI("a"))
        .set(motherClassProperties)
    } else {
      sw.root
        .something("_esp___type")
        .focus(sw.focusNode)
        .isLinkFrom(QueryVariable("_esp___type"),"_esp___property")
    }).focus("_esp___property")
      .filter.not.regex(regex_avoid_prefix)

    (if ( regex.trim != "")
      query.focus("_esp___property").filter.regex(regex)
    else
      query)

      .selectByPage(List("_esp___property"))
      .flatMap(  v => {
        val futurePages : Seq[SWTransaction] = v._2
        if ( futurePages.length > page ) {
          futurePages(page)
            .distinct
            .commit()
            .raw
            .map( json => {
              json("results")("bindings").arr.map(
                row => {
                  SparqlBuilder.createUri(row("_esp___property")) }
              ).toSeq.distinct
            })
        } else {
          Future { Seq[URI]() }
        }
      })
  }

}
