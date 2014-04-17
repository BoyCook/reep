import controllers.{TypeController}
import models.{Type}
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
      "code" -> "FOO",
      "description" -> "Foo Desc",
      "businessFunction" -> "Foo Function",
      "subFunction" -> "Foo Sub-Function",
      "system" -> "Foo System",
      "lob" -> "Foo LOB",
      "uriPattern" -> "Foo URI")

    "save a type" in withMongoDb { implicit app =>
      status(TypeController.add()(FakeRequest().withBody(fooJSON))) must_== CREATED
      val types = Await.result(TypeDao.findAll(0, 10), Duration.Inf)
      types must haveSize(1)
      types.head.name must_== "Foo Name"
      types.head.code must_== "FOO"
      types.head.description must_== "Foo Desc"
      types.head.businessFunction must_== "Foo Function"
      types.head.subFunction must_== "Foo Sub-Function"
      types.head.system must_== "Foo System"
      types.head.lob must_== "Foo LOB"
      types.head.uriPattern must_== "Foo URI"
    }

//    "not save duplicate types" in withMongoDb { implicit app =>
//      status(TypeController.add()(FakeRequest().withBody(fooJSON))) must_== CREATED
//      status(TypeController.add()(FakeRequest().withBody(fooJSON))) must_== CONFLICT
//    }

    "get types" in withMongoDb { implicit app =>
      createType("Foo Name", "FOO", "Foo Desc", "Foo Function", "Foo Sub-Function", "Foo System", "Foo LOB", "Foo URI")
      createType("Bar Name", "BAR", "Bar Desc", "Bar Function", "Bar Sub-Function", "Bar System", "Bar LOB", "Bar URI")
      val types = Json.parse(contentAsString(TypeController.getAll(0, 10)(FakeRequest()))).as[Seq[Type]]
      types must haveSize(2)

      types(1).name must_== "Foo Name"
      types(1).code must_== "FOO"
      types(1).description must_== "Foo Desc"
      types(1).businessFunction must_== "Foo Function"
      types(1).subFunction must_== "Foo Sub-Function"
      types(1).system must_== "Foo System"
      types(1).lob must_== "Foo LOB"
      types(1).uriPattern must_== "Foo URI"

      types(0).name must_== "Bar Name"
      types(0).code must_== "BAR"
      types(0).description must_== "Bar Desc"
      types(0).businessFunction must_== "Bar Function"
      types(0).subFunction must_== "Bar Sub-Function"
      types(0).system must_== "Bar System"
      types(0).lob must_== "Bar LOB"
      types(0).uriPattern must_== "Bar URI"      
    }

    "page types" in withMongoDb { implicit app =>
      for (i <- 1 to 30) {
        createType("Name " + i, "CODE " + i, "Desc " + i, "Function " + i, "Sub-Function " + i, "System " + i, "LOB " + i, "URI " + i)
      }
      def test(page: Int, perPage: Int) = {
        val result = TypeController.getAll(page, perPage)(FakeRequest())
        val (prev, next) = header("Link", result).map { link =>
          (extractLink("prev", link), extractLink("next", link))
        }.getOrElse((None, None))
        (prev.isDefined, next.isDefined, Json.parse(contentAsString(result)).as[Seq[Type]].size)
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
