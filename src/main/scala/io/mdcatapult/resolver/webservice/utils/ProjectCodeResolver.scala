package io.mdcatapult.resolver.webservice.utils

import com.johnsnowlabs.nlp.annotators.RegexMatcher
import com.johnsnowlabs.nlp.{DocumentAssembler, SparkNLP}
import io.mdcatapult.resolver.webservice.model.{Element, Project}
import org.apache.spark.ml.Pipeline
import spray.json.enrichAny

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
      .setRules("src/main/resources/regex.txt", "=")

    val regexPipeline = new Pipeline()
      .setStages(
        Array(documentAssembler, regexTokenizer)
      )


    regexPipeline
      .fit(dataframe)
      .transform(dataframe)
      .select("regex")
      .as[Array[Element]]
      .foreach(elementArray => {
        val projects = ArrayBuffer[Project]()
        elementArray.foreach(element => {
          projects += Project(element.result, element.metaData("identifier"))
        })
        print(projects.toArray.toJson)
        projects.toArray.toJson
      })
  }
}
