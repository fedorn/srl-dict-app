package issst.evex.kb

import scala.collection.JavaConversions._
import play.api.libs.json._
import org.uimafit.component.JCasAnnotator_ImplBase
import org.apache.uima.cas.TypeSystem
import org.apache.uima.cas.Type
import org.uimafit.descriptor.ExternalResource
import org.uimafit.util.JCasUtil.select
import org.opencorpora.cas.Word
import org.apache.uima.cas.text.AnnotationFS
import org.apache.uima.jcas.JCas
import org.apache.uima.cas.FeatureStructure
import org.opencorpora.cas.Wordform
import org.apache.uima.UimaContext
import ru.kfu.itis.cll.uima.commons.DocumentMetadata

object IndicatorAnnotator {
  final val JSON_DATA_KEY = "JsonDataKey"
}

class IndicatorAnnotator extends JCasAnnotator_ImplBase {
  @ExternalResource(key = IndicatorAnnotator.JSON_DATA_KEY)
  private var jsonData: JsonData = null
  private var indicatorToEvents: collection.mutable.Map[String, Set[String]] = collection.mutable.Map()

  override def initialize(aContext: UimaContext) {
    super.initialize(aContext)
    for ((eventType, indicatorsMap) <- jsonData.json) {
      for ((indicator, _) <- indicatorsMap) {
        indicatorToEvents.put(indicator, indicatorToEvents.getOrElse(indicator, Set()) + eventType)
      }
    }
  }

  override def process(jCas: JCas) {
    val cas = jCas.getCas()
    select(jCas, classOf[Word]).foreach((word: Word) => {
      word.getWordforms.toArray.foreach((wordformFS: FeatureStructure) => {
        val lemma = wordformFS.asInstanceOf[Wordform].getLemma()
        if (indicatorToEvents.contains(lemma)) {

          println(select(jCas, classOf[DocumentMetadata]).head.getSourceUri())
          println(word.getCoveredText)
          println(indicatorToEvents(lemma))

          for (eventName <- indicatorToEvents(lemma)) {
            val eventTypeName = TSDImporter.eventType(eventName)
            val eventType = jCas.getTypeSystem().getType(eventTypeName)
            val indicatorAnnotation = cas.createAnnotation(eventType, word.getBegin(), word.getEnd())
            jCas.addFsToIndexes(indicatorAnnotation)
          }
        }
      })
    })
  }
}
