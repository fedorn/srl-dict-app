name := "evex.kb"

version := "1.0"

scalaVersion := "2.11.0"

resolvers += "Typesafe Repo" at "http://repo.typesafe.com/typesafe/releases/"

resolvers += "Local Maven Repository" at "file://"+Path.userHome.absolutePath+"/.m2/repository"

libraryDependencies += "com.typesafe.play" %% "play-json" % "2.3.0"

libraryDependencies += "net.databinder.dispatch" %% "dispatch-core" % "0.11.1"

libraryDependencies += "ru.ksu.niimm.cll.uima" % "uima-ext-dep-parser-mst" % "0.3-SNAPSHOT" classifier "tests" excludeAll(
  ExclusionRule(organization = "org.slf4j")
)

libraryDependencies += "ru.ksu.niimm.cll.uima" % "uima-ext-lemmatizer-opencorpora" % "0.3-SNAPSHOT" excludeAll(
  ExclusionRule(organization = "org.slf4j")
)

libraryDependencies += "ru.ksu.niimm.cll.uima" % "uima-ext-commons" % "0.3-SNAPSHOT" excludeAll(
  ExclusionRule(organization = "org.slf4j")
)

libraryDependencies += "ru.ksu.niimm.cll.uima" % "uima-ext-brat-integration" % "0.3-SNAPSHOT" excludeAll(
  ExclusionRule(organization = "org.slf4j")
)

libraryDependencies ++= {
  val akkaV = "2.3.3"
  val sprayV = "1.3.1"
  Seq(
    "io.spray"            %%   "spray-can"     % sprayV,
    "io.spray"            %%   "spray-routing" % sprayV,
    "io.spray"            %%   "spray-testkit" % sprayV  % "test",
    "com.typesafe.akka"   %%  "akka-actor"    % akkaV,
    "com.typesafe.akka"   %%  "akka-testkit"  % akkaV   % "test",
    "org.specs2"          %%  "specs2-core"   % "2.3.12" % "test"
  )
}

Revolver.settings

net.virtualvoid.sbt.graph.Plugin.graphSettings
