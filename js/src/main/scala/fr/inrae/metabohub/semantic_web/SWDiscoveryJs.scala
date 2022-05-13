package fr.inrae.metabohub.semantic_web

import fr.inrae.metabohub.semantic_web.node.Node
import fr.inrae.metabohub.semantic_web.rdf.{IRI, SparqlDefinition, URI}
import fr.inrae.metabohub.semantic_web.configuration._
import fr.inrae.metabohub.semantic_web.exception._
import fr.inrae.metabohub.semantic_web.view.HtmlView

import scala.scalajs._
import scala.scalajs.js.{Dynamic, JSON}
import scala.scalajs.js.JSConverters._
import scala.scalajs.js.annotation.{JSExport, JSExportTopLevel}

@JSExportTopLevel(name="SWDiscovery")
case class SWDiscoveryJs(
                          config: SWDiscoveryConfiguration=SWDiscoveryConfiguration(),
                          swArg: SWDiscovery = null
                        ) {
  implicit val ec: scala.concurrent.ExecutionContext = scala.concurrent.ExecutionContext.global

  val sw: SWDiscovery = swArg match {
    case null => SWDiscovery(config)
    case v =>v
  }

  def toIRI(any:Any) : IRI = any match {
    case v : IRI => v
    case v : URI => IRI(v.sparql)
    case string : String => string
    case _ => throw SWDiscoveryException(any.toString + " can not be cast into IRI.")
  }

  def toURI(any:Any): URI = any match {
    case v : URI => v
    case string : String => string
    case _ => throw SWDiscoveryException(any.toString + " can not be cast into IRI.")
  }

  @JSExport
  val filter: FilterIncrementJs = FilterIncrementJs(this)

  @JSExport
  def helper(regex : String = ""): SWDiscoveryJs = { HtmlView(sw,regex) ; SWDiscoveryJs(config,sw) }

  @JSExport
  def bind(`var` : String) : BindIncrementJs = BindIncrementJs(this,`var`)

  @JSExport
  def finder :SWDiscoveryHelperJs = SWDiscoveryHelperJs(sw)

  @JSExport
  def setConfig(newConfig : SWDiscoveryConfiguration) : SWDiscoveryJs = SWDiscoveryJs(newConfig,sw.setConfig(newConfig))

  @JSExport
  def getConfig() : SWDiscoveryConfiguration = sw.getConfig

  @JSExport
  def focus(ref : String) : SWDiscoveryJs = SWDiscoveryJs(config,sw.focus(ref))

  @JSExport
  def prefix(short : String, long : Any ) : SWDiscoveryJs = SWDiscoveryJs(config,sw.prefix(short,toIRI(long)))

  @JSExport
  def getPrefix(short : String) : Any = sw.getPrefix(short)

  @JSExport
  def directive(directive : String) : SWDiscoveryJs = SWDiscoveryJs(config,sw.directive(directive))

  @JSExport
  def graph(graph : Any) : SWDiscoveryJs = SWDiscoveryJs(config,sw.graph(toIRI(graph)))

  @JSExport
  def root(): SWDiscoveryJs = SWDiscoveryJs(config,sw.root)

  @JSExport
  def focus() : String = sw.focusNode

  @JSExport
  def namedGraph(graph : Any ) : SWDiscoveryJs = SWDiscoveryJs(config,sw.namedGraph(toIRI(graph)))
  /* start a request */
  @JSExport
  def something( ref : String = sw.getUniqueRef("something") ) : SWDiscoveryJs = SWDiscoveryJs(config,sw.something(ref))

  /* create node which focus is the subject : ?focusId <uri> ?target */
  @JSExport
  def isSubjectOf( uri : Any , ref : String = sw.getUniqueRef("object") ) : SWDiscoveryJs =
    SWDiscoveryJs(config,sw.isSubjectOf(uri,ref))

  /* create node which focus is the subject : ?focusId <uri> ?target */
  @JSExport
  def isObjectOf( uri : Any , ref : String = sw.getUniqueRef("subject") ) : SWDiscoveryJs = SWDiscoveryJs(config,sw.isObjectOf(uri,ref))

  @JSExport
  def isLinkTo( uri : Any , ref : String = sw.getUniqueRef("linkTo") ) : SWDiscoveryJs = SWDiscoveryJs(config,sw.isLinkTo(uri,ref))

  @JSExport
  def isA( term : Any ) : SWDiscoveryJs = SWDiscoveryJs(config,sw.isA(term))

  @JSExport
  def isLinkFrom( uri : String , ref : String = sw.getUniqueRef("linkFrom") ) : SWDiscoveryJs = SWDiscoveryJs(config,sw.isLinkFrom(uri,ref))
  /* set */
  @JSExport
  def set( term : Any ) : SWDiscoveryJs = SWDiscoveryJs(config,sw.set(term))

  @JSExport
  def setList( terms : Any* ) : SWDiscoveryJs = SWDiscoveryJs(config,sw.setList(terms.map(SparqlDefinition.fromAny)))

  @JSExport
  def datatype( uri : Any, ref : String ) : SWDiscoveryJs = SWDiscoveryJs(config,sw.datatype(toURI(uri),ref))

  @JSExport
  def remove( focus : String ) : SWDiscoveryJs = SWDiscoveryJs(config,sw.remove(focus))

  @JSExport
  def console() : SWDiscoveryJs = SWDiscoveryJs(config,sw.console)

  @JSExport
  def sparql() : String = sw.sparql

  @JSExport
  def sparql_get() : String = sw.sparql_get

  @JSExport
  def sparql_curl() : String = sw.sparql_curl

  @JSExport
  def getSerializedString(): String = sw.getSerializedString

  @JSExport
  def setSerializedString(query : String): SWDiscoveryJs = SWDiscoveryJs(config,sw.setSerializedString(query))

  @JSExport
  def browse[A](visitor : js.Function2[Dynamic, Integer,A] ) : js.Array[A] = {
    val visitor2 : (Node, Integer) => A = (n, p) => {
      visitor(JSON.parse(OptionPickler.write(n)),p)
    }
    sw.browse(visitor2).toJSArray
  }
  
  @JSExport
  def setDecoration(key : String, value : String) : SWDiscoveryJs = SWDiscoveryJs(config,sw.setDecoration(key,value))

  @JSExport
  def getDecoration(key : String) : String = sw.getDecoration(key)

  @JSExport
  def select(lRef: String*): SWTransactionJs = SWTransactionJs(sw.select(lRef))

  @JSExport
  def select(lRef: js.Array[String], limit : Int = 0, offset : Int = 0): SWTransactionJs =
    SWTransactionJs(sw.select(lRef.toSeq,limit,offset))

  @JSExport
  def selectByPage(lRef: js.Array[String])  : js.Promise[(Int,js.Array[SWTransactionJs])] =
    sw.selectByPage(lRef.toSeq).map( res => {
      val n : Int = res._1
      val l : Seq[SWTransaction] = res._2
      (n, l.map(swt => SWTransactionJs(swt) ).toJSArray) }).toJSPromise

  @JSExport
  def selectByPage(lRef: String*)  : js.Promise[(Int,js.Array[SWTransactionJs])] =
    selectByPage(lRef.toJSArray)

  @JSExport
  def selectDistinctByPage(lRef: js.Array[String])  : js.Promise[(Int,js.Array[SWTransactionJs])] =
    sw.selectDistinctByPage(lRef.toSeq).map( res => {
    val n : Int = res._1
    val l : Seq[SWTransaction] = res._2
    (n, l.map(swt => SWTransactionJs(swt) ).toJSArray) }).toJSPromise

  @JSExport
  def selectDistinctByPage(lRef: String*)  : js.Promise[(Int,js.Array[SWTransactionJs])] =
    selectDistinctByPage(lRef.toJSArray)
}
