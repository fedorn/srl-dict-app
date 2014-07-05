package issst.evex.kb.util

import scala.collection.JavaConversions._
import java.nio.file.Paths

import org.apache.uima.util.CasCreationUtils
import org.uimafit.factory.{AnalysisEngineFactory, CollectionReaderFactory, TypeSystemDescriptionFactory}
import org.uimafit.factory.ExternalResourceFactory.createExternalResourceDescription
import org.uimafit.pipeline.SimplePipeline
import ru.kfu.itis.cll.uima.cpe.XmiCollectionReader
import ru.kfu.itis.issst.uima.brat.UIMA2BratAnnotator

import issst.evex.kb.JsonData
import issst.evex.kb.annotators.IndicatorAnnotator
import issst.evex.kb.annotators.ArgumentsAnnotator
import issst.evex.kb.B2UConfigImporter

object B2UCpeLauncher extends App {
  if (args.length != 2) {
    println("Please provide corpus directory path and output path.")
    System.exit(0)
  }

  launchCpe(args(0), args(1))

  def launchCpe(inputDir: String, outputDir: String) {
    val xmiColReaderDesc = CollectionReaderFactory.createDescription(classOf[XmiCollectionReader], CasCreationUtils.mergeTypeSystems(List(TypeSystemDescriptionFactory.createTypeSystemDescription(Paths.get(inputDir).resolve(LemmasDepsSerializer.typeSystemFilename).toUri.toString), TypeSystemDescriptionFactory.createTypeSystemDescription("ru.kfu.itis.cll.uima.commons.Commons-TypeSystem"))), XmiCollectionReader.PARAM_INPUTDIR, inputDir)

    val jsonDataDesc = createExternalResourceDescription(classOf[JsonData], "http://srl.meteor.com/data.json")
    val indicatorAnnotatorDesc = AnalysisEngineFactory.createPrimitiveDescription(classOf[IndicatorAnnotator], IndicatorAnnotator.JSON_DATA_KEY, jsonDataDesc)
    val argumentsAnnotatorDesc = AnalysisEngineFactory.createPrimitiveDescription(classOf[ArgumentsAnnotator], IndicatorAnnotator.JSON_DATA_KEY, jsonDataDesc)

    val annWriterDesc = AnalysisEngineFactory.createAnalysisEngineDescription("ru.kfu.itis.issst.uima.brat.UIMA2BratAnnotator", UIMA2BratAnnotator.BRAT_OUT, outputDir, UIMA2BratAnnotator.ENTITIES_TO_BRAT, B2UConfigImporter.entitiesToBrat.toArray, UIMA2BratAnnotator.EVENTS_TO_BRAT, B2UConfigImporter.eventsToBrat.toArray)

    val aggregateDesc = AnalysisEngineFactory.createAggregateDescription(indicatorAnnotatorDesc, argumentsAnnotatorDesc)
    //val aggregateDesc = AnalysisEngineFactory.createAggregateDescription(eventExtractorDesc)

    SimplePipeline.runPipeline(xmiColReaderDesc, aggregateDesc, annWriterDesc)
  }
}
