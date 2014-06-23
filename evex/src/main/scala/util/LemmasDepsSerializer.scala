package issst.evex.kb.util

import java.nio.file.Paths
import java.nio.file.Files
import java.nio.charset.StandardCharsets
import org.apache.uima.util.CasCreationUtils
import org.uimafit.factory.ExternalResourceFactory.createExternalResourceDescription
import org.uimafit.factory.{AnalysisEngineFactory, CollectionReaderFactory, TypeSystemDescriptionFactory}
import org.uimafit.pipeline.SimplePipeline
import ru.ksu.niimm.cll.uima.morph.opencorpora.MorphologyAnnotator
import ru.kfu.itis.issst.uima.morph.lemmatizer.DescriptionGenerator
import ru.kfu.itis.cll.uima.cpe.FileDirectoryCollectionReader
import ru.kfu.itis.cll.uima.consumer.XmiWriter

object LemmasDepsSerializer {
  val typeSystemFilename = "type-system"

  def main(args: Array[String]) {
    if (args.length != 2) {
      println("Please provide corpus directory path and output path.")
      System.exit(0)
    }
    //val depLemmaPipelineDesc = DescriptionGenerator.getDescriptionWithDep()
    val dirReader = CollectionReaderFactory.createCollectionReader(classOf[FileDirectoryCollectionReader], TypeSystemDescriptionFactory.createTypeSystemDescription("ru.kfu.itis.cll.uima.commons.Commons-TypeSystem"), FileDirectoryCollectionReader.PARAM_DIRECTORY_PATH, args(0))

    val depLemmaPipeline = AnalysisEngineFactory.createAggregate(DescriptionGenerator.getDescriptionWithDep())

    val fileWriter = Files.newBufferedWriter(Paths.get(args(1)).resolve(typeSystemFilename + ".xml"), StandardCharsets.UTF_8)
    depLemmaPipeline.getAnalysisEngineMetaData.getTypeSystem.toXML(fileWriter)
    fileWriter.close()

    val xmiWriter = AnalysisEngineFactory.createPrimitive(classOf[XmiWriter], XmiWriter.PARAM_OUTPUTDIR, args(1))

    SimplePipeline.runPipeline(dirReader, depLemmaPipeline, xmiWriter)
  }
}
