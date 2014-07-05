package issst.evex.kb.server

import akka.actor.Actor
import spray.routing._
import spray.http._
import MediaTypes._

import issst.evex.kb.util.B2UCpeLauncher

// we don't implement our route structure directly in the service actor because
// we want to be able to test it independently, without having to spin up an actor
class ProcessingServiceActor(inputDir: String, outputDir: String) extends Actor with ProcessingService {

  // the HttpService trait defines only one abstract member, which
  // connects the services environment to the enclosing actor or test
  def actorRefFactory = context

  // this actor only runs our route, but you could add
  // other things here, like request stream processing
  // or timeout handling
  def receive = runRoute(myRoute(inputDir, outputDir))
}


// this trait defines our service behavior independently from the service actor
trait ProcessingService extends HttpService {

  def myRoute(inputDir: String, outputDir: String) = {
    path("") {
      post {
        respondWithStatus(200) {
          complete {
            B2UCpeLauncher.launchCpe(inputDir, outputDir)
            "ok"
          }
        }
      }
    }
  }
}
