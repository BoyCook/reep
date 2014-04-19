import controllers.{TypeController}
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

object TypeControllerSpec extends Specification {

  "the type controller" should {

    val fooJSON = Json.obj("name" -> "Foo Name",
      "name" -> "FOO",
      "description" -> "Foo Desc")

    "save a type" in withMongoDb { implicit app =>
      status(TypeController.add()(FakeRequest().withBody(fooJSON))) must_== CREATED
      val types = Await.result(TypeDao.findAll(0, 10), Duration.Inf)
      types must haveSize(1)
      types.head.name must_== "Foo Name"
      types.head.description must_== "Foo Desc"
    }

//    "not save duplicate types" in withMongoDb { implicit app =>
//      status(TypeController.add()(FakeRequest().withBody(fooJSON))) must_== CREATED
//      status(TypeController.add()(FakeRequest().withBody(fooJSON))) must_== CONFLICT
//    }

    "get types" in withMongoDb { implicit app =>
      createType("Foo Name", "Foo Desc")
      createType("Bar Name", "Bar Desc")
      val types = Json.parse(contentAsString(TypeController.getAll(0, 10)(FakeRequest()))).as[Seq[Model]]
      types must haveSize(2)

      types(1).name must_== "Foo Name"
      types(1).description must_== "Foo Desc"

      types(0).name must_== "Bar Name"
      types(0).description must_== "Bar Desc"
    }

    "page types" in withMongoDb { implicit app =>
      for (i <- 1 to 30) {
        createType("Name " + i, "Desc " + i)
      }
      def test(page: Int, perPage: Int) = {
        val result = TypeController.getAll(page, perPage)(FakeRequest())
        val (prev, next) = header("Link", result).map { link =>
          (extractLink("prev", link), extractLink("next", link))
        }.getOrElse((None, None))
        (prev.isDefined, next.isDefined, Json.parse(contentAsString(result)).as[Seq[Model]].size)
      }

      test(0, 10) must_== (false, true, 10)
      test(1, 10) must_== (true, true, 10)
      test(2, 10) must_== (true, false, 10)
      test(3, 10) must_== (true, false, 0)
      test(0, 30) must_== (false, false, 30)
      test(0, 31) must_== (false, false, 30)
      test(0, 29) must_== (false, true, 29)
    }
  }

  def extractLink(rel: String, link: String) = {
    """<([^>]*)>;\s*rel="%s"""".format(rel).r.findFirstMatchIn(link).map(_.group(1))
  }
}
