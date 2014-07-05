package issst.evex.kb

import play.api.libs.json._
import dispatch._
import Defaults._
import org.apache.uima.resource.metadata.TypeSystemDescription
import org.apache.uima.resource.metadata.impl.TypeSystemDescription_impl
import java.nio.charset.StandardCharsets

object B2UConfigImporter {
  val types = TSDImporter.getTypes("http://srl.meteor.com/event-types.json")

  def main(args: Array[String]) {

    val paramsXml = <configurationParameterSettings>
      <nameValuePair>
        <name>EntitiesToBrat</name>
        <value>
          <array>
          {
            for (en2b <- entitiesToBrat) yield
            <string>{en2b}</string>
          }
          </array>
        </value>
      </nameValuePair>
      <nameValuePair>
        <name>EventsToBrat</name>
        <value>
          <array>
          {
            for (ev2b <- eventsToBrat) yield
            <string>{ev2b}</string>
          }
          </array>
        </value>
      </nameValuePair>
    </configurationParameterSettings>

    //for ((eventName, argumentNames) <- types) {
      //addEventType(tsd, eventName, argumentNames)
    //}

    println(paramsXml)
  }

  def entitiesToBrat = {
    for (argumentName: String <- types.values.flatten.toSet) yield TSDImporter.argumentType(argumentName)
  }

  def eventsToBrat = {
    for ((eventName, argumentNames) <- types) yield
      TSDImporter.eventType(eventName) + ": " + argumentNames.map(TSDImporter.argumentFeature).mkString(", ")
  }
}
