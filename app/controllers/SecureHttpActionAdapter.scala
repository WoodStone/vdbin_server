package controllers

import org.pac4j.core.context.HttpConstants
import org.pac4j.play.PlayWebContext
import org.pac4j.play.http.DefaultHttpActionAdapter
import play.mvc.Result
import play.mvc.Results._

class SecureHttpActionAdapter extends DefaultHttpActionAdapter {

  override def adapt(code: Int, context: PlayWebContext): Result = {
    if (code == HttpConstants.UNAUTHORIZED) {
      unauthorized("error401")
    } else if (code == HttpConstants.FORBIDDEN) {
      forbidden("forbidden")
    } else {
      super.adapt(code, context)
    }
  }

}
