package issst.evex.kb

import dispatch._
import Defaults._
import play.api.libs.json._
import org.apache.uima.resource.SharedResourceObject
import org.apache.uima.resource.DataResource

class JsonData extends SharedResourceObject {
  var json: JsValue = null

  def load(aData: DataResource) {
    val uri = aData.getUri().toString()
    val dataUrl = url(uri)
    val jsonString = Http(dataUrl OK as.String)
    json = Json.parse(jsonString())
  }
}
