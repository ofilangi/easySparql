package fr.inrae.metabohub.semantic_web

import scala.scalajs._
import scala.scalajs.js.JSConverters._
import scala.scalajs.js.annotation.{JSExport, JSExportTopLevel}

@JSExportTopLevel("ProjectionExpressionIncrement")
case class ProjectionExpressionIncrementJs(swf: SWTransactionJs,`var` : String) {
  @JSExport
  def count(ref: js.Array[String], distinct: Boolean=false) : SWTransactionJs = SWTransactionJs(swf.transaction.aggregate(`var`).count(ref.toSeq,distinct))
}
