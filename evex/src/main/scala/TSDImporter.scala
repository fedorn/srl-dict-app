package issst.evex.kb

import play.api.libs.json._
import dispatch._, Defaults._
import org.apache.uima.resource.metadata.{TypeSystemDescription, TypeDescription}
import org.uimafit.factory.TypeSystemDescriptionFactory
import ru.kfu.cll.uima.tokenizer.InitialTokenizer
import java.io.StringWriter

object TSDImporter {
  val indicatorBaseType = "issst.evex.indicator.Base"
  val argumentBaseType = "issst.evex.argument.Base"

  def makeImport(): String = {
    val types = getTypes("http://srl.meteor.com/event-types.json")

    val tsd = bootstrapTSD()

    for ((eventName, argumentNames) <- types) {
      addEventType(tsd, eventName, argumentNames)
    }

    for (argumentName: String <- types.values.flatten.toSet) {
      addArgumentType(tsd, argumentName)
    }

    val stringWriter = new StringWriter()
    tsd.toXML(stringWriter)
    stringWriter.toString
  }

  private def getTypes(urlString: String) = {
    val typesUrl = url(urlString)
    val jsonString = Http(typesUrl OK as.String)
    val json: JsValue = Json.parse(jsonString())
    json.as[Map[String, List[String]]]
  }

  private def bootstrapTSD() = {
    val tsd = TypeSystemDescriptionFactory.createTypeSystemDescription()

    tsd.addType(indicatorBaseType, "Base type for event indicators", "uima.tcas.Annotation")
    tsd.addType(argumentBaseType, "Base type for event arguments", "uima.tcas.Annotation")

    tsd
  }

  private def addEventType(tsd: TypeSystemDescription, eventName: String, argumentNames: List[String]) = {
    val eventTypeDesc = tsd.addType(eventType(eventName), eventName + " event indicator", indicatorBaseType)
    for (argumentName <- argumentNames) {
      eventTypeDesc.addFeature(argumentName, argumentName + " of event", argumentType(argumentName))
    }
  }

  private def addArgumentType(tsd: TypeSystemDescription, argumentName: String) {
    tsd.addType("issst.evex.argument." + argumentType(argumentName), argumentName + " event argument", argumentBaseType)
  }

  private def eventType(eventName: String) = {
    "issst.evex.indicator." + eventName.toLowerCase.capitalize
  }

  private def argumentType(argumentName: String) = {
    "issst.evex.argument." + argumentName.toLowerCase.capitalize
  }
}
