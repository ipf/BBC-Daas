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
 * Tag model
 */
case class Tag(
	id: ObjectId = new ObjectId,
	title: String
)

/**
 * Further methods for Tags
 */
object Tag extends TagDAO with TagJson {
	def all(): List[Tag] = Nil
	def create(title: String) {}
	def delete(id: ObjectId) {}
}

/**
 * Trait with specific methods and queries
 */
trait TagDAO extends ModelCompanion[Tag, ObjectId] {
	def collection = mongoCollection("tags")

	val dao = new SalatDAO[Tag, ObjectId](collection) {}
	val columns = List("_id", "title")

	// Indexes
	collection.ensureIndex(DBObject("title" -> 1), "title", unique = true)

	// Queries
	def findOneByTagTitle(title: String): Option[Tag] = dao.findOne(MongoDBObject("title" -> title))
}

/**
 * Trait used to convert to and from json
 */
trait TagJson {

	implicit val tagJsonWrite = new Writes[Tag] {
		def writes(t: Tag): JsValue = {
			Json.obj(
				"id" -> t.id,
				"title" -> t.title
			)
		}
	}
}