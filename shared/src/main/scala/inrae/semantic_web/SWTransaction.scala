package inrae.semantic_web

import inrae.semantic_web.exception._
import inrae.semantic_web.event._
import inrae.semantic_web.node._
import inrae.semantic_web.rdf.{QueryVariable, SparqlDefinition, URI}
import inrae.semantic_web.sparql.QueryResult
import inrae.semantic_web.strategy._
import upickle.default.{macroRW, read, write, ReadWriter => RW}
import wvlet.log.Logger.rootLogger.{debug, trace}

import scala.concurrent.{Future, Promise}
import scala.util.{Failure, Success, Try}


object SWTransaction {
  implicit val rw: RW[SWTransaction] = macroRW
}

case class SWTransaction(sw : SWDiscovery = SWDiscovery())
    extends Subscriber[DiscoveryRequestEvent,StrategyRequest]
{
  implicit val ec: scala.concurrent.ExecutionContext = scala.concurrent.ExecutionContext.global

  def notify(pub: StrategyRequest, event: DiscoveryRequestEvent) = {
    notify(event)
  }

  private val _prom_raw: Promise[ujson.Value] = Promise[ujson.Value]()
  val raw: Future[ujson.Value] =  _prom_raw.future
  var currentRequestEvent: String = DiscoveryStateRequestEvent.START.toString()

  private var countEvent: Int = 1

  private var _progressionCallBack = Seq[Double => Unit]()

  def progression(  callBack  : Double => Unit  ): SWTransaction = {
    _progressionCallBack = _progressionCallBack :+ callBack
    this
  }

  private var _requestEventCallBack = Seq[String => Unit]()

  def requestEvent(callBack  : String => Unit  ): SWTransaction = {
    _requestEventCallBack = _requestEventCallBack :+ callBack
    this
  }

  def notify(event: DiscoveryRequestEvent): Unit = {
    currentRequestEvent = event.state.toString()
    countEvent = countEvent + 1

    _progressionCallBack.foreach (f => f(DiscoveryStateRequestEvent.getPercentProgression(event.state)))

    _requestEventCallBack.foreach(f => f(currentRequestEvent))
  }

  def abort(): Unit = {

    /*
      http request should be cancelled
    * SWResults should be a publish[DiscoveryCancelEvent] => SW/QueryManager/HttpRequest => Subscriber[DiscoveryCancelEvent,SWResults]
    */
    currentRequestEvent = DiscoveryStateRequestEvent.ABORTED_BY_THE_USER.toString()

    _prom_raw failure(SWDiscoveryException("aborted by the user."))
  }


  def process_datatypes(root: Root,
                        qr : QueryResult,
                        datatypeNode : DatatypeNode,
                        lUris : Seq[SparqlDefinition]) = {
    debug(" -- process_datatypes --")
    val labelProperty = datatypeNode.property.reference()

    lUris.grouped(sw.config.settings.sizeBatchProcessing).toList.map(
      f = lSubUris => {
        trace(" datatypes:" + lSubUris.toString)
        /* request using api */
        SWDiscovery(sw.config)
          .prefixes(root.getPrefixes)
          .something("val_uri")
          .setList(lSubUris.flatMap(
            _ match {
              case uri: URI => Some(uri)
              case _ => None
            }
          ))
          .focusManagement(datatypeNode.property, false)
          .select(List("val_uri", labelProperty))
          .commit()
          .raw
          .map(json => {
            qr.setDatatype(labelProperty, json("results")("bindings").arr.map(rec => {
              rec("val_uri")("value").value.toString -> rec(labelProperty)
            }).toMap)
          })
      })
  }

  def commit() : SWTransaction = {
    notify(DiscoveryRequestEvent(DiscoveryStateRequestEvent.START))

    val lSelectedVariable : Seq[QueryVariable] = sw.rootNode.getChild(Projection(List(),"")).lastOption match {
      case Some(proj) => proj.variables.distinct
      case None => {
        notify(DiscoveryRequestEvent(DiscoveryStateRequestEvent.ERROR_REQUEST_DEFINITION))
        throw SWDiscoveryException("projection/selected required variables are not defined.")
      }
    }

    val lDatatype: Seq[DatatypeNode] =
      sw.rootNode.getChild[DatatypeNode](DatatypeNode("",SubjectOf("",URI("")),"unk"))
        .filter(ld => lSelectedVariable.map(_.name).contains(ld.property.reference()))

    if ( lDatatype.filter( datatypeNode => lSelectedVariable.map(_.name).contains(datatypeNode.refNode) ).length != lDatatype.length )
      {
        notify(DiscoveryRequestEvent(DiscoveryStateRequestEvent.ERROR_REQUEST_DEFINITION))
        throw SWDiscoveryException("The user have to select node of interest before setup a desired datatype ["+lDatatype.map( d=>d.idRef + "->"+d.refNode).mkString(" ,")+"]")
      }


    Try(StrategyRequestBuilder.build(sw.config)) match {
      case Failure(e) => _prom_raw failure (e)
      case Success(driver) => {
        driver.subscribe(this.asInstanceOf[Subscriber[DiscoveryRequestEvent,Publisher[DiscoveryRequestEvent]]])
          driver.execute(this)
          /* manage datatype decoration */
          .map((qr: QueryResult) => {
            notify(DiscoveryRequestEvent(DiscoveryStateRequestEvent.DATATYPE_BUILD))
            /* create an empty set of datatypes */
            qr.json("results").update("datatypes", ujson.Obj())
            trace(qr.json)
            /* manage datatype */
            trace("  lDatatype ====> " + lDatatype.toString())

            Future.sequence(lDatatype.map(datatypeNode => {
              trace("datatype node:" + datatypeNode)

              sw.rootNode.getRdfNode(datatypeNode.refNode) match {
                case Some(_) => {

                  /* find uris value inside results to decorate */
                  val lUris: Seq[SparqlDefinition] =
                    try {
                      qr.getValues(datatypeNode.refNode)
                    } catch {
                      case _: Throwable => {
                        List()
                      }
                    }
                  Future.sequence(process_datatypes(sw.rootNode,qr, datatypeNode, lUris))
                }
                case None => {
                  Future {}
                }
              }
            })) onComplete {
              case Success(_) => {
                notify(DiscoveryRequestEvent(DiscoveryStateRequestEvent.DATATYPE_DONE))
                _prom_raw success qr.json
                notify(DiscoveryRequestEvent(DiscoveryStateRequestEvent.REQUEST_DONE))
              }
              case Failure(e) => {
                _prom_raw failure (e)
              }
            }
          }).recover(exception => {
          _prom_raw failure (exception)
        })
      }
    }
    this
  }

  case class ProjectionExpressionIncrement(v : String) {

    def manage(n:AggregateNode,forward : Boolean = false) : SWTransaction = {
      sw.focusManagement(
        ProjectionExpression(QueryVariable(v),n,sw.getUniqueRef()),false).transaction
    }

    def count(lRef : Seq[String],distinct: Boolean=false) : SWTransaction = manage(Count(lRef.map(QueryVariable(_)),distinct,sw.getUniqueRef()))
  //  def countAll(distinct: Boolean=false) : SWTransaction = manage(CountAll(distinct,sw.getUniqueRef()),true)
  }

  def aggregate(`var` : String) : ProjectionExpressionIncrement = ProjectionExpressionIncrement(`var`)

  def projection  : SWTransaction = {
    /* check if a projection exist or create a new one */
    sw.rootNode.getChild(Projection(Seq(),"")).lastOption match {
      case Some(p) => sw.focus(p.idRef).transaction
      case None => sw.root.focusManagement(Projection(Seq(),sw.getUniqueRef())).transaction
    }
  }

  def projection( lRef: Seq[String] )  : SWTransaction = {

    sw.rootNode.getChild(Projection(Seq(),"")).lastOption match {
      case Some(p) => {
        val listVariable : Seq[QueryVariable] = p.variables ++  lRef.map(QueryVariable(_))
        sw.root.focusManagement(
          Projection(listVariable,p.idRef,p.children))
          .focus(p.idRef).transaction
      }
      case None => {
        sw.root.focusManagement(Projection(lRef.map(QueryVariable(_)),sw.getUniqueRef())).transaction
      }
    }

  }

  def distinct : SWTransaction = sw.root.focusManagement(Distinct(sw.getUniqueRef()), false).transaction

  def reduced : SWTransaction = sw.root.focusManagement(Reduced(sw.getUniqueRef()), false).transaction

  def limit( value : Int ) : SWTransaction = sw.root.focusManagement(Limit(value,sw.getUniqueRef()), false).transaction

  def offset( value : Int ) : SWTransaction = sw.root.focusManagement(Offset(value,sw.getUniqueRef()), false).transaction

  def orderByAsc( ref: String ) : SWTransaction =
    sw.refExist(ref).root.focusManagement(OrderByAsc(Seq(QueryVariable(ref)),sw.getUniqueRef()), false).transaction

  def orderByAsc( lRef: Seq[String] ) : SWTransaction = {
    lRef.foreach( sw.refExist(_) )
    sw.root.focusManagement(OrderByAsc(lRef.map(QueryVariable(_)),sw.getUniqueRef()), false).transaction
  }

  def orderByDesc( ref: String ) : SWTransaction =
    sw.refExist(ref).root.focusManagement(OrderByDesc(Seq(QueryVariable(ref)),sw.getUniqueRef()), false).transaction

  def orderByDesc( lRef: Seq[String] ) : SWTransaction = {
    lRef.foreach( sw.refExist(_) )
    sw.root.focusManagement(OrderByDesc(lRef.map(QueryVariable(_)),sw.getUniqueRef()), false).transaction
  }

  def getSerializedString : String = write(this)
  def setSerializedString(query : String) : SWTransaction = read[SWTransaction](query)

  def console : SWTransaction = sw.console.transaction
}
