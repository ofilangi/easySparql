package inrae.semantic_web

import inrae.semantic_web.rdf.URI

import scala.scalajs.js
import scala.scalajs.js.Promise
import scala.scalajs.js.annotation.{JSExport, JSExportTopLevel}
import scala.scalajs.js.JSConverters._

@JSExportTopLevel(name="SWDiscoveryHelper")
case class SWDiscoveryHelperJs(sw : SWDiscovery) {
  implicit val ec: scala.concurrent.ExecutionContext = scala.concurrent.ExecutionContext.global

  @JSExport
  def count(lRef: js.Array[String],distinct : Boolean=false): Promise[Int] = { sw.finder.count(lRef.toSeq,distinct).toJSPromise }

  @JSExport
  def classes(regex : String = "",uri:URI = URI(""), page : Int = 0 ): Promise[js.Array[URI]] =
  { sw.finder.classes(regex,uri,page).map(array => array.toJSArray).toJSPromise }

  @JSExport
  def objectProperties(regex : String = "",motherClassProperties: URI = URI(""), page : Int = 0 ) : Promise[js.Array[URI]] = {
    sw.finder.objectProperties(regex,motherClassProperties,page).map(array => array.toJSArray).toJSPromise
  }

  @JSExport
  def datatypeProperties(regex : String = "",motherClassProperties: URI = URI("") , page : Int = 0) : Promise[js.Array[URI]] = {
    sw.finder.datatypeProperties(regex,motherClassProperties,page).map(array => array.toJSArray).toJSPromise
  }

  @JSExport
  def subjectProperties(regex : String = "",motherClassProperties: URI = URI("") , page : Int = 0) : Promise[js.Array[URI]] = {
    sw.finder.subjectProperties(regex,motherClassProperties,page).map(array => array.toJSArray).toJSPromise
  }

}
