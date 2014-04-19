import controllers.{ModelController}
import models.{Model}
import services.ModelDao

import org.specs2.mutable.Specification
import MongoDBTestUtils._

import play.api.libs.json.Json
import play.api.test._
import play.api.test.Helpers._
import reactivemongo.bson.BSONObjectID

import scala.concurrent.Await
import scala.concurrent.duration.Duration

object ModelControllerSpec extends Specification {

  "the type controller" should {

    val fooJSON = Json.obj("name" -> "Foo Name",
                    "description" -> "Foo Desc")

    "save a type" in withMongoDb { implicit app =>
      status(ModelController.add()(FakeRequest().withBody(fooJSON))) must_== CREATED
      val types = Await.result(ModelDao.findAll(0, 10), Duration.Inf)
      types must haveSize(1)
      types.head.name must_== "Foo Name"
      types.head.description must_== "Foo Desc"
    }

//    "not save duplicate types" in withMongoDb { implicit app =>
//      status(TypeController.add()(FakeRequest().withBody(fooJSON))) must_== CREATED
//      status(TypeController.add()(FakeRequest().withBody(fooJSON))) must_== CONFLICT
//    }

    "get types" in withMongoDb { implicit app =>
      createModel("Foo Name", "Foo Desc")
      createModel("Bar Name", "Bar Desc")
      val types = Json.parse(contentAsString(ModelController.getAll(0, 10)(FakeRequest()))).as[Seq[Model]]
      types must haveSize(2)

      types(1).name must_== "Foo Name"
      types(1).description must_== "Foo Desc"

      types(0).name must_== "Bar Name"
      types(0).description must_== "Bar Desc"
    }

    "page types" in withMongoDb { implicit app =>
      for (i <- 1 to 30) {
        createModel("Name " + i, "Desc " + i)
      }
      def test(page: Int, perPage: Int) = {
        val result = ModelController.getAll(page, perPage)(FakeRequest())
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
