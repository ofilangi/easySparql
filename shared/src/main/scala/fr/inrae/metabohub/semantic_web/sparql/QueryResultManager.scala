package fr.inrae.metabohub.semantic_web.sparql

object hashBuilder {

  val separator : String =":"

  def encode(s : String) : (List[String],List [Short])  = {
    val sA : Array[String] = s.split(separator)
    val dict = sA.toList.distinct
   // val tp = sA.map(x => dict.indexOf(x)).toList
   // println("ratio-->" + (dict.mkString("").length+(tp.length*(2))).toFloat/s.length.toFloat)

    (dict,sA.map(x => dict.indexOf(x).toShort).toList)
  }

  def decode( dic: List[String], data : List[Short] ) : String = {
    data.map( x => dic(x) ).mkString(separator)
  }

}

/**
 * Simple manager to memorize results
 */

case class QueryResultManager() {
  var mapQueryResult = Map[String,(List[String],List[Short])]()

  private def encode(value : String ) : (List[String],List [Short]) = {
    hashBuilder.encode(value)
  }

  private def decode(k : List[String], v: List [Short] ) : String = {
    hashBuilder.decode(k,v)
  }

  def set(queryString : String ,value:String) = {
    mapQueryResult += queryString -> encode(value)
  }

  def remove(queryString : String) = {
    mapQueryResult = mapQueryResult.-(queryString)
  }

  def get(queryString : String) : Option[String] = mapQueryResult.get(queryString) match {
    case Some( (lk,lv) ) => Some(decode(lk,lv))
    case None => None
  }
}
