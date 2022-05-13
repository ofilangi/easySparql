package fr.inrae.metabohub.semantic_web.event

object DiscoveryStateRequestEvent extends Enumeration {

  type DiscoveryRequestEvent = Value
  val
  START,
  QUERY_BUILD,
  START_HTTP_REQUEST,
  PROCESS_HTTP_REQUEST,
  FINISHED_HTTP_REQUEST,
  RESULTS_BUILD,
  DATATYPE_BUILD,
  DATATYPE_DONE,
  RESULTS_DONE,
  REQUEST_DONE,

  ERROR_REQUEST_DEFINITION,
  ERROR_HTTP_REQUEST,
  ABORTED_BY_THE_USER
  = Value

  val nValidStep = 11

  def getPercentProgression(s : DiscoveryRequestEvent) : Double = s match {
    case START => 0.1
    case QUERY_BUILD => 0.2
    case RESULTS_BUILD => 0.3
    case START_HTTP_REQUEST => 0.3
    case PROCESS_HTTP_REQUEST => 0.4
    case FINISHED_HTTP_REQUEST => 0.5
    case RESULTS_DONE => 0.6
    case DATATYPE_BUILD => 0.7
    case DATATYPE_DONE => 0.8
    case _ => 1.0
  }

}

case class DiscoveryRequestEvent(state : DiscoveryStateRequestEvent.Value)
