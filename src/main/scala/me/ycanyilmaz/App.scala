package me.ycanyilmaz

import akka.actor.{ActorRef, ActorSystem, Props}
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.directives.Credentials
import akka.pattern.ask
import akka.routing.{FromConfig, RoundRobinPool, RoundRobinRoutingLogic}
import akka.stream.ActorMaterializer
import akka.util.Timeout
import me.ycanyilmaz.actor.MasterActor
import me.ycanyilmaz.model.{IncomingRequest, OutGoingResponse}
import me.ycanyilmaz.util.Config

import scala.concurrent.ExecutionContextExecutor
import scala.concurrent.duration._
import scala.util.{Failure, Success}


object App extends Config{

  implicit val system: ActorSystem = ActorSystem("data-collector-actor-system", config = config)
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val ec: ExecutionContextExecutor = system.dispatcher
  implicit val timeout: Timeout = Timeout(5 seconds)

  val masterActor: ActorRef = system.actorOf(Props(classOf[MasterActor]).withRouter(new RoundRobinPool(10)), name = "master-actor")

  def myUserPassAuthenticator(credentials: Credentials): Option[String] =
    credentials match {
      case p @ Credentials.Provided(id) if p.verify("p4ssw0rd") => Some(id)
      case _ => None
    }

  def main(args: Array[String]): Unit = {

    val userRoutes = path("users") {
      post {
        entity(as[IncomingRequest]) { entity =>
          onComplete((masterActor ? entity).mapTo[OutGoingResponse]) { //t =>
            case Success(t) => complete(t)
            case Failure(_) => complete(StatusCodes.InternalServerError)
          }
        }
      }
    }

    val dummyRoute = path("dummy") {
      post {
        entity(as[IncomingRequest]) { entity =>
          onComplete((masterActor ? "A").mapTo[OutGoingResponse]) { //t =>
            case Success(t) => complete(t)
            case Failure(_) => complete(StatusCodes.BadRequest)
          }
        }
      }
    }

    val healthRoute = path("health") {
      get {
        complete(StatusCodes.OK)
      }
    }

//    val newRoute = path("test") {
//      get {
//        authenticateBasic(realm = "secure site", authenticator = myUserPassAuthenticator){
//          complete(StatusCodes.OK)
//        }
//      }
//    }


    val routes = userRoutes ~ healthRoute ~ dummyRoute //~ newRoute

    Http().bindAndHandle(routes, "0.0.0.0", 9000).map { r =>
      println("Application is on localhost with port 9000")
    }

  }

}
