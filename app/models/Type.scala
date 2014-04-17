package models

import play.api.libs.json.Json
import reactivemongo.bson.BSONObjectID
import play.modules.reactivemongo.json.BSONFormats._

/**
 * Created by boycook on 4/11/14.
 */

case class Type(_id: BSONObjectID,
                    name: String,
                    code: String,
                    description: String,
                    businessFunction: String,
                    subFunction: String,
                    system: String,
                    lob: String,
                    uriPattern: String)

object Type {
  implicit val typeFormat = Json.format[Type]
}
