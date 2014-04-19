package services

import play.modules.reactivemongo.ReactiveMongoPlugin
import play.modules.reactivemongo.json.collection.JSONCollection
import play.modules.reactivemongo.json.BSONFormats._
import play.api.Play.current
import models._
import models.Model._
import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.json.{JsObject, Json}
import reactivemongo.api.QueryOpts
import reactivemongo.core.commands.Count
import reactivemongo.bson.BSONObjectID
import reactivemongo.api.indexes.{IndexType, Index}

/** A data access object for types backed by a MongoDB collection */
object TypeDao {

  /** The types collection */
  private def collection = ReactiveMongoPlugin.db.collection[JSONCollection]("types")
//  collection.indexesManager.ensure(Index(List("name" -> IndexType.Ascending), unique = true))
  collection.indexesManager.ensure(Index(Seq("name" -> IndexType.Ascending), name = Some("code_idx"), unique = true, sparse = true))

  /**
   * Save a type.
   *
   * @return The saved type, once saved.
   */
  def save(taskType: Model): Future[Model] = {
    collection.save(taskType).map {
      case ok if ok.ok => taskType
      case error => throw new RuntimeException(error.message)
    }
  }

  /**
   * Find all the types.
   *
   * @param page The page to retrieve, 0 based.
   * @param perPage The number of results per page.
   * @return All of the types.
   */
  def findAll(page: Int, perPage: Int): Future[Seq[Model]] = {
    collection.find(Json.obj())
      .options(QueryOpts(page * perPage))
      .sort(Json.obj("_id" -> -1))
      .cursor[Model]
      .collect[Seq](perPage)
  }

  /**
   * Get an individual type by name
   *
   * @param code of the type
   * @return the individual type
   */
  def find(code: String): Future[Option[Model]] = {
    collection
      .find(Json.obj("name" -> code))
      .one[Model]
  }

  /**
   * Get a individual type by ID
   * @param id of the type
   * @return the individual type
   */
  def findById(id: BSONObjectID): Future[Option[Model]] = {
    collection
    .find(Json.obj("id" -> id))
    .one[Model]
  }

  /** The total number of types */
  def count: Future[Int] = {
    ReactiveMongoPlugin.db.command(Count(collection.name))
  }
}
