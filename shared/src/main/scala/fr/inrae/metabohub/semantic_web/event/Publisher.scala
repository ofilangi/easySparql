package fr.inrae.metabohub.semantic_web.event

import scala.collection.immutable.{HashMap, HashSet}


trait Publisher[Event] {
  type Pub <: Publisher[Event]
  type Sub = Subscriber[Event, Pub]

  protected val self: Pub = this.asInstanceOf[Pub]

  private var filters = new HashSet[Sub]()
  private var suspended = new HashSet[Sub]()


  def subscribe(sub: Sub): Publisher[Event] = { filters += sub ; this }

  /**
   * RAJOUTER la methode subscribe(sub: Sub, FILTER (event => bool )):
   * pour filter sur les objets (subscriber) qui
   * avait souscrit au publisher sur un event precis !!!!
   *
   * voir la classe publisher de scala 1.2
    */


  def suspendSubscription(sub: Sub): Publisher[Event] = { suspended += sub ; this }
  def activateSubscription(sub: Sub) : Publisher[Event] = { suspended -= sub ; this }
  def removeSubscription(sub: Sub) : Publisher[Event] = { filters -= sub ; this }
  def removeSubscriptions() : Publisher[Event] = { filters = new HashSet[Sub]() ; this }

  protected def publish(event: Event): Unit = {
    filters.foreach(sub =>
      if (!suspended.contains(sub) )  sub.notify(self, event)
    )
  }
}
