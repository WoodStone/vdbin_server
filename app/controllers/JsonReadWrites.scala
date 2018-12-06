package controllers

import java.text.{DateFormat, SimpleDateFormat}
import java.util.{Date, Locale, TimeZone, UUID}

import models._
import play.api.libs.json._
import play.api.libs.functional.syntax._

trait JsonReadWrites {

  implicit val binWrites = new Writes[Bin] {
    def writes(bin: Bin) = Json.obj(
      "uuid" -> bin.uuid.toString,
      "timestamp" -> bin.timestamp,
      "data" -> bin.data,
      "src" -> bin.src
    )
  }

  implicit val binRead: Reads[Bin] = (
    (JsPath \ "uuid").readWithDefault[UUID](UUID.fromString("aaabbbbb-0000-0000-0000-b2f4e744d023")) and
      (JsPath \ "timestamp").readWithDefault[String](iso8601String) and
      (JsPath \ "data").read[String] and
      (JsPath \ "src").read[String]
  )(Bin.apply _)

  implicit val binUUIDRead: Reads[UUID] = (JsPath \ "uuid").read[String].map(uuid => UUID.fromString(uuid))

  def iso8601String: String = {
    val now: Date = new Date()
    val dateFormat: DateFormat = new SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss", Locale.US)
    dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"))
    return dateFormat.format(now)
  }

}
