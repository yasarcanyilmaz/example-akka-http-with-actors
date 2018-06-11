package me.ycanyilmaz

import akka.actor.{ActorRef, ActorSystem, Props}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.StatusCodes
import akka.stream.ActorMaterializer
import akka.http.scaladsl.server.Directives._
import me.ycanyilmaz.model.{IncomingRequest, OutGoingResponse}

import scala.concurrent.ExecutionContextExecutor
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import me.ycanyilmaz.actor.MasterActor
import akka.pattern.ask
import akka.util.Timeout

import scala.concurrent.duration._
import scala.util.{Failure, Success}

object App {

  implicit val system: ActorSystem = ActorSystem()
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val ec: ExecutionContextExecutor = system.dispatcher
  implicit val timeout: Timeout = Timeout(5 seconds)

  val masterActor: ActorRef = system.actorOf(Props(classOf[MasterActor]), "master-actor")

  def main(args: Array[String]): Unit = {

    import me.ycanyilmaz.model.OutGoingResponseProtocol._
    import me.ycanyilmaz.model.IncomingRequestProtocol._


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

    val routes = userRoutes ~ healthRoute ~ dummyRoute

    Http().bindAndHandle(routes, "0.0.0.0", 9000).map { r =>
      println("Application is on localhost with port 9000")
    }

  }

}
