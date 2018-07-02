package me.ycanyilmaz.actor

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import akka.routing.RoundRobinPool
import me.ycanyilmaz.App.system
import me.ycanyilmaz.model.{IncomingRequest, OutGoingResponse}

class MasterActor extends Actor with ActorLogging {

  val workerActor: ActorRef = system.actorOf(Props(classOf[WorkerActor]).withRouter(new RoundRobinPool(10)) )

  override def receive: Receive = {
    case IncomingRequest(x, y) =>
      workerActor ! IncomingRequest(x,y)
    case _ => log.error("Not known model")
  }

  override def preRestart(reason: Throwable, message: Option[Any]): Unit =

    log.debug("On the stage")

}
