package me.ycanyilmaz.model

import spray.json.{DefaultJsonProtocol, RootJsonFormat}
import spray.json._

case class IncomingRequest(name: String, id: Int)

object IncomingRequestProtocol extends DefaultJsonProtocol {
  implicit val incomingJsonFormat: RootJsonFormat[IncomingRequest] = jsonFormat2(IncomingRequest)
}

case class OutGoingResponse(name: String)

object OutGoingResponseProtocol extends DefaultJsonProtocol {
  implicit val outgoingResponseFormat: RootJsonFormat[OutGoingResponse] = jsonFormat1(OutGoingResponse)
}