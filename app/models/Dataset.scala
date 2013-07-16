package models

import play.api.Play.current
import play.api.{Logger, Application}
import play.api.libs.json._
import java.util.Date
import com.novus.salat._
import com.novus.salat.annotations._
import com.novus.salat.dao._
import com.novus.salat.global._
import com.mongodb.casbah.Imports.{WriteConcern}
import com.mongodb.casbah.query.Imports._
import play.api.libs.functional.syntax._
import se.radley.plugin.salat._
import se.radley.plugin.salat.Binders._
import mongoContext._
import scala._
import collection.JavaConversions
import org.bson.BSONObject
import org.bson.types.ObjectId
import com.mongodb.BasicDBObject
import com.mongodb.BasicDBList
import com.mongodb.DBObject
import java.util.Date
import scala.collection.JavaConversions
import com.mongodb.util._
import com.novus.salat.json.JSONConfig

/**
 * Tag model
 */
case class Dataset(
	id: ObjectId = new ObjectId,
	title: String,
	file: ObjectId,
	fileName: String,
	description: String,
	uploadDate: java.util.Date,
	uploadedBy: String,
	category: Category,
	dataCollection: String,
	headers: List[String],
	downloads: Int
)

/**
 * Dataset object
 */
object Dataset extends DatasetDAO with DatasetJson {
	def all(): List[Dataset] = Nil
	def create(title: String) {}
	def delete(id: Long) {}
}

/**
 * Additional queries and methods for datasets
 */
trait DatasetDAO extends ModelCompanion[Dataset, ObjectId] {
	def collection = mongoCollection("data")

	val dao = new SalatDAO[Dataset, ObjectId](collection) {}

	// create an index over various fields
	collection.ensureIndex(com.mongodb.casbah.Imports.DBObject("title" -> 1, "description" -> 1, "category" -> 1), "datasets", unique = true)

	// mongodb 2.4 text index
	collection.ensureIndex(com.mongodb.casbah.Imports.DBObject("title" -> "text", "description" -> "text", "category" -> "text"), "text")

	/**
	 * Find a dataset by title
	 *
	 * @param title
	 * @return
	 */
	def findOneByDatasetByTitle(title: String): Option[Dataset] = dao.findOne(com.mongodb.casbah.Imports.MongoDBObject("title" -> title))

	/**
	 * find by title fragment
	 * title: |.*ata.*|i
	 * @param title
	 * @return
	 */
	def findDatasetByTitleSearch(title: String): com.novus.salat.dao.SalatMongoCursor[models.Dataset] = {
		val searchPattern = "(?i).*" + title + ".*"
		dao.find(com.mongodb.casbah.Imports.MongoDBObject("title" -> searchPattern.r))
	}

	/**
	 * find all datasets for a special category
	 *
	 * @param id
	 * @return
	 */
	def findDatasetsByCategory(id: ObjectId): com.novus.salat.dao.SalatMongoCursor[models.Dataset] = dao.find(com.mongodb.casbah.Imports.MongoDBObject("category._id" -> id))

	def convertJsonDataToMongoObject(jsonData: JsValue) = {
		val jsonString = Json.stringify(jsonData)
		val mongoDbObject = com.mongodb.util.JSON.parse(jsonString)
		//Logger.info(mongoDbObject.toString)
		mongoDbObject
	}

	/**
	 * Increases the value in the downloads field
	 *
	 * @param id
	 * @return
	 */
	def increaseDownloadCounter(id: ObjectId) = {
		dao.update(MongoDBObject("_id" -> id), $inc ("downloads" -> 1))
	}

}

/**
 * Trait used to convert to and from json
 */
trait DatasetJson {

	implicit val datasetJsonWrite = new Writes[Dataset] {
		def writes(d: Dataset): JsValue = {
			Json.obj(
				"id" -> d.id,
				"title" -> d.title,
				"downloads" -> d.downloads
			)
		}
	}
}