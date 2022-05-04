package fr.inrae.mth.app

import inrae.semantic_web.SWTransaction
import ujson.Value

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

object SWDiscoveryProxy extends cask.MainRoutes{

  @cask.get("/get")
  def transaction(transaction: String) : Value = {
    apply(transaction)
  }

  @cask.postForm("/post")
  def test2(transaction: String): Value = {
    apply(transaction)
  }

  def apply(transaction : String): Value = {
    val future : Future[ujson.Value] = SWTransaction().setSerializedString(transaction).commit().raw
    Await.result(future, Duration.Inf)
  }

  initialize()
}