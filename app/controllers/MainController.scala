package controllers

import play.api.mvc._
import play.api.Routes
import services.EventDao
import play.api.libs.EventSource
import controllers.routes

object MainController extends Controller {

  /**
   * The index page.  This is the main entry point, seeing as this is a single page app.
   */
  def index(path: String) = Action {
    Ok(views.html.index())
  }

  /** The javascript router. */
  def router = Action { implicit req =>
    Ok(
      Routes.javascriptRouter("routes")(
        routes.javascript.MainController.events,
        routes.javascript.ModelController.getAll,
        routes.javascript.ModelController.get,
        routes.javascript.ModelController.add
      )
    ).as("text/javascript")
  }

  /** Server Sent Events endpoint. */
  def events = Action {
    Ok.feed(EventDao.stream &> EventSource()).as(EVENT_STREAM)
  }

  def eventsForEmployee(ein: String) = Action {
    Ok.feed(EventDao.stream &> EventSource()).as(EVENT_STREAM)
  }
}
