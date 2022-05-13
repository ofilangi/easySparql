package fr.inrae.metabohub.semantic_web

import fr.inrae.metabohub.data.DataTestFactory
import fr.inrae.metabohub.semantic_web.rdf._
import fr.inrae.metabohub.semantic_web.configuration._
import utest._

import scala.language.postfixOps
import scala.util.matching.Regex
import scala.util.{Failure, Success, Try}

object SWDiscoveryNodeAddTest extends TestSuite {

  val config: SWDiscoveryConfiguration = DataTestFactory.getConfigVirtuoso1()

  def tests: Tests = Tests {

    test("something") {
      SWDiscovery(config).something("h1")
    }

    test("isSubjectOf on the root") {
      Try(SWDiscovery(config).isSubjectOf(URI("bb"), "var")) match {
        case Success(_) => assert(false)
        case Failure(_) => assert(true)
      }
    }

    test("isSubjectOf") {
      val s = SWDiscovery(config)
               .something("h1")
               .isSubjectOf(URI("bb"), "var")

      val triplet: Regex = "\\?h1+ <bb> \\?var+".r

      triplet.findFirstMatchIn(s.sparql) match {
        case Some(_) => assert(true)
        case None => assert(false)
      }
    }

    test("isObjectOf on the root") {
      Try(SWDiscovery(config)
        .isObjectOf(URI("bb"), "var")
        .console) match {
        case Success(_) => assert(false)
        case Failure(_) => assert(true)
      }
    }

    test("isObjectOf") {
      val s = SWDiscovery(config)
        .something("h1")
        .isObjectOf(URI("bb"), "var")

      val triplet: Regex = "\\?var <bb> \\?h1".r

      triplet.findFirstMatchIn(s.sparql) match {
        case Some(_) => assert(true)
        case None => assert(false)
      }
    }

    test("isLinkTo on the root") {

      Try(SWDiscovery(config).isLinkTo(URI("bb"), "var")) match {
        case Success(_) => assert(false)
        case Failure(_) => assert(true)
      }
    }

    test("isLinkTo") {
      val s = SWDiscovery(config)
        .something("h1")
        .isLinkTo(URI("bb"), "var")

      val triplet: Regex = "\\?h1 \\?var+ <bb>".r

      triplet.findFirstMatchIn(s.sparql) match {
        case Some(_) => assert(true)
        case None => assert(false)
      }
    }

    test("isLinkFrom on the root") {
      Try(SWDiscovery(config).isLinkFrom(URI("bb"), "var")) match {
        case Success(_) => assert(false)
        case Failure(_) => assert(true)
      }
    }

    test("isLinkFrom") {
      val s = SWDiscovery(config)
        .something("h1")
        .isLinkFrom(URI("bb"), "var")

      val triplet: Regex = "<bb> \\?var \\?h1".r

      triplet.findFirstMatchIn(s.sparql) match {
        case Some(_) => assert(true)
        case None => assert(false)
      }
    }

    test("isA on the root") {
      Try(SWDiscovery(config).isA(URI("class"))) match {
        case Success(_) => assert(false)
        case Failure(_) => assert(true)
      }
    }

    test("isA") {
      val s = SWDiscovery(config)
        .something("h1")
        .isA(URI("class"))

      val triplet: Regex = "\\?h1 a \\?object[0-9]+".r

      triplet.findFirstMatchIn(s.sparql) match {
        case Some(_) => assert(true)
        case None => assert(false)
      }
    }
  }
}
