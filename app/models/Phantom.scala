package models

import scala.concurrent.Future
import scala.util.{Failure, Success}

import org.joda.time.DateTime
import org.joda.time.DateTimeZone.UTC

import play.api.libs.json.Json
import reactivemongo.bson.BSONObjectID
import play.modules.reactivemongo.json.BSONFormats._

import play.api.Logger
import play.api.Play.current
import play.api.libs.concurrent.Execution.Implicits.defaultContext

import reactivemongo.api._
import reactivemongo.bson._
import reactivemongo.core.commands.LastError


/**
	* A phantom class
	*
	*@param _id The BSON object id of the phantom
	*@param message The message
	*/
	
case class Phantom(
	_id: BSONObjectID, 
	whenCreated  : Option[DateTime],
	whenUpdated  : Option[DateTime],
	message: String
)

object Phantom {

  implicit object PhantomBSONReader extends BSONDocumentReader[Phantom] {
    def read(doc: BSONDocument): Phantom = {
      Phantom(
        doc.getAs[BSONObjectID]("_id").get,
        doc.getAs[BSONDateTime]("whenCreated").map(dt => new DateTime(dt.value)),
        doc.getAs[BSONDateTime]("whenUpdated").map(dt => new DateTime(dt.value)),
        doc.getAs[BSONString]("message").get.value
      )
    }
  }

  implicit object PhantomBSONWriter extends BSONDocumentWriter[Phantom] {
    def write(phantom: Phantom) = {
      val bson = BSONDocument(
        "_id" -> BSONObjectID.generate,
        "whenCreated"  -> phantom.whenCreated.map(dt => BSONDateTime(dt.getMillis)),
				"whenUpdated"  -> phantom.whenUpdated.map(dt => BSONDateTime(dt.getMillis)),
        "message" -> BSONString(phantom.message)
        )
      bson
    }
  }

}