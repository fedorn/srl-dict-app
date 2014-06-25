package issst.evex.kb.annotators

import scala.collection.JavaConversions._
import play.api.libs.json._
import org.uimafit.component.CasAnnotator_ImplBase
import org.apache.uima.cas.TypeSystem
import org.apache.uima.cas.Type
import org.opencorpora.cas.Word
import org.apache.uima.cas.text.AnnotationFS
import org.apache.uima.jcas.JCas
import org.apache.uima.cas.FeatureStructure
import org.opencorpora.cas.Wordform
import org.apache.uima.UimaContext
import org.apache.uima.cas.CAS
import org.uimafit.util.CasUtil
import org.uimafit.util.JCasUtil
import org.uimafit.descriptor.ExternalResource
import org.uimafit.util.JCasUtil.select
import ru.kfu.itis.issst.uima.depparser.Dependency
import ru.kfu.itis.cll.uima.commons.DocumentMetadata
import issst.evex.kb.JsonData
import issst.evex.kb.TSDImporter

object ArgumentsAnnotator {
  val caseGrammems = Set("nomn", "gent", "datv", "accs", "ablt", "loct")
}

class ArgumentsAnnotator extends CasAnnotator_ImplBase {
  @ExternalResource(key = IndicatorAnnotator.JSON_DATA_KEY)
  private var jsonData: JsonData = null
  private var casesToArgs: collection.mutable.Map[Tuple3[String, String, String], String] = collection.mutable.Map()
  private var prepCasesToArgs: collection.mutable.Map[Tuple4[String, String, String, String], String] = collection.mutable.Map()

  private var indicatorBaseType: Type = null

  override def typeSystemInit(aTypeSystem: TypeSystem) {
    indicatorBaseType = aTypeSystem.getType(TSDImporter.indicatorBaseType)
  }

  override def initialize(aContext: UimaContext) {
    super.initialize(aContext)
    for ((eventType, indicatorsMap) <- jsonData.json;
         (indicator, arguments) <- indicatorsMap;
         argumentMap <- arguments if argumentMap.contains("type_arg")) {
      if (argumentMap.contains("prep")) {
        prepCasesToArgs += ((TSDImporter.eventType(eventType), indicator, argumentMap.get("prep").get, argumentMap.get("noun_case").get) -> argumentMap.get("type_arg").get)
      } else {
        casesToArgs += ((TSDImporter.eventType(eventType), indicator, argumentMap.get("noun_case").get) -> argumentMap.get("type_arg").get)
      }
    }
    //println(casesToArgs)
    //println(prepCasesToArgs)
  }

  override def process(cas: CAS) {
    val jCas = cas.getJCas()
    for {dependency: Dependency <- select(jCas, classOf[Dependency])
         dependent = getDependent(dependency)
         head = getHead(dependency)
         if !head.isEmpty && !dependent.isEmpty && isNoun(dependent.get)
         nounCase = getCase(dependent.get)
         if !nounCase.isEmpty} {
      // For deps without preps
      for {headFS <- CasUtil.selectCovered(indicatorBaseType, dependency.getHead())
           arg = casesToArgs.get((headFS.getType.getName, head.get.getLemma, nounCase.get))
           if !arg.isEmpty} {
        println(select(jCas, classOf[DocumentMetadata]).head.getSourceUri())
        println(dependency.getHead.getCoveredText)
        println(dependency.getDependent.getCoveredText)
        println(headFS.getType.getName)
        println(arg.get)
        println()

        annotateArgument(cas, headFS, None, dependency.getDependent, arg.get)
      }
      //println(dependency.getHead.getCoveredText)
      //println(dependency.getDependent.getCoveredText)
      //println(headFS.getType.getName)
      //println()
      val prepWord = dependency.getHead
      val prepWordform = getWordform(prepWord)
      if (!prepWordform.isEmpty && isPrep(prepWordform.get)) {

        val prepDep = JCasUtil.selectCovered(classOf[Dependency], prepWord)
        if (prepDep.size > 0) {
          val indicatorWord = prepDep.head.getHead
          val indicatorWordform = getWordform(indicatorWord)

          if (indicatorWord != null) {

            for {headFS <- CasUtil.selectCovered(indicatorBaseType, indicatorWord)
                 arg = prepCasesToArgs.get((headFS.getType.getName, indicatorWordform.get.getLemma, prepWordform.get.getLemma, nounCase.get))
                 if !arg.isEmpty} {
              println(select(jCas, classOf[DocumentMetadata]).head.getSourceUri())
              println(indicatorWord.getCoveredText)
              println(dependency.getHead.getCoveredText)
              println(dependency.getDependent.getCoveredText)
              println(headFS.getType.getName)
              println(arg.get)
              println()

              annotateArgument(cas, headFS, Option(prepWord), dependency.getDependent, arg.get)
            }
          }
        }

        //for {headFS <- CasUtil.selectCovered(indicatorBaseType, prepWord)} {
           //println((headFS.getType.getName, head.get.getLemma, prepWord.getCoveredText, nounCase.get))
             //val arg = prepCasesToArgs.get((headFS.getType.getName, head.get.getLemma, prepWord.getCoveredText, nounCase.get))
             ////if !arg.isEmpty} {
          ////println(dependency.getHead.getCoveredText)
          ////println(dependency.getDependent.getCoveredText)
          ////println(headFS.getType.getName)
          ////println(arg.get)
          ////println()
        //}
    //println(dependency.getHead.getCoveredText)
    //println(dependency.getDependent.getCoveredText)
    //println(headFS.getType.getName)
    //println()
      }
    }
  }

  private def annotateArgument(cas: CAS, indicatorFS: AnnotationFS, prep: Option[Word], argumentWord: Word, argumentType: String) {
    val argCasType = cas.getTypeSystem().getType(TSDImporter.argumentType(argumentType))
    val argumentFS = cas.createAnnotation(argCasType, argumentWord.getBegin, argumentWord.getEnd)
    if (!prep.isEmpty) {
      val prepFeature = argCasType.getFeatureByBaseName(TSDImporter.prepFeature)
      argumentFS.setFeatureValue(prepFeature, prep.get)
    }
    cas.addFsToIndexes(argumentFS)

    val argFeature = indicatorFS.getType().getFeatureByBaseName(TSDImporter.argumentFeature(argumentType))
    indicatorFS.setFeatureValue(argFeature, argumentFS)
  }

  private def getHead(dependency: Dependency) = {
    getWordform(dependency.getHead())
  }

  private def getDependent(dependency: Dependency) = {
    getWordform(dependency.getDependent())
  }

  private def getWordform(word: Word) = {
    Option(word) map {
      _.getWordforms.toArray.head.asInstanceOf[Wordform]
    }
  }

  private def isNoun(wordform: Wordform) = {
    wordform.getGrammems != null && wordform.getGrammems().toArray().toSet.contains("NOUN")
  }

  private def isPrep(wordform: Wordform) = {
    wordform.getGrammems != null && wordform.getGrammems().toArray().toSet.contains("PREP")
  }

  private def getCase(wordform: Wordform) = {
    if (wordform.getGrammems != null) {
      (wordform.getGrammems().toArray().toSet & ArgumentsAnnotator.caseGrammems).headOption
    } else {
      None
    }
  }
}
