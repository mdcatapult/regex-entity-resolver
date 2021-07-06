import sbtrelease.ReleaseStateTransformations._
import Release._

val meta = """META.INF/(blueprint|cxf).*""".r

lazy val root = (project in file("."))
  .settings(
    Defaults.itSettings,
    name := "mdc-project-resolver",
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
      "MDC Nexus Releases" at "https://nexus.wopr.inf.mdc/repository/maven-releases/",
      "MDC Nexus Snapshots" at "https://nexus.wopr.inf.mdc/repository/maven-snapshots/"),
    credentials += {
      sys.env.get("NEXUS_PASSWORD") match {
        case Some(p) =>
          Credentials("Sonatype Nexus Repository Manager", "nexus.wopr.inf.mdc", "gitlab", p)
        case None =>
          Credentials(Path.userHome / ".sbt" / ".credentials")
      }
    },
    libraryDependencies ++= {
      val configVersion = "1.4.0"
      val akkaVersion = "2.6.4"
      val akkaHttpVersion = "10.2.0"

      Seq(
        "com.github.pathikrit" %% "better-files" % "3.9.1",
        "org.scalactic" %% "scalactic" % "3.2.0",
        "org.scalatest" %% "scalatest" % "3.1.1" % "test",
        "org.apache.poi" % "poi" % "5.0.0",
        "org.apache.poi" % "poi-ooxml" % "5.0.0",
        "com.typesafe.akka" %% "akka-slf4j" % akkaVersion,
        "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
        "com.typesafe.akka" %% "akka-actor-typed" % akkaVersion,
        "com.typesafe.akka" %% "akka-actor-testkit-typed" % akkaVersion % Test,
        "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpVersion,
        "com.typesafe.scala-logging" %% "scala-logging" % "3.9.2",
        "io.spray" %% "spray-json" % "1.3.5",
        "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpVersion,
        "com.typesafe" % "config" % configVersion,
        "io.mdcatapult.klein" % "util_2.13" % "1.2.0",
        "com.typesafe.akka" %% "akka-stream-testkit" % akkaVersion,
        "com.typesafe.akka" %% "akka-http-testkit" % akkaHttpVersion,
        "com.github.pureconfig" %% "pureconfig" % "0.14.0"
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
