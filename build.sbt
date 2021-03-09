name := "MDC-project-resolver"
version := "0.1"
scalaVersion := "2.11.12"

libraryDependencies ++= {
  val sparkVer = "2.4.7"
  val sparkNLP = "2.7.1"
  Seq(
    "org.apache.spark" %% "spark-core" % sparkVer,
    "org.apache.spark" %% "spark-mllib" % sparkVer,
    "com.johnsnowlabs.nlp" %% "spark-nlp" % sparkNLP,
    "org.apache.poi" % "poi" % "5.0.0",
    "org.apache.poi" % "poi-ooxml" % "5.0.0",
    "io.spray" %% "spray-json" % "1.3.5",
  )
}
