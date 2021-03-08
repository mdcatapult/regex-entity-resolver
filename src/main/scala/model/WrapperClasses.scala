package model

// example utility case classes for use when converting dataframes to datasets
// these must be outside the scope of the test to be used, as encoders must be generated for them

case class Element(annotatorType: String,
                   begin: Int,
                   end: Int,
                   result: String,
                   metaData: Map[String, String],
                   embeddings: Array[Float])

case class Project(code: String, name: String)
