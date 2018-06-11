package me.ycanyilmaz.actor

import akka.actor.{Actor, ActorLogging}
import me.ycanyilmaz.model.{IncomingRequest, OutGoingResponse}

class MasterActor extends Actor with ActorLogging {
  
  override def receive: Receive = {
    case IncomingRequest(x, y) =>
      log.info(s"Incoming request: name: $x, id: $y")
      sender() ! OutGoingResponse(x)
    case _ => log.error("Not known model")
  }

}
