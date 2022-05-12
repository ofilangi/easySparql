package fr.inrae.metabohub.semantic_web.sparql

import fr.inrae.metabohub.semantic_web.rdf.{SparqlBuilder, SparqlDefinition}

import scala.util.{Failure, Success, Try}

case class QueryResult(results: String, mimetype : String = "json") {

  lazy val json =
    Try(ujson.read(results)) match {
      case Success(json) =>
        json.objOpt match {
          case Some(_) => json
          // fix when get only results without header (triplydb)
          case None => ujson.Obj(
            "head" -> ujson.Obj(
              "link" -> ujson.Arr(),
              "vars" -> ujson.Arr()
            ),
            "results" -> ujson.Obj(
              "bindings" -> json
            )
          )
        }
      case Failure(_) => ujson.Obj(
        "head" -> ujson.Obj(
          "link" -> ujson.Arr(),
          "vars" -> ujson.Arr()
        ),
        "results" -> ujson.Obj(
          "bindings" -> ujson.Arr()
        )
      )
    }


  /* get column results */
  def getValues( key : String ): Seq[SparqlDefinition] = {
    json("results")("bindings").arr.flatMap(kv => kv match {
      case o: ujson.Obj => {
        Some(SparqlBuilder.create(o(key)))
      }
      case _ => None
    }).toSeq
  }

  def setDatatype( key : String , uri_values : Map[String,ujson.Value] ): Unit = {
    val datatype = json("results").obj.getOrElse("datatypes",ujson.Obj())
    val keyObjet = datatype.obj.getOrElse(key,ujson.Obj())

    uri_values.foreach( {
      case (subkey, value) => {
        val subkeyObjet = keyObjet.obj.getOrElse(subkey,ujson.Arr())
        subkeyObjet.arr.append(value)
        keyObjet.obj.update(subkey,subkeyObjet)
      }
    })

    datatype.obj.update(key,keyObjet)
    json("results").update("datatypes",datatype)
  }
}
