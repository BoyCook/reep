package controllers

import play.api.mvc._
import play.api.libs.json.Json
import play.api.libs.concurrent.Execution.Implicits._
import services.{TypeDao}
import scala.concurrent.Future
import models.Model
import reactivemongo.bson.BSONObjectID

object TypeController extends Controller {

  implicit val taskFormFormat = Json.format[TaskTypeForm]

  case class TaskTypeForm(name: String,
                          code: String,
                          description: String) {
    def toTaskType: Model = Model(BSONObjectID.generate, name, code)
  }

  def index = Action {
    implicit req =>
      render {
        case Accepts.Html() => Ok(views.html.types()).as("text/html")
        case Accepts.Json() => Ok(Json.obj("message" -> "No JSON here - HTML only")).as("application/json")
      }
  }

  def getAll(page: Int, perPage: Int) = Action.async {
    implicit req =>
      for {
        count <- TypeDao.count
        types <- TypeDao.findAll(page, perPage)
      } yield {
        val result = Ok(Json.toJson(types))
//        Calculate paging headers, if necessary
        val next = if (count > (page + 1) * perPage) Some("next" -> (page + 1)) else None
        val prev = if (page > 0) Some("prev" -> (page - 1)) else None
        val links = next ++ prev
        if (links.isEmpty) {
          result
        } else {
          result.withHeaders("Link" -> links.map {
            case (rel, p) =>
              "<" + routes.TypeController.getAll(p, perPage).absoluteURL() + ">; rel=\"" + rel + "\""
          }.mkString(", "))
        }
      }
  }

  def get(code: String) = Action.async {
    implicit req => TypeDao.find(code).map{
      case Some(taskType) => Ok(Json.toJson(taskType))
      case None => NotFound(Json.obj("message" -> "No such type"))
    }
  }

  //TODO: validate params match object
  def update(code: String) = Action.async(parse.json) {
    req =>
      Json.fromJson[TaskTypeForm](req.body).fold(
        invalid => Future.successful(BadRequest("Bad type form")),
        form => TypeDao.save(form.toTaskType).map(_ => Created)
      )
  }

  def add = update(null)
}
