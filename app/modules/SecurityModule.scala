package modules

import com.google.inject.{AbstractModule, Provides}
import com.nimbusds.jose.JWSAlgorithm
import com.nimbusds.oauth2.sdk.auth.ClientAuthenticationMethod
import controllers.SecureHttpActionAdapter
import org.pac4j.core.authorization.authorizer.RequireAnyRoleAuthorizer
import org.pac4j.core.client.Clients
import org.pac4j.core.config.Config
import org.pac4j.core.context.WebContext
import org.pac4j.core.profile.CommonProfile
import org.pac4j.core.redirect.{RedirectAction, RedirectActionBuilder}
import org.pac4j.oidc.client.KeycloakOidcClient
import org.pac4j.oidc.config.KeycloakOidcConfiguration
import org.pac4j.play.{CallbackController, LogoutController}
import org.pac4j.play.scala.{DefaultSecurityComponents, Pac4jScalaTemplateHelper, SecurityComponents}
import play.api.{Configuration, Environment}
import org.pac4j.play.store.{PlayCookieSessionStore, PlaySessionStore, ShiroAesDataEncrypter}

class SecurityModule(environment: Environment, configuration: Configuration) extends AbstractModule {

  val baseUrl = configuration.get[String]("baseUrl")

  override def configure(): Unit = {

    val secretKey = configuration.get[String]("play.http.secret.key").substring(0, 16)
    val dataEncrypter = new ShiroAesDataEncrypter(secretKey)
    val playSessionStore = new PlayCookieSessionStore(dataEncrypter)
    bind(classOf[PlaySessionStore]).toInstance(playSessionStore)

    bind(classOf[SecurityComponents]).to(classOf[DefaultSecurityComponents])

    bind(classOf[Pac4jScalaTemplateHelper[CommonProfile]])

    val callbackController = new CallbackController()
    callbackController.setDefaultUrl("/")
    callbackController.setMultiProfile(true)
    bind(classOf[CallbackController]).toInstance(callbackController)

    val logoutController = new LogoutController()
    logoutController.setDefaultUrl("/?defaulturlafterlogout")
    bind(classOf[LogoutController]).toInstance(logoutController)

  }

  @Provides
  def provideOidcClient: KeycloakOidcClient = {
    val clientId = configuration.get[String]("oidc.clientId")
    val clientSecret = configuration.get[String]("oidc.clientSecret")
    val baseUri = configuration.get[String]("oidc.baseUri")
    val realm = configuration.get[String]("oidc.realm")

    val oidcConfiguration = new KeycloakOidcConfiguration()
    oidcConfiguration.setClientId(clientId)
    oidcConfiguration.setSecret(clientSecret)
    oidcConfiguration.setBaseUri(baseUri)
    oidcConfiguration.setRealm(realm)

    oidcConfiguration.setScope("openid profile roles email")
    oidcConfiguration.setResponseType("code")
    oidcConfiguration.setClientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
    oidcConfiguration.setPreferredJwsAlgorithm(JWSAlgorithm.RS256)


    val oidcClient = new KeycloakOidcClient(oidcConfiguration)
    oidcClient.addAuthorizationGenerator((ctx, profile) => {
      println(profile.getAttributes)
      profile
    })
    oidcClient
  }

  @Provides
  def provideConfig(oidcClient: KeycloakOidcClient): Config = {
    val clients = new Clients(baseUrl + "/callback", oidcClient)
    val config = new Config(clients)
    config.addAuthorizer("admin", new RequireAnyRoleAuthorizer[Nothing]("RandomRole"))
    config.setHttpActionAdapter(new SecureHttpActionAdapter())
    //bind(classOf[Config]).toInstance(config)
    config
  }

}
