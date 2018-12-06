package controllers

import java.util.UUID

import javax.inject.Inject
import models.Bin
import play.api.libs.json._
import play.api.mvc.{AbstractController, ControllerComponents}
import skinny.Pagination

class ApiController @Inject()(cc: ControllerComponents) extends AbstractController(cc) with JsonReadWrites {

  def get(id: String) = Action { request =>
    val bin = Bin.findById(UUID.fromString(id))
    Ok(Json.toJson(bin))
  }

  def gets(page: Int, per: Int) = Action { request =>
    Ok(Json.toJson(Bin.paginate(Pagination.page(page).per(per)).apply()))
  }

  def post = Action { request =>
    request.body.asJson.map { json =>
      json.validate[Bin] match {
        case s: JsSuccess[Bin] => {
          val rsjs = JsObject(Seq(
            "uuid" -> JsString(Bin.create(s.value).toString)
          ))
          Ok(rsjs)
        }
        case e: JsError => {
          BadRequest
        }
      }
    } getOrElse {
      BadRequest
    }
  }

}
