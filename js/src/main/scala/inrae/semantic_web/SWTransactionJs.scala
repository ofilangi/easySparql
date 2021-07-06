package inrae.semantic_web

import scala.scalajs.js
import scala.scalajs.js.JSConverters.JSRichFutureNonThenable
import scala.scalajs.js.Promise
import scala.scalajs.js.annotation.{JSExport, JSExportTopLevel}

@JSExportTopLevel(name="SWDiscoveryTransaction")
case class SWTransactionJs(transaction : SWTransaction) {
  implicit val ec: scala.concurrent.ExecutionContext = scala.concurrent.ExecutionContext.global

  @JSExport
  def progression(  callBack  : js.Function1[Double,Unit]  ): Unit = {
    transaction.progression(callBack)
  }

  @JSExport
  def requestEvent(callBack  : js.Function1[String,Unit]  ): Unit = transaction.requestEvent(callBack)

  @JSExport
  def abort(): Unit = transaction.abort()

  @JSExport
  def commit() : SWTransactionJs = SWTransactionJs(transaction.commit())

  @JSExport
  def raw() : Promise[Dynamic] = {
    transaction.raw.map(x => scala.scalajs.js.JSON.parse(x.toString())).toJSPromise
  }

  @JSExport
  def projection( lRef: Seq[String]=Seq() )  : SWTransactionJs = SWTransactionJs(transaction.projection(lRef))

  @JSExport
  def aggregate(`var` : String) : ProjectionExpressionIncrementJs = ProjectionExpressionIncrementJs(this,`var`)

  @JSExport
  def distinct  : SWTransactionJs = SWTransactionJs(transaction.distinct)

  @JSExport
  def reduced  : SWTransactionJs = SWTransactionJs(transaction.reduced)

  @JSExport
  def limit( value : Int )  : SWTransactionJs = SWTransactionJs(transaction.limit(value))

  @JSExport
  def offset( value : Int )  : SWTransactionJs = SWTransactionJs(transaction.offset(value))

  @JSExport
  def orderByAsc( ref: String )  : SWTransactionJs = SWTransactionJs(transaction.orderByAsc(ref))

  @JSExport
  def orderByAsc( lRef: Seq[String] )  : SWTransactionJs = SWTransactionJs(transaction.orderByAsc(lRef))

  @JSExport
  def orderByDesc( ref: String ) : SWTransactionJs = SWTransactionJs(transaction.orderByDesc(ref))

  @JSExport
  def orderByDesc( lRef: Seq[String] )  : SWTransactionJs = SWTransactionJs(transaction.orderByDesc(lRef))

  @JSExport
  def getSerializedString: String = transaction.getSerializedString

  @JSExport
  def setSerializedString(transaction_string : String): SWTransactionJs =
    SWTransactionJs(transaction.setSerializedString(transaction_string))

  @JSExport
  def console() : SWTransactionJs = SWTransactionJs(transaction.console)
}
