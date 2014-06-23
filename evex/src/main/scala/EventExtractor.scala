package issst.evex.kb

import java.nio.file.Paths

import org.apache.uima.util.CasCreationUtils
import org.uimafit.factory.ExternalResourceFactory.createExternalResourceDescription
import org.uimafit.factory.AnalysisEngineFactory
import org.uimafit.pipeline.SimplePipeline

import ru.ksu.niimm.cll.uima.morph.opencorpora.MorphologyAnnotator
//import ru.kfu.itis.issst.uima.depparser.mst.GeneratePipelineDescriptorForDepParsing
import ru.kfu.itis.issst.uima.morph.lemmatizer.DescriptionGenerator

object EventExtractor {

  def main(args: Array[String]) {
    val depLemmaPipelineDesc = DescriptionGenerator.getDescriptionWithDep()

    val jsonDataDesc = createExternalResourceDescription(classOf[JsonData], "http://srl.meteor.com/data.json")
    val eventExtractorDesc = AnalysisEngineFactory.createPrimitiveDescription(classOf[IndicatorAnnotator], IndicatorAnnotator.JSON_DATA_KEY, jsonDataDesc)

    val aggregateDesc = AnalysisEngineFactory.createAggregateDescription(depLemmaPipelineDesc, eventExtractorDesc)
    //val aggregateDesc = AnalysisEngineFactory.createAggregateDescription(eventExtractorDesc)

    val jCas = CasCreationUtils.createCas(aggregateDesc).getJCas
    jCas.setDocumentText("Мальчик купил пирожок")
    SimplePipeline.runPipeline(jCas, aggregateDesc)
    println
  }
}
