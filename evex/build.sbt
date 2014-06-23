name := "evex.kb"

version := "1.0"

scalaVersion := "2.11.0"

resolvers += "Typesafe Repo" at "http://repo.typesafe.com/typesafe/releases/"

resolvers += "Local Maven Repository" at "file://"+Path.userHome.absolutePath+"/.m2/repository"

libraryDependencies += "com.typesafe.play" %% "play-json" % "2.3.0"

libraryDependencies += "net.databinder.dispatch" %% "dispatch-core" % "0.11.1"

libraryDependencies += "org.clapper" %% "grizzled-slf4j" % "1.0.2"

libraryDependencies += "ru.ksu.niimm.cll.uima" % "uima-ext-dep-parser-mst" % "0.3-SNAPSHOT"

libraryDependencies += "ru.ksu.niimm.cll.uima" % "uima-ext-dep-parser-mst" % "0.3-SNAPSHOT" classifier "tests"

libraryDependencies += "ru.ksu.niimm.cll.uima" % "uima-ext-lemmatizer-opencorpora" % "0.3-SNAPSHOT"

libraryDependencies += "ru.ksu.niimm.cll.uima" % "uima-ext-commons" % "0.3-SNAPSHOT"
