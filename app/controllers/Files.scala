package controllers

import play.api._
import libs.iteratee.Enumerator
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.Play
import play.api.Play.current
import play.api.data.format.Formats._
import com.novus.salat._
import com.mongodb.casbah.Imports._
import com.mongodb.casbah.Implicits._
import se.radley.plugin.salat._
import se.radley.plugin.salat.Formats._
import com.mongodb.casbah.gridfs.Imports._
import views._
import models._
import play.api.libs.json.Json
import play.api.libs.json
import com.github.tototoshi.csv._
import java.security.MessageDigest
import com.google.common.hash

import java.util
import java.text.SimpleDateFormat
import com.mongodb.casbah.MongoCollection

/**
 * Files Controller handles uploads / downloads
 */
object Files extends Controller with securesocial.core.SecureSocial {

	/**
	 * Create a file / dataset
	 */
	def create = UserAwareAction(parse.multipartFormData) { implicit request =>

			import com.mongodb.casbah.Imports.DBObject._

			val title = request.body.asFormUrlEncoded("Title").last
			val description = request.body.asFormUrlEncoded("description").last

			// find category
			def category = {
				Category.findOneById(new ObjectId(request.body.asFormUrlEncoded("category").last)).get
			}

			// initial download counter
			val downloads: java.lang.Integer = 0

			// create a unique collection name for the dataset
			val collectionName = md5(description + title + description + System.currentTimeMillis / 1000)

			// get username
			val uploadedBy = request.user match {
				case Some(user) => user.fullName
				case _ => "guest"
			}

			request.body.file("File") match {
				case Some(file) =>

					// generate a file id on our own to use it in metadata and have a reference to the file
					val fileId = new ObjectId

					val gridFs = gridFS("files")
					val uploadedDataset = gridFs.createFile(file.ref.file)
					// set required filename and contentType
					uploadedDataset.contentType = file.contentType.orNull
					uploadedDataset.filename = file.filename

					// csv conversion
					def reader = CSVReader.open(file.ref.file)

					// get the field titles and store them in the metadata set
					val headers = reader.readNext.get

					// insert metadata into dataset collection
					val dataset = Dataset (
						new ObjectId,
						title,
						fileId,
						file.filename,
						description,
						new java.util.Date,
						uploadedBy,
						category,
						collectionName,
						headers,
						downloads
					)

					Dataset.insert(dataset)

					// get the collection where the dataset should ne inserted into
					def collection = mongoCollection(collectionName)

					// zomg
					// @todo needs speedup. it is simply inefficient to pass each document to a databse
					reader.allWithHeaders.foreach(read => collection.insert(csvToMongo(read)))

					// set the id of the file
					uploadedDataset.put("_id", fileId)
					// set title of dataset
					uploadedDataset.put("title", title)
					// set a unique collection name storing the dataset's data
					uploadedDataset.put("collectionName", collectionName)

					// save the raw document into gridfs
					uploadedDataset.save()

					Ok(html.files.process("Process Dataset", reader)).flashing("success" -> "File %s has been uploaded".format(file.filename))

				case None =>
					Redirect(routes.Files.list).flashing("fail" -> "No file has been not been uploaded")
			}
	}

	/**
	 * List uploaded files
	 * @return
	 */
	def list = Action { implicit request =>
		val datasets = models.Dataset.findAll
		Ok(html.files.list("List Datasets", datasets))
	}

	/**
	 * List action - display form and options
	 */
	def upload = SecuredAction { implicit request =>
		val tags = Tag.findAll.sort(orderBy = MongoDBObject("title" -> 1))
		Ok(html.files.upload(models.File.all(), fileForm(), tags, Category.options))
	}

	/**
	 * Find a file by given id
	 * @param id
	 * @return
	 */
	def show (id: ObjectId) = Action { implicit request =>
		val dataset = Dataset.findOneById(id)
		val data = mongoCollection(dataset.get.dataCollection).find
		Ok(html.files.show("Show file " + dataset.get.title, dataset, data))
	}

	/**
	 * Get a json representation of the dataset
	 * @todo remove limit to show all datasets
	 * @param id
	 * @return
	 */
	def showDataset(id: String) = Action {
		val data = mongoCollection(id)
		// get raw mongodb objects and omit the id by a projection query
		val json = "[%s]".format(data.find(MongoDBObject(),MongoDBObject("_id" -> 0)).limit(100).toList.mkString(","))
		Ok(json).as("text/javascript")
	}

	/**
	 * Delete a file. Call to api with file id
	 * @param id
	 * @return
	 */
	def delete(id: ObjectId) = Action { implicit request =>
		Ok(html.files.delete("Delete"))
	}

	/**
	 * Download a file
	 * @param id
	 * @return
	 */
	def download(id: ObjectId) = Action {
		import com.mongodb.casbah.Implicits._

		//increase download counter
		Dataset.increaseDownloadCounter(id)

		// @ start real file download
		// thanks to http://stackoverflow.com/questions/13135653/any-sample-to-store-and-retrieve-image-on-mongodb-using-gridfs-scala-and-playfr
		val fileToDownload = Dataset.findOneById(id).last

		val gridFs = gridFS("files")

		gridFs.findOne(Map("_id" -> fileToDownload.file)) match {
			case Some(f) => SimpleResult(
				ResponseHeader(OK, Map(
					CONTENT_LENGTH -> f.length.toString,
					CONTENT_TYPE -> f.contentType.getOrElse(BINARY),
					DATE -> new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss 'GMT'", java.util.Locale.US).format(f.uploadDate)
				)),
				Enumerator.fromStream(f.inputStream)
			)

			case None => NotFound
		}
	}

	/**
	 * Creates the object for the category form and its constraints
	 * @param id
	 */
	def fileForm(id: ObjectId = new ObjectId) = Form(
		mapping(
			"id" -> ignored(id),
			"fileName" -> nonEmptyText,
			"category" -> optional(of[ObjectId]),
			"description" -> optional(of[String]),
			"downloads" -> ignored(0)
		)(models.File.apply)(models.File.unapply)
	)

	/**
	 * returns an md5 hash of a given string
	 *
	 * @param s
	 * @return md5 hash
	 */
	def md5(s: String) = {
		val hashFunction = com.google.common.hash.Hashing.md5()
		// reminiscence to mitchell hashimoto - creator of the fabulous vagrant
		val hashimoto = hashFunction.newHasher().putString(s).hash().toString
		hashimoto
	}

	/**
	 * Build MongoDB Documents out of each row
	 * @param csv
	 * @return
	 */
	def csvToMongo(csv: Map[String,String]) = {
		val builder = MongoDBObject.newBuilder
		for(m <- csv) {
			builder += m
		}
		builder.result()
	}
}