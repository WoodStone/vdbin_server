package controllers

import java.util.UUID

import javax.inject.Inject
import models.Bin
import org.pac4j.core.profile.{CommonProfile, ProfileManager}
import org.pac4j.play.PlayWebContext
import org.pac4j.play.scala.{Security, SecurityComponents}
import play.api.Configuration
import play.api.libs.json._
import skinny.Pagination

import scala.collection.JavaConverters

class ApiController @Inject() (val controllerComponents: SecurityComponents, configuration: Configuration) extends Security[CommonProfile] with JsonReadWrites {

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

  def delete(id: String) = Secure("", "admin") { implicit request =>
    Bin.deleteById(UUID.fromString(id)) match {
      case 0 => BadRequest
      case _ => Ok
    }
  }

  def check = Secure("") { request =>
    val context = new PlayWebContext(request, playSessionStore)
    val profileManager = new ProfileManager[CommonProfile](context)
    val profiles = profileManager.getAll(true)

    Ok(JavaConverters.asScalaBuffer(profiles).toList.toString())
  }

  def login(r: String) = Secure("KeycloakOidcClient") { implicit request =>
    val cors = configuration.get[Seq[String]]("play.filters.cors.allowedOrigins")

    if (cors.filter(r.startsWith(_)).isEmpty) {
      Redirect("/")
    } else {
      Redirect(r)
    }
  }

}
