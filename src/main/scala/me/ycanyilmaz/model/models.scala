package me.ycanyilmaz.model

import spray.json.{DefaultJsonProtocol, RootJsonFormat}
import spray.json._

case class IncomingRequest(name: String, id: Int)

object IncomingRequest extends DefaultJsonProtocol {
  implicit val incomingJsonFormat: RootJsonFormat[IncomingRequest] = jsonFormat2(IncomingRequest.apply)
}

case class OutGoingResponse(name: String)

object OutGoingResponse extends DefaultJsonProtocol {
  implicit val outgoingResponseFormat: RootJsonFormat[OutGoingResponse] = jsonFormat1(OutGoingResponse.apply)
}