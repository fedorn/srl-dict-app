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

object CorpusEventExtractor {

  def main(args: Array[String]) {
    if (args.length != 2) {
      println("Please provide corpus directory path and output path.")
      System.exit(0)
    }
    //val depLemmaPipelineDesc = DescriptionGenerator.getDescriptionWithDep()
    val xmiColReaderDesc = CollectionReaderFactory.createDescription(classOf[XmiCollectionReader], CasCreationUtils.mergeTypeSystems(List(TypeSystemDescriptionFactory.createTypeSystemDescription(Paths.get(args(0)).resolve(LemmasDepsSerializer.typeSystemFilename).toUri.toString), TypeSystemDescriptionFactory.createTypeSystemDescription("ru.kfu.itis.cll.uima.commons.Commons-TypeSystem"))), XmiCollectionReader.PARAM_INPUTDIR, args(0))

    val jsonDataDesc = createExternalResourceDescription(classOf[JsonData], "http://srl.meteor.com/data.json")
    val eventExtractorDesc = AnalysisEngineFactory.createPrimitiveDescription(classOf[IndicatorAnnotator], IndicatorAnnotator.JSON_DATA_KEY, jsonDataDesc)

    val xmiWriterDesc = AnalysisEngineFactory.createPrimitiveDescription(classOf[XmiWriter], XmiWriter.PARAM_OUTPUTDIR, args(1))

    val aggregateDesc = AnalysisEngineFactory.createAggregateDescription(eventExtractorDesc)
    //val aggregateDesc = AnalysisEngineFactory.createAggregateDescription(eventExtractorDesc)

    SimplePipeline.runPipeline(xmiColReaderDesc, aggregateDesc, xmiWriterDesc)
  }
}
