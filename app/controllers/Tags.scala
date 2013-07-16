package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._

import org.bson.types.ObjectId

import play.api.Play.current

import com.novus.salat._
import com.mongodb.casbah.Imports._
import se.radley.plugin.salat._
import se.radley.plugin.salat.Formats._

import views._

import models._
import play.api.libs.json.Json

/**
 * Tag Controller
 */
object Tags extends Controller with securesocial.core.SecureSocial {

	/**
	 * Show details for a single tag
	 * @param tag
	 * @return
	 */
	def show(tag: ObjectId) = Action { implicit request =>
		Tag.findOneById(tag).map(singleTag =>
			Ok(html.tags.show(singleTag, tags))
		).getOrElse(NotFound)
	}

	/**
	 * list all tags and display form for adding a new tag
	 * @return
	 */
	def list = Action {  implicit request =>
		Ok(html.tags.list(tags, tagForm()))
	}

	/**
	 * Deletes a tag
	 * @param id
	 * @return
	 */
	def delete(id: ObjectId) = SecuredAction { implicit request =>
		Tag.remove(MongoDBObject("_id" -> id))
		Redirect(routes.Tags.list()).flashing("success" -> "Tag has been deleted")
	}

	/**
	 * Create a new tag and give some feedback whether the creation succeeded or failed
	 * @return
	 */
	def create = SecuredAction { implicit request =>
			tagForm().bindFromRequest.fold(
				formWithErrors => BadRequest("Bad request"),
				tag => {
					Tag.insert(tag)
					Redirect(routes.Tags.list()).flashing("success" -> "Tag %s has been created".format(tag.title))
				}
			)
	}

	/**
	 * Updates a tag and its values
	 * @return
	 */
	def update = Action {
		Ok("TODO")
	}

	/**
	 * API for JSON Calls. Lists all tags
	 * @return
	 */
	def listJson = Action {
		val jsonTags = Json.toJson(tags.toList)
		Ok(jsonTags).as("text/javascript")
	}

	/**
	 * Form for adding tags
	 * @param id
	 * @return
	 */
	def tagForm(id: ObjectId = new ObjectId) = Form(
		mapping(
			"id" -> ignored(id),
			"title" -> nonEmptyText
		)(Tag.apply)(Tag.unapply)
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
