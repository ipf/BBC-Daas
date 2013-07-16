package models

import play.api.Play.current
import java.util.Date
import com.novus.salat._
import com.novus.salat.annotations._
import com.novus.salat.dao._
import com.novus.salat.global._
import com.mongodb.casbah.Imports._
import play.api.libs.json._
import play.api.libs.functional.syntax._

import se.radley.plugin.salat._
import se.radley.plugin.salat.Binders._
import mongoContext._

/**
 * Category model
 */
case class Category(
	id: ObjectId = new ObjectId,
	title: String,
	parent: Option[ObjectId]
)

/**
 * Helpers for categories
 */
object Category extends CategoryDAO with CategoryJson {
	def all(): List[Category] = Nil

	def create(title: String) {}

	def delete(id: Long) {}

	def options: Seq[(String, String)] = {
		find(MongoDBObject.empty).map(c =>
			(c.id.toString, c.title)).toSeq
	}

}

/**
 * Additional Queries and methods for categories
 */
trait CategoryDAO extends ModelCompanion[Category, ObjectId] {
	def collection = mongoCollection("categories")

	val dao = new SalatDAO[Category, ObjectId](collection) {}

	// Indexes
	collection.ensureIndex(DBObject("title" -> 1), "title", unique = true)

	// Queries
	def findOneByCategoryTitle(title: String): Option[Category] = dao.findOne(MongoDBObject("title" -> title))
}

/**
 * Trait used to convert to and from json
 */
trait CategoryJson {

	implicit val categoryJsonWrite = new Writes[Category] {
		def writes(c: Category): JsValue = {
			Json.obj(
				"id" -> c.id,
				"title" -> c.title,
				"parent" -> c.parent
			)
		}
	}
}