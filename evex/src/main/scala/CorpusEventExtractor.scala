package issst.evex.kb

import scala.collection.JavaConversions._
import java.nio.file.Paths

import org.apache.uima.util.CasCreationUtils
import org.uimafit.factory.ExternalResourceFactory.createExternalResourceDescription
import org.uimafit.factory.{AnalysisEngineFactory, CollectionReaderFactory, TypeSystemDescriptionFactory}
import org.uimafit.pipeline.SimplePipeline

import ru.ksu.niimm.cll.uima.morph.opencorpora.MorphologyAnnotator
//import ru.kfu.itis.issst.uima.depparser.mst.GeneratePipelineDescriptorForDepParsing
import ru.kfu.itis.issst.uima.morph.lemmatizer.DescriptionGenerator
import ru.kfu.itis.cll.uima.cpe.XmiCollectionReader
import ru.kfu.itis.cll.uima.consumer.XmiWriter

import issst.evex.kb.util.LemmasDepsSerializer
import issst.evex.kb.annotators.IndicatorAnnotator
import issst.evex.kb.annotators.ArgumentsAnnotator

object CorpusEventExtractor {

  def main(args: Array[String]) {
    if (args.length != 2) {
      println("Please provide corpus directory path and output path.")
      System.exit(0)
    }

    extractEvents(args(0), args(1))
  }

  def extractEvents(inputDir: String, outputDir: String) {
    //val depLemmaPipelineDesc = DescriptionGenerator.getDescriptionWithDep()
    val xmiColReaderDesc = CollectionReaderFactory.createDescription(classOf[XmiCollectionReader], CasCreationUtils.mergeTypeSystems(List(TypeSystemDescriptionFactory.createTypeSystemDescription(Paths.get(inputDir).resolve(LemmasDepsSerializer.typeSystemFilename).toUri.toString), TypeSystemDescriptionFactory.createTypeSystemDescription("ru.kfu.itis.cll.uima.commons.Commons-TypeSystem"))), XmiCollectionReader.PARAM_INPUTDIR, inputDir)

    val jsonDataDesc = createExternalResourceDescription(classOf[JsonData], "http://srl.meteor.com/data.json")
    val indicatorAnnotatorDesc = AnalysisEngineFactory.createPrimitiveDescription(classOf[IndicatorAnnotator], IndicatorAnnotator.JSON_DATA_KEY, jsonDataDesc)
    val argumentsAnnotatorDesc = AnalysisEngineFactory.createPrimitiveDescription(classOf[ArgumentsAnnotator], IndicatorAnnotator.JSON_DATA_KEY, jsonDataDesc)

    val xmiWriterDesc = AnalysisEngineFactory.createPrimitiveDescription(classOf[XmiWriter], XmiWriter.PARAM_OUTPUTDIR, outputDir)

    val aggregateDesc = AnalysisEngineFactory.createAggregateDescription(indicatorAnnotatorDesc, argumentsAnnotatorDesc)
    //val aggregateDesc = AnalysisEngineFactory.createAggregateDescription(eventExtractorDesc)

    SimplePipeline.runPipeline(xmiColReaderDesc, aggregateDesc, xmiWriterDesc)

  }
}
