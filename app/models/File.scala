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
import models._

/**
 * File handling - upload / delete ...
 *
 * @author Ingo Pfennigstorf <i.pfennigstorf@gmail.com>
 */
case class File(
	id: ObjectId,
	fileName: String,
	category: Option[ObjectId],
	description: Option[String],
	downloads: Int
)

/**
 * File methods
 */
object File {
	def all(): List[File] = Nil

	def create(fileName: String) {}

	def delete(id: Long) {}

}