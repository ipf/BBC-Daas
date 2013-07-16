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
 * User definition
 * @param id
 * @param username
 * @param emailAddress
 * @param token
 * @param added
 */
case class User(
	 id: ObjectId = new ObjectId,
	 username: String,
	 emailAddress: String,
	 token: String,
	 added: Date = new Date()
)

/**
 * Stub for user objects
 */
object User extends UserDAO with UserJson

/**
 * Additional queries and methods for users
 */
trait UserDAO extends ModelCompanion[User, ObjectId] {
	def collection = mongoCollection("users")

	val dao = new SalatDAO[User, ObjectId](collection) {}

	// Indexes
	collection.ensureIndex(DBObject("username" -> 1), "emailAddress", unique = true)

	// Queries
	def findOneByUsername(username: String): Option[User] = dao.findOne(MongoDBObject("username" -> username))

	def authenticate(username: String, password: String): Option[User] = findOne(DBObject("username" -> username, "password" -> password))
}

/**
 * Trait used to convert to and from json
 */
trait UserJson {

	implicit val userJsonWrite = new Writes[User] {
		def writes(u: User): JsValue = {
			Json.obj(
				"id" -> u.id,
				"username" -> u.username,
				"emailAddress" -> u.emailAddress,
				"added" -> u.added,
				"token" -> u.token
			)
		}
	}
}