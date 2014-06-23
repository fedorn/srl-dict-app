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

object IndicatorAnnotator {
  final val JSON_DATA_KEY = "JsonDataKey"
}

class IndicatorAnnotator extends JCasAnnotator_ImplBase {
  @ExternalResource(key = IndicatorAnnotator.JSON_DATA_KEY)
  private var jsonData: JsonData = null

  override def process(jCas: JCas) {
    select(jCas, classOf[Word]).foreach((word: Word) => {
      word.getWordforms.toArray.foreach((wordformFS: FeatureStructure) => {
        val wordform = wordformFS.asInstanceOf[Wordform]
        println(wordform.getLemma())
      })
    })
  }
}
