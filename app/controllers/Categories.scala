package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._

import com.mongodb.casbah.Imports._
import com.mongodb.casbah.Implicits._
import se.radley.plugin.salat._
import se.radley.plugin.salat.Formats._
import play.api.Play.current
import se.radley.plugin.salat._
import se.radley.plugin.salat.Formats._
import com.novus.salat._
import play.api.libs.json.Json
import com.mongodb.casbah.Imports._
import views._
import models._


/**
 * Categories Controller
 */
object Categories extends Controller with securesocial.core.SecureSocial {

	/**
	 * List all categories and pass forms to the view
	 */
	def list = Action {  implicit request =>
		Ok(html.categories.list(Category.options, categoryForm(), categories))
	}

	/**
	 * JSON API for retrieving data from the database and converting it to JSON via GET
	 */
	def listJson = Action {
		val jsonCategories = Json.toJson(categories.sort(orderBy = MongoDBObject("title" -> 1)).toList)
		Ok(jsonCategories).as("text/javascript")
	}

	/**
	 * Creation af a category and inserting it into the database.
	 * A flash message is delivered to the view to inform the user of the success or failure
	 */
	def create = SecuredAction { implicit request =>
		categoryForm().bindFromRequest.fold(
			formWithErrors => BadRequest("Bad request"),
			category => {
				Category.insert(category)
				Redirect(routes.Categories.list()).flashing("success" -> "Category %s has been created".format(category.title))
			}
		)
	}

	/**
	 * Show all datasets filtered by a special category
	 * @param id
	 * @return
	 */
	def show (id: ObjectId) = Action { implicit request =>
		val dataSets = Dataset.findDatasetsByCategory(id)

		// val dataSets = Dataset.find("category.id" -> id)
		Category.findOneById(id).map(singleCategory =>
			Ok(html.categories.show(singleCategory, tags, categories, dataSets))
		).getOrElse(NotFound)
	}

	/**
	 * Creates the object for the category form and its constraints
	 * @param id
	 */
	def categoryForm(id: ObjectId = new ObjectId) = Form (
		mapping (
			"id" -> ignored(id),
			"title" -> nonEmptyText,
			"parent" -> optional(of[ObjectId])
		)(Category.apply)(Category.unapply)
	)

	/**
	 * Find all categories
	 * @return
	 */
	def categories = {
		Category.findAll
	}

	/**
	 * Find all tags
	 * @return
	 */
	def tags = {
		Tag.findAll
	}

}