import com.mongodb.casbah.Imports._
import play.api._
import libs.ws.WS
import models._
import se.radley.plugin.salat._

object Global extends GlobalSettings {

	override def onStart(app: Application) {
		if (Tag.count(DBObject(), Nil, Nil) <= 10) {
			Tag.save(Tag(
				title = "Agriculture"
			))
			Tag.save(Tag(
				title = "Travel"
			))
			Tag.save(Tag(
				title = "Earth"
			))
			Tag.save(Tag(
				title = "Parliament"
			))
			Tag.save(Tag(
				title = "Health"
			))
			Tag.save(Tag(
				title = "Science"
			))
			Tag.save(Tag(
				title = "Social"
			))
			Tag.save(Tag(
				title = "web 2.0"
			))
			Tag.save(Tag(
				title = "Education"))

			Tag.save(Tag(
				title = "Open Data"
			))
			Tag.save(Tag(
				title = "Population"
			))
			Tag.save(Tag(
				title = "Government"
			))
			Tag.save(Tag(
				title = "Geo"
			))
		}
	}

}