package controllers

import play.api._
import play.api.Play._
import play.api.mvc._

import play.api.data._
import play.api.data.Forms._
import play.api.Play.current
import play.api.libs.json.Json
import play.api.libs.ws._
import play.api.data.Forms._
import concurrent.{Promise, Future}
import play.api.libs.concurrent.Execution.Implicits._
import java.util.concurrent.TimeoutException
import se.radley.plugin.salat._
import com.mongodb.casbah.Imports._
import com.novus.salat._
import models._

/**
 * General Application Actions
 */
object Application extends Controller with securesocial.core.SecureSocial {

	/**
	 * Main page
	 */
	def index = Action { implicit request =>
		Ok(views.html.content.index("Building Blocks for Cloud Data as a Service", searchForm, tags, categories))
	}

	/**
	 * Contact page action
	 */
	def contact = UserAwareAction { implicit request =>
		Ok(views.html.content.contact("Contact", searchForm, tags))
	}

	/**
	 * Imprint actioni
	 */
	def imprint = Action { implicit request =>
		Ok(views.html.content.imprint("Imprint", searchForm, tags))
	}

	/**
	 * About action - display the about page
	 */
	def about = Action { implicit request =>
		Ok(views.html.content.about("About", searchForm, tags))
	}

	/**
	 * Learn action - display the learn page
	 */
	def learn = Action { implicit request =>
		Ok(views.html.content.learn("Learn", searchForm, tags))
	}

	/**
	 * Terms action - display the terms page
	 */
	def terms = Action { implicit request =>
		Ok(views.html.content.terms("Terms", searchForm, tags))
	}


	/**
	 * Buy & Sell page action
	 */
	def buySell = SecuredAction { implicit request =>
		Ok(views.html.buySell("Buy & Sell", searchForm, tags))
	}

	/**
	 * Filter action
	 */
	def filter = Action { implicit request =>
		Ok(views.html.filter("Filter", searchForm, tags))
	}

	/**
	 * Visualization stub action
	 */
	def visualization = Action { implicit request =>
		Ok(views.html.visualization("Visualization", searchForm, tags))
	}

	/**
	 * Analytics page action
	 */
	def analytics = Action { implicit request =>
		Ok(views.html.analytics("Analytics", searchForm, tags))
	}

	/**
	 * Mashups page action
	 */
	def mashUps = Action { implicit request =>
		Ok(views.html.mashUps("Mashups", searchForm, tags))
	}

	/**
	 * Search engine action
	 */
	def search = Action(parse.urlFormEncoded) { implicit request =>
		val term = request.body("term").head
		val foundDatasets = Dataset.findDatasetByTitleSearch(term)

		Ok(views.html.search(term, searchForm, tags, foundDatasets))
	}

	def apiDocumentation = Action { implicit request =>
		Ok(views.html.content.api("Api documentation"))
	}

	/**
	 * Administration page action
	 * @return
	 */
	def administration = SecuredAction { implicit request =>
		Ok(views.html.administration("Administration"))
	}

	/**
	 * Display search form
	 */
	val searchForm = Form (
		"term" -> nonEmptyText
	)

	/**
	 * Find all categories and order them by title
	 * @return
	 */
	def categories = {
		Category.findAll.sort(orderBy = MongoDBObject("title" -> 1))
	}

	/**
	 * Find all tags and order them by title
	 * @return
	 */
	def tags = {
		Tag.findAll.sort(orderBy = MongoDBObject("title" -> 1))
	}

}