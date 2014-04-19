import controllers.{MainController, TypeController}
import models.{Model}
import services.TypeDao

import org.specs2.mutable.Specification
import MongoDBTestUtils._

import play.api.libs.json.Json
import play.api.test._
import play.api.test.Helpers._
import reactivemongo.bson.BSONObjectID

import scala.concurrent.Await
import scala.concurrent.duration.Duration

object MainControllerSpec extends Specification {

//  "the main controller" should {
//    "get an index" in {
//      status(MainController.index("")(FakeRequest())) must_== OK
//    }
//
//    "get employee home" in {
//      status(MainController.employee("123456789")(FakeRequest())) must_== OK
//    }
//  }//.pendingUntilFixed("Issue testing against templated HTML")
}
