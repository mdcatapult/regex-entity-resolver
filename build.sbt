import sbtrelease.ReleaseStateTransformations._
import Release._

val meta = """META.INF/(blueprint|cxf).*""".r

lazy val root = (project in file("."))
  .settings(
    Defaults.itSettings,
    name := "regex-entity-resolver",
    scalaVersion := "2.13.3",
    scalacOptions ++= Seq(
      "-encoding", "utf-8",
      "-unchecked",
      "-deprecation",
      "-feature",
      "-explaintypes",
      "-Ywarn-unused:_,-implicits",
      "-Ywarn-dead-code",
      "-Ywarn-numeric-widen"
    ),
    useCoursier := false,
    updateOptions := updateOptions.value.withLatestSnapshots(latestSnapshots = false),
    resolvers ++= Seq(
      "gitlab" at "https://gitlab.com/api/v4/projects/50550924/packages/maven",
      "Maven Public" at "https://repo1.maven.org/maven2"),
    credentials += {
      sys.env.get("CI_JOB_TOKEN") match {
        case Some(p) =>
          Credentials("GitLab Packages Registry", "gitlab.com", "gitlab-ci-token", p)
        case None =>
          Credentials(Path.userHome / ".sbt" / ".credentials")
      }
    },
    libraryDependencies ++= {
      val configVersion = "1.4.3"
      val pekkoVersion = "1.0.3"
      val pekkoHttpVersion = "1.0.1"

      Seq(
        "com.github.pathikrit" %% "better-files" % "3.9.2",
        "org.scalactic" %% "scalactic" % "3.2.19",
        "org.scalatest" %% "scalatest" % "3.2.19" % "test",
        "org.apache.poi" % "poi" % "5.2.5",
        "org.apache.poi" % "poi-ooxml" % "5.2.5",
        "org.apache.pekko" %% "pekko-slf4j" % pekkoVersion,
        "org.apache.pekko" %% "pekko-http" % pekkoHttpVersion,
        "org.apache.pekko" %% "pekko-actor-typed" % pekkoVersion,
        "org.apache.pekko" %% "pekko-actor-testkit-typed" % pekkoVersion % Test,
        "org.apache.pekko" %% "pekko-http-spray-json" % pekkoHttpVersion,
        "com.typesafe.scala-logging" %% "scala-logging" % "3.9.5",
        "io.spray" %% "spray-json" % "1.3.6",
        "com.typesafe" % "config" % configVersion,
        "io.mdcatapult.klein" %% "util" % "1.2.6",
        "org.apache.pekko" %% "pekko-stream-testkit" % pekkoVersion,
        "org.apache.pekko" %% "pekko-http-testkit" % pekkoHttpVersion,
        "com.github.pureconfig" %% "pureconfig" % "0.17.7"
      ).map(
        _.exclude(org = "javax.ws.rs", name = "javax.ws.rs-api")
          .exclude(org = "com.google.protobuf", name = "protobuf-java")
          .exclude(org = "com.typesafe.play", name = "shaded-asynchttpclient")
      )
    }
  )
  .settings(
    assemblyJarName := "webservice.jar",
    test in assembly := {},
    assemblyMergeStrategy in assembly := {
      case PathList("javax", "servlet", _*) => MergeStrategy.first
      case PathList("javax", "xml", "stream", _*) => MergeStrategy.first
      case PathList("javax", "activation", _*) => MergeStrategy.first
      case PathList("sun", "activation", "registries", _*) => MergeStrategy.first
      case PathList("sun", "activation", "viewers", _*) => MergeStrategy.first
      case PathList("com", "sun", "activation", "registries", _*) => MergeStrategy.first
      case PathList("com", "sun", "activation", "viewers", _*) => MergeStrategy.first
      case PathList(ps@_*) if ps.last endsWith ".html" => MergeStrategy.first
      case PathList(xs@_*) if xs.last == "module-info.class" => MergeStrategy.first
      case PathList("org", "apache", "commons", _*) => MergeStrategy.first
      case PathList("com", "ctc", "wstx", _*) => MergeStrategy.first
      case PathList(xs@_*) if xs.last == "public-suffix-list.txt" => MergeStrategy.first
      case PathList(xs@_*) if xs.last == ".gitkeep" => MergeStrategy.discard
      case PathList("org", "apache", "batik", _*) => MergeStrategy.first
      case "META-INF/jpms.args" => MergeStrategy.discard
      case n if n.startsWith("application.conf") => MergeStrategy.concat
      case n if n.endsWith(".conf") => MergeStrategy.concat
      case meta(_) => MergeStrategy.first
      case "log4j.properties" => MergeStrategy.last
      case n if n.startsWith("logback.xml") => MergeStrategy.first
      case x =>
        val oldStrategy = (assemblyMergeStrategy in assembly).value
        oldStrategy(x)
    }
  )
  .settings(
    releaseProcess := Seq[ReleaseStep](
      checkSnapshotDependencies,
      inquireVersions,
      runClean,
      runTest,
      setReleaseVersion,
      getShortSha,
      writeReleaseVersionFile,
      commitAllRelease,
      tagRelease,
      runAssembly,
      setNextVersion,
      writeNextVersionFile,
      commitAllNext,
      pushChanges
    )
  )
