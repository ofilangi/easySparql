package fr.inrae.metabohub.data

import org.apache.http.NameValuePair
import org.apache.http.client.entity.UrlEncodedFormEntity
import org.apache.http.client.methods.HttpPost
import org.apache.http.impl.client.HttpClientBuilder
import org.apache.http.message.BasicNameValuePair

import java.util
import scala.concurrent.Future


case object PostRequest {
  implicit val ec: scala.concurrent.ExecutionContext = scala.concurrent.ExecutionContext.global

  def put(stringQuery: String, url_endpoint: String) : Future[Any] = {
    val post = new HttpPost(url_endpoint)
    val nameValuePairs = new util.ArrayList[NameValuePair]()
    nameValuePairs.add(new BasicNameValuePair("query", stringQuery))
    post.setEntity(new UrlEncodedFormEntity(nameValuePairs))
    val client = HttpClientBuilder.create.build()

    Future { client.execute(post) }
  }
}
