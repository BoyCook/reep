package models

import play.api.libs.json.Json
import reactivemongo.bson.BSONObjectID
import play.modules.reactivemongo.json.BSONFormats._

/**
 * Created by boycook on 4/11/14.
 */

case class Model(_id: BSONObjectID,
                    name: String,
                    description: String)

object Model {
  implicit val typeFormat = Json.format[Model]
}
