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

/** A data access object for model backed by a MongoDB collection */
object ModelDao {

  /** The model collection */
  private def collection = ReactiveMongoPlugin.db.collection[JSONCollection]("model")
  collection.indexesManager.ensure(Index(Seq("name" -> IndexType.Ascending), name = Some("name_idx"), unique = true, sparse = true))

  /**
   * Save a model.
   *
   * @return The saved model, once saved.
   */
  def save(taskType: Model): Future[Model] = {
    collection.save(taskType).map {
      case ok if ok.ok => taskType
      case error => throw new RuntimeException(error.message)
    }
  }

  /**
   * Find all the model.
   *
   * @param page The page to retrieve, 0 based.
   * @param perPage The number of results per page.
   * @return All of the model.
   */
  def findAll(page: Int, perPage: Int): Future[Seq[Model]] = {
    collection.find(Json.obj())
      .options(QueryOpts(page * perPage))
      .sort(Json.obj("_id" -> -1))
      .cursor[Model]
      .collect[Seq](perPage)
  }

  /**
   * Get an individual model by name
   *
   * @param code of the model
   * @return the individual model
   */
  def find(code: String): Future[Option[Model]] = {
    collection
      .find(Json.obj("name" -> code))
      .one[Model]
  }

  /**
   * Get a individual model by ID
   * @param id of the model
   * @return the individual model
   */
  def findById(id: BSONObjectID): Future[Option[Model]] = {
    collection
    .find(Json.obj("id" -> id))
    .one[Model]
  }

  /** The total number of model */
  def count: Future[Int] = {
    ReactiveMongoPlugin.db.command(Count(collection.name))
  }
}
