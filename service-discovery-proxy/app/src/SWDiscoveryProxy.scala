package fr.inrae.metabohub.app

import cask.main.Main
import fr.inrae.metabohub.semantic_web.{SWDiscoveryVersionAtBuildTime, SWTransaction}
import io.undertow.Undertow
import io.undertow.server.handlers.BlockingHandler
import ujson.Value
import wvlet.log.Logger.rootLogger.info

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

object SWDiscoveryProxy extends cask.MainRoutes{

  var _server : Option[Undertow] = None

  override def defaultHandler: BlockingHandler =
    new BlockingHandler( CorsHandler(dispatchTrie,
      mainDecorators,
      debugMode = false,
      handleNotFound,
      handleMethodNotAllowed,
      handleEndpointError) )

  import scopt.OParser

  case class Config(
                     port: Int = 8082,
                     host: String = "localhost",
                     verbose: Boolean = true,
                     background : Boolean = false,
                    )

  val builder = OParser.builder[Config]
  val parser1 = {
    import builder._
    OParser.sequence(
      programName("SWDiscoveryProxy"),
      head("swdiscovery-proxy", SWDiscoveryVersionAtBuildTime.version),
      opt[Int]("port")
        .optional()
        .action({ case (r, c) => c.copy(port = r) })
        .validate(x =>
          if (x > 0) success
          else failure("port should be positive."))
        .valueName("<port>")
        .text(s"listening port. default [$port]."),
      opt[String]("host")
        .optional()
        .action({ case (r, c) => c.copy(host = r) })
        .valueName("<host>")
        .text(s"hostname. default [$host]."),
      opt[Unit]("verbose")
        .action((_, c) => c.copy(verbose = true))
        .text("verbose flag."),
      opt[Unit]("background")
        .action((_, c) => c.copy(background = true))
        .text("background flag."),

      help("help").text("prints this usage text"),
      note("some notes." + sys.props("line.separator")),
      checkConfig(_ => success)
    )
  }


  override def main(args: Array[String]) : Unit = {

    OParser.parse(parser1, args, Config()) match {
      case Some(config) =>
        info(s"PORT:${config.port}")
        info(s"HOST:${config.host}")
        info(s"VERBOSE:${config.verbose}")

        if (!config.verbose) Main.silenceJboss()
        val server: Undertow = Undertow.builder
          .addHttpListener(config.port, config.host)
          .setHandler(defaultHandler)
          .build

        _server = Some(server)
        server.start()

        info(s" == start service ${this.getClass.getSimpleName} == ")
        @volatile var keepRunning = true

        Runtime.getRuntime().addShutdownHook(new Thread {
          override def run = {
            println("* catch signal / stop service *")
            server.stop()
            keepRunning = false
          }
        })

        if (config.background) while (keepRunning) {}

      case _ =>
        // arguments are bad, error message will have been displayed
        System.err.println("exit with error.")
    }

  }

  def closeService() : Unit =  {
    _server match {
      case Some(s) => s.stop()
      case _ =>
    }
    this.executionContext.shutdown()
  }

  @cask.get("/get")
  def transaction_get(transaction: String) : Value = {
    apply(transaction)
  }

  @cask.postForm("/post")
  def transaction_post(transaction: String): Value = {
    apply(transaction)
  }

  def apply(transaction : String): Value = {
    println(transaction)
    val future : Future[ujson.Value] = SWTransaction().setSerializedString(transaction).commit().raw
    Await.result(future, Duration.Inf)
  }

  initialize()
}

