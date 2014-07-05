package issst.evex.kb.server

import akka.actor.{ActorSystem, Props}
import akka.io.IO
import spray.can.Http

object Boot extends App {
  if (args.length != 2) {
    println("Please provide corpus directory path and output path.")
    System.exit(0)
  }

  // we need an ActorSystem to host our application in
  implicit val system = ActorSystem("on-spray-can")

  // create and start our service actor
  val service = system.actorOf(Props(classOf[ProcessingServiceActor], args(0), args(1)), "processing-service")

  // start a new HTTP server on port 8080 with our service actor as the handler
  IO(Http) ! Http.Bind(service, interface = "0.0.0.0", port = 8080)
}
