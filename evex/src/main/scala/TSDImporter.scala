package issst.evex.kb

import play.api.libs.json._
import dispatch._
import Defaults._
import org.apache.uima.resource.metadata.TypeSystemDescription
import org.apache.uima.resource.metadata.impl.TypeSystemDescription_impl
import java.nio.file.{Paths, Files}
import java.nio.charset.StandardCharsets

object TSDImporter {
  val indicatorBaseType = "issst.evex.event.EventBase"
  val argumentBaseType = "issst.evex.argument.ArgumentBase"
  val tsdPath = Paths.get("src/main/resources/EvexTypeSystem.xml")

  def main(args: Array[String]) {
    val types = getTypes("http://srl.meteor.com/event-types.json")

    val tsd = bootstrapTSD()

    for ((eventName, argumentNames) <- types) {
      addEventType(tsd, eventName, argumentNames)
    }

    for (argumentName: String <- types.values.flatten.toSet) {
      addArgumentType(tsd, argumentName)
    }

    val fileWriter = Files.newBufferedWriter(tsdPath, StandardCharsets.UTF_8)
    tsd.toXML(fileWriter)
    fileWriter.close()
  }

  private def getTypes(urlString: String) = {
    val typesUrl = url(urlString)
    val jsonString = Http(typesUrl OK as.String)
    val json: JsValue = Json.parse(jsonString())
    json.as[Map[String, List[String]]]
  }

  private def bootstrapTSD() = {
    val tsd = new TypeSystemDescription_impl()

    tsd.addType(indicatorBaseType, "Base type for event indicators", "uima.tcas.Annotation")
    val argTypeDesc = tsd.addType(argumentBaseType, "Base type for event arguments", "uima.tcas.Annotation")
    argTypeDesc.addFeature("prep", "Preposition of argument", "org.opencorpora.cas.Word")

    tsd
  }

  private def addEventType(tsd: TypeSystemDescription, eventName: String, argumentNames: List[String]) = {
    val eventTypeDesc = tsd.addType(eventType(eventName), eventName + " event indicator", indicatorBaseType)
    for (argumentName <- argumentNames) {
      eventTypeDesc.addFeature(argumentName.toLowerCase, argumentName + " of event", argumentType(argumentName))
    }
  }

  private def addArgumentType(tsd: TypeSystemDescription, argumentName: String) {
    tsd.addType(argumentType(argumentName), argumentName + " event argument", argumentBaseType)
  }

  def eventType(eventName: String) = {
    "issst.evex.event." + camelCaseize(eventName.toLowerCase.capitalize)
  }

  def argumentType(argumentName: String) = {
    "issst.evex.argument." + argumentName
  }

  private def camelCaseize(s: String) = {
    """-([a-z])""".r replaceAllIn(s,  m => m.group(1).toUpperCase)
  }
}
