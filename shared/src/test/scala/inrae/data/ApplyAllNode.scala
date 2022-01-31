package inrae.data

import inrae.semantic_web.node._
import inrae.semantic_web.rdf._

object ApplyAllNode {

  val listNodes : List[Node] = List(
    Root("h1"),
    Something("h1"),
    SubjectOf("h1",URI("http://some")),
    ObjectOf("h1",URI("http://some")),
    LinkTo("h1",URI("http://some")),
    LinkFrom("h1",URI("http://some")),
    Value("h1"),
    ListValues(Seq("h1","h2")),
    UnionBlock("h1",Value("h2")),
    NotBlock("h1",Value("h2")),
    isBlank(false,"h1"),
    isLiteral(false,"h1"),
    isURI(false,"h1"),
    Regex("i","i",false,"h1"),
    Contains("s",false,"h1"),
    StrStarts("s",false,"h1"),
    StrEnds("s",false,"h1"),
    Equal("s",false,"h1"),
    NotEqual("s",false,"h1"),
    Inf("s",false,"h1"),
    InfEqual("s",false,"h1"),
    Sup("s",false,"h1"),
    SupEqual("s",false,"h1"),
    DatatypeNode("href",SubjectOf("h1",URI("http://some")),"h1"),
    SourcesNode("href",Seq(),"h1"),
    OrderByAsc(Seq(),"h1"),
    OrderByDesc(Seq(),"h1"),
    Projection(Seq(),"h1"),
    Distinct("h1"),
    Reduced("h1"),
    Offset(2,"h1"),
    Limit(2,"h1"),
    Bind(SubStr("s","e","h1"),"h1"),
    SparqlDefinitionExpression("s","h1"),
    SubStr("s","e","h1"),
    Replace("a","b","c","h1"),
    Abs("h1"),
    Round("h1"),
    Ceil("h1"),
    Floor("h1"),
    Rand("h1"),
    Datatype("h1"),
    ProjectionExpression(QueryVariable("aq"),Count(Seq(QueryVariable("q")),true,"h1"),"h1"),
    Count(Seq(QueryVariable("q")),false,"h1"),
    //CountAll(true,"h1"),
    Str(URI("s"),"h1"),
    Lang(URI("s"),"h1"),
    LangMatches(URI("s"),"h1")
  )
}
