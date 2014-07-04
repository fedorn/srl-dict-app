package issst.evex.kb

import play.api.libs.json._
import dispatch._
import Defaults._
import org.apache.uima.resource.metadata.TypeSystemDescription
import org.apache.uima.resource.metadata.impl.TypeSystemDescription_impl
import java.nio.charset.StandardCharsets

object B2UConfigImporter {
  def main(args: Array[String]) {
    val types = TSDImporter.getTypes("http://srl.meteor.com/event-types.json")

    val paramsXml = <configurationParameterSettings>
      <nameValuePair>
        <name>EntitiesToBrat</name>
        <value>
          <array>
          {
            for (argumentName: String <- types.values.flatten.toSet) yield
            <string>{TSDImporter.argumentType(argumentName)}</string>
          }
          </array>
        </value>
      </nameValuePair>
      <nameValuePair>
        <name>EventsToBrat</name>
        <value>
          <array>
          {
            for ((eventName, argumentNames) <- types) yield
            <string>{TSDImporter.eventType(eventName) + ": " + argumentNames.map(TSDImporter.argumentFeature).mkString(", ")}</string>
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
}
