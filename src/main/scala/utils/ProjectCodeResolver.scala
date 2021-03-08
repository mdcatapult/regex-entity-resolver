package utils

import com.johnsnowlabs.nlp.annotators.RegexMatcher
import com.johnsnowlabs.nlp.{DocumentAssembler, Finisher, SparkNLP}
import model.{Element, Project}
import org.apache.spark.ml.Pipeline
import org.apache.spark.sql.functions.{arrays_zip, explode}

import scala.collection.mutable.ArrayBuffer

object ProjectCodeResolver {

  def main(args: Array[String]): Unit = {

    val ProjectCodes: String =
      "MDCP-0199. MDCP-0167."

    val sparkSession = SparkNLP.start()
    val sqlContext = sparkSession.sqlContext
    import sqlContext.implicits._

    val dataframe = Seq(ProjectCodes).toDF("text")

    val documentAssembler = new DocumentAssembler()
      .setInputCol("text")
      .setOutputCol("document")

    val regexTokenizer = new RegexMatcher()
      .setInputCols("document")
      .setOutputCol("regex")
      .setStrategy("MATCH_ALL")
      .setRules("src/test/resources/regex.txt", "=")

//    val finisher = new Finisher()
//      .setInputCols("regex")
//      .setIncludeMetadata(true)

    val regexPipeline = new Pipeline()
      .setStages(
        Array(documentAssembler, regexTokenizer)
      )

    val docsWithRegexMatchDf =
      regexPipeline
        .fit(dataframe)
        .transform(dataframe)
//        .withColumn("result", explode(arrays_zip($"regex")))
//        .select( "result")

//    docsWithRegexMatchDf.show(false)

      docsWithRegexMatchDf.select("regex")
      .as[Array[Element]]
      .foreach(elements => {
        val ab = ArrayBuffer[Project]()
        elements.foreach(element => {
          ab += Project(element.result, element.metaData("identifier"))
        })
        print(ab)
      })
  }
}
