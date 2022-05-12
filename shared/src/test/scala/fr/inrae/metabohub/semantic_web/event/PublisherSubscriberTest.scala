package fr.inrae.metabohub.semantic_web.event

import utest.{TestSuite, Tests, test}

object PublisherSubscriberTest extends TestSuite {

  case class TestEvent( s : String )
  case class ClassPublisher() extends Publisher[TestEvent] {
    def action() = {
      publish(TestEvent("action"))
    }
  }
  case class ClassSubscriber(
                              var start : Boolean=false,
                              var action : Boolean = false,
                              publisher : ClassPublisher
                            )
    extends Subscriber[TestEvent,ClassPublisher] {
    val myInst = this.asInstanceOf[Subscriber[TestEvent,Publisher[TestEvent]]]
    publisher.subscribe(myInst)

    def suspendSubscription: Publisher[TestEvent] = publisher.suspendSubscription(myInst)
    def activateSubscription: Publisher[TestEvent] = publisher.activateSubscription(myInst)
    def removeSubscription: Publisher[TestEvent] = publisher.removeSubscription(myInst)



    override def notify(pub: ClassPublisher, event: TestEvent): Unit = {
      action = ( event.s == "action")
    }
  }

  def tests: Tests = Tests {
    test("subscriber inscription") {
      val p = ClassPublisher()
      val s = ClassSubscriber(publisher=p)
      assert(!s.action)
      p.action()
      assert(s.action)
    }

    test("suspendSubscription/activateSubscription") {
      val p = ClassPublisher()
      val s = ClassSubscriber(publisher=p)
      s.suspendSubscription
      p.action()
      assert(!s.action)
      s.activateSubscription
      p.action()
      assert(s.action)
    }

    test("removeSubscription") {
      val p = ClassPublisher()
      val s = ClassSubscriber(publisher=p)
      s.removeSubscription
      p.action()
      assert(!s.action)
    }

    test("removeSubscriptions") {
      val p = ClassPublisher()
      val s = ClassSubscriber(publisher=p)
      p.removeSubscriptions()
      p.action()
      assert(!s.action)
    }

  }
}