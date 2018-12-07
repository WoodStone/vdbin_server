package controllers

import java.util.concurrent.CompletionStage

import javax.inject.Inject
import org.pac4j.play.LogoutController
import play.api.Configuration
import play.mvc.Result

class CentralLogoutController @Inject()(config: Configuration) extends LogoutController {
  val baseUrl: String = config.get[String]("baseUrl")
  setDefaultUrl(baseUrl + "/?defaulturlafterlogoutafteridp")
  setLocalLogout(true)
  setCentralLogout(true)
  setLogoutUrlPattern(baseUrl + "/.*")

  override def logout(): CompletionStage[Result] = super.logout()
}
