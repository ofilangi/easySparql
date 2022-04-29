package inrae.semantic_web
import inrae.data.DataTestFactory
import inrae.semantic_web.rdf._
import inrae.semantic_web.configuration._
import utest._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object SWDiscoveryHelperTest  extends TestSuite  {

  val insertData: Future[Any] = DataTestFactory.insertVirtuoso1(
    """
      <http://aa> <http://bb> <http://cc> .
      <http://aa> <http://bb2> <http://cc2> .
      <http://aa> <http://bb2> <http://cc3> .

      <http://bb2> a owl:ObjectProperty .

      <http://aa1> a <http://LeafType> .

      <http://aa2> a <http://LeafType> .
      <http://aa2> a <http://OwlClass> .


      <http://aa3> <http://propDatatype> "test" .
      <http://aa3> <http://objprop> <http://something_else> .

      <http://OwlClass> a owl:Class .
      """.stripMargin, this.getClass.getSimpleName)

  val config: SWDiscoveryConfiguration = DataTestFactory.getConfigVirtuoso1()


  def tests: Tests = Tests {
    test("count") {
     insertData.map(_ => {
        SWDiscovery(config)
          .graph(IRI(DataTestFactory.graph1(this.getClass.getSimpleName)))
          .something("h1") //http://rdf.ebi.ac.uk/terms/chembl#BioComponent
          .isSubjectOf(URI("http://bb2"))
          .finder
          .count(Seq("h1"))
          .map(count => assert(count == 2))
      }).flatten
    }

    test("count distinct") {
      insertData.map(_ => {
        SWDiscovery(config)
          .graph(IRI(DataTestFactory.graph1(this.getClass.getSimpleName)))
          .something("h1") //http://rdf.ebi.ac.uk/terms/chembl#BioComponent
          .isSubjectOf(URI("http://bb2"))
          .finder
          .count(Seq("h1"),distinct = true)
          .map(count => assert(count == 1))
      }).flatten
    }

    test("count with datatype") {
      insertData.map(_ => {
        SWDiscovery(config)
          .graph(IRI(DataTestFactory.graph1(this.getClass.getSimpleName)))
          .something("h1") //http://rdf.ebi.ac.uk/terms/chembl#BioComponent
          .datatype(URI("http://fake/"),"dt1")
          .isSubjectOf(URI("http://bb2"))
          .finder
          .count(Seq("h1","dt1"))
          .map(count => assert(count == 2))
      }).flatten
    }

    test("findClasses") {
      val query = SWDiscovery(config)
        .graph(IRI(DataTestFactory.graph1(this.getClass.getSimpleName)))
        .something("h1")
        .set(URI("http://aa1"))
        .finder

      insertData.map(_ => {
        query.classes()
          .map(types => assert(types.length == 1))
      }).flatten

      insertData.map(_ => {
        query.classes("", "", 1)
          .map(types => assert(types.isEmpty))
      }).flatten

      insertData.map(_ => {
        query.classes("eaf")
          .map(types => assert(types.length == 1))
      }).flatten

      insertData.map(_ => {
        query.classes("^(eaf)")
          .map(types => assert(types.isEmpty))
      }).flatten

    }

    test("findClasses with mother class -> owl:Class") {
      val query = SWDiscovery(config)
        .graph(IRI(DataTestFactory.graph1(this.getClass.getSimpleName)))
        .something("h1")
        .set(URI("http://aa2"))
        .finder

      insertData.map(_ => {
        query.classes()
          .map(types => assert(types.length == 1))
      }).flatten

      insertData.map(_ => {
        query.classes(regex="")
          .map(types => assert(types.length == 1))
      }).flatten

      insertData.map(_ => {
        query.classes("", URI("Class", "owl"))
          .map(types => assert(types.length == 1))
      }).flatten

      insertData.map(_ => {
        query.classes("OwlClass", URI("Class", "owl"))
          .map(types => assert(types.length == 1))
      }).flatten

      insertData.map(_ => {
        query.classes("eafTyp", URI("Class", "owl"))
          .map(types => assert(types.isEmpty))
      }).flatten

      insertData.map(_ => {
        query.classes("OwlClass", URI("Class", "owl"), 1)
          .map(types => assert(types.isEmpty))
      }).flatten
    }


    test("findObjectProperties") {
      val query = SWDiscovery(config).something("h1")
        .set(URI("http://aa"))
        .finder

      insertData.map(_ => {
        query.objectProperties()
          .map(response => assert(response.length == 2))
      }).flatten

      insertData.map(_ => {
        query.objectProperties("bb")
          .map(response => assert(response.length == 2))
      }).flatten

      insertData.map(_ => {
        query.objectProperties("bb", "")
          .map(response => assert(response.length == 2))
      }).flatten

      insertData.map(_ => {
        query.objectProperties("bb", "", 1)
          .map(response => assert(response.isEmpty))
      }).flatten
    }

    test("findObjectProperties mother class --> owl:ObjectProperty ") {
      val query = SWDiscovery(config).something("h1")
        .set(URI("http://aa"))
        .finder

      insertData.map(_ => {
        query.objectProperties("", URI("ObjectProperty", "owl"))
          .map(response => assert(response.length == 1))
      }).flatten

      insertData.map(_ => {
        query.objectProperties("bb", URI("ObjectProperty", "owl"))
          .map(response => assert(response.length == 1))
      }).flatten

      insertData.map(_ => {
        query.objectProperties("bb", URI("ObjectProperty", "owl"), 1)
          .map(response => assert(response.isEmpty))
      }).flatten
    }

    test("datatypeProperties") {
      val query = SWDiscovery(config).something("h1")
        .set(URI("http://aa3"))
        .finder

      insertData.map(_ => {
        query.datatypeProperties()
          .map(response => assert(response.length == 1))
      }).flatten

      insertData.map(_ => {
        query.datatypeProperties("propDatatype")
          .map(response => assert(response.length == 1))
      }).flatten

      insertData.map(_ => {
        query.datatypeProperties("propDatatype", "")
          .map(response => assert(response.length == 1))
      }).flatten

      insertData.map(_ => {
        query.datatypeProperties("propDatatype", "", 1)
          .map(response => assert(response.isEmpty))
      }).flatten
    }

    test("subjectProperties") {
      val query = SWDiscovery(config).something("h1")
        .set(URI("http://cc"))
        .finder

      insertData.map(_ => {
        query.subjectProperties()
          .map(response => assert(response.length == 1))
      }).flatten

      insertData.map(_ => {
        query.subjectProperties("bb")
          .map(response => assert(response.length == 1))
      }).flatten

      insertData.map(_ => {
        query.subjectProperties("bb", "")
          .map(response => assert(response.length == 1))
      }).flatten

      insertData.map(_ => {
        query.subjectProperties("bb", URI(":anything"))
          .map(response => assert(response.isEmpty))
      }).flatten

      insertData.map(_ => {
        query.subjectProperties("bb", "", 1)
          .map(response => assert(response.isEmpty))
      }).flatten
    }
  }

}
