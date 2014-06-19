resolvers += "Typesafe Repo" at "http://repo.typesafe.com/typesafe/releases/"

resolvers += "Local Maven Repository" at "file://"+Path.userHome.absolutePath+"/.m2/repository"

libraryDependencies += "com.typesafe.play" %% "play-json" % "2.3.0"

libraryDependencies += "net.databinder.dispatch" %% "dispatch-core" % "0.11.1"

libraryDependencies += "org.clapper" %% "grizzled-slf4j" % "1.0.2"

libraryDependencies += "ru.ksu.niimm.cll.uima" % "uima-tokenizer" % "0.3-SNAPSHOT"

libraryDependencies += "org.slf4j" % "slf4j-simple" % "1.7.7"
