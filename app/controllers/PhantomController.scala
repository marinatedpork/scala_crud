package controllers

import play.api.mvc._
import play.api.libs.json.Json
import play.api.libs.concurrent.Execution.Implicits._
import models._
import reactivemongo.bson.BSONObjectID
import scala.concurrent.Future

import org.joda.time.DateTime
import org.joda.time.DateTimeZone.UTC

import play.api.Play.current
import play.api.libs.concurrent.Execution.Implicits.defaultContext

import reactivemongo.api._
import reactivemongo.api.collections.default.BSONCollection
import reactivemongo.bson.{BSONDateTime, BSONDocument, BSONDocumentReader, BSONDocumentWriter, BSONObjectID}
import reactivemongo.core.commands.LastError

object PhantomController extends Controller {

	import Phantom.PhantomBSONAccessor
	val mongoDriver = new MongoDriver
	val mongoConnection = mongoDriver.connection(List("localhost"))
	val mongoDb = mongoConnection("matt")
	val collection : BSONCollection = mongoDb.collection("phantoms")

	def index = Action.async {
		val selector = BSONDocument()
		val foundPhantoms = collection.
			find(selector).
			cursor[Phantom].
			collect[List]()
		foundPhantoms.map { phantom =>
			Ok(views.html.index(phantom))
		}
	}

	def create = Action { implicit request =>
		val params = request.body.asFormUrlEncoded.get
		val message = params("message")(0).toString
		val phantom = new Phantom(
			BSONObjectID.generate,
			whenCreated  = Some(DateTime.now(UTC)),
			whenUpdated  = None,
			message
		)
		collection.insert(phantom)
		Redirect(routes.PhantomController.index)
	}

	def edit(id: String) = Action.async {
		val selector = BSONDocument("_id" -> BSONObjectID(id))
		val foundPhantom = collection.find(selector).one[Phantom]
		foundPhantom.map { phantom => 
			phantom match {
				case Some(p) => Ok(views.html.edit(p))
				case None => Redirect(routes.PhantomController.index)
			}
		}
	}

	def update (id: String) = Action { implicit request =>
		val params = request.body.asFormUrlEncoded.get
		val message = params("message")(0).toString
		val selector = BSONDocument("_id" -> BSONObjectID(id))
		val modifier = BSONDocument(
			"$set" -> BSONDocument(
				"message"        -> message,
				"whenUpdated" -> BSONDateTime(DateTime.now(UTC).getMillis)
			)
		)
		val futureUpdate = collection.update(selector, modifier, multi = false)
		Redirect(routes.PhantomController.index)
	}

	def delete (id: String) = Action {
		val selector = BSONDocument("_id" -> BSONObjectID(id))
		collection.remove(selector, firstMatchOnly = true)
		Redirect(routes.PhantomController.index)
	}
}