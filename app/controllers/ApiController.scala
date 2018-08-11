package controllers

import java.text.{DateFormat, SimpleDateFormat}
import java.util.{Date, Locale, TimeZone}

import javax.inject.Inject
import play.api.db.Database
import play.api.libs.json._
import play.api.libs.functional.syntax._
import play.api.mvc.{AbstractController, ControllerComponents}
import services.Id

class ApiController @Inject()(db: Database, cc: ControllerComponents, idGen: Id) extends AbstractController(cc) {

  def post = Action { request =>
    request.body.asJson.map { json =>
      json.validate[Bin] match {
        case s: JsSuccess[Bin] => {
          db.withConnection { conn =>
            val ps = conn.prepareStatement("INSERT INTO Bin (id, timestamp, data, src, ip) VALUES (?, ?, ?, ?, ?)")
            val bin = s.get

            ps.setObject(1, bin.id)
            ps.setObject(2, bin.timestamp)
            ps.setObject(3, bin.data)
            ps.setObject(4, bin.src)
            ps.setObject(5, request.remoteAddress)
            ps.executeUpdate()

            val rsjs = JsObject(Seq(
              "id" -> JsString(bin.id)
            ))
            Ok(rsjs)
          }
        }
        case e: JsError => {
          BadRequest("Json error")
        }
      }
    } getOrElse {
      BadRequest("Not valid json")
    }
  }

  def get(id: String) = Action { request =>
    db.withConnection { conn =>
      val ps = conn.prepareStatement("SELECT * FROM Bin WHERE id = ?")
      ps.setObject(1, id)
      val rs = ps.executeQuery()
      if (rs.next()) {
        val jsrs = Json.toJson(Bin(
          rs.getString("id"),
          rs.getString("timestamp"),
          rs.getString("data"),
          rs.getString("src")
        ))
        Ok(jsrs)
      } else {
        NotFound
      }
    }
  }

  def gets(page: Int, pageSize: Int) = Action { request =>
    val offset = pageSize * (page - 1)
    db.withConnection { conn =>
      val ps = conn.prepareStatement("SELECT id, timestamp, src FROM Bin ORDER BY rowid DESC LIMIT ? OFFSET ?")
      ps.setObject(1, pageSize)
      ps.setObject(2, offset)

      val rs = ps.executeQuery()

      val jsrs = Json.toJson(Iterator.continually((rs.next(), rs)).takeWhile(_._1).map { r =>
        JsObject(Seq(
          "id" -> JsString(rs.getString("id")),
          "timestamp" -> JsString(rs.getString("timestamp")),
          "src" -> JsString(rs.getString("src"))
        ))
      }.toList)

      Ok(jsrs)
    }
  }

  case class Bin(id: String, timestamp: String, data: String, src: String)

  implicit val binRead: Reads[Bin] = (
      (JsPath \ "id").readWithDefault[String](idGen.nextId()) and
      (JsPath \ "timestamp").readWithDefault[String](iso8601String) and
      (JsPath \ "data").read[String] and
      (JsPath \ "src").read[String]
  )(Bin.apply _)

  implicit val binWrite: Writes[Bin] = (
    (JsPath \ "id").write[String] and
    (JsPath \ "timestamp").write[String] and
    (JsPath \ "data").write[String] and
    (JsPath \ "src").write[String]
  )(unlift(Bin.unapply))

  def iso8601String: String = {
    val now: Date = new Date()
    val dateFormat: DateFormat = new SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss", Locale.US)
    dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"))
    return dateFormat.format(now)
  }

}
