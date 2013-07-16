package controllers

import play.api.data.Forms._
import play.api._
import play.api.Play._
import play.api.mvc._
import play.api.data._

import com.mongodb.casbah.Imports._

import views._
import securesocial.core.{Identity, Authorization}
import models._
import play.api.libs.json.Json
import play.api.libs.json._
import play.api.Play
import concurrent.Future
import play.api.libs.ws.WS
import java.util.concurrent.TimeoutException
import play.api.libs.concurrent.Execution.Implicits._

/**
 * Users Controller
 */
object Users extends Controller with securesocial.core.SecureSocial {

	/**
	 * User registration action
	 */
	def register = Action { implicit request =>
		Ok(views.html.users.register("Registration"))
	}

	/**
	 * Log in action
	 * @return
	 */
	def login = Action { implicit request =>
		Ok(views.html.users.login("Log In"))
	}

	/**
	 * Make a call to github to get user details
	 * @param token
	 */
	def accessToken(token: JsValue): JsValue = {
		val accessToken: JsValue = token \ "access_token"
		accessToken
	}

	/**
	 * helper for concatenating two strings
	 *
	 * @param strings
	 * @return
	 */
	def concat(strings: String*) = strings filter (_.nonEmpty) mkString
}

// An Authorization implementation that only authorizes uses that logged in using twitter
case class WithProvider(provider: String) extends Authorization {
	def isAuthorized(user: Identity) = {
		user.id.providerId == provider
	}
}

