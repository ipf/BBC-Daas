import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {

	val appName = "BBC-DaaS"
	val appVersion = "1.0-SNAPSHOT"

	val appDependencies = Seq(
		"se.radley" %% "play-plugins-salat" % "1.2",
		"com.clever-age" % "play2-elasticsearch" % "0.5.3",
		"securesocial" %% "securesocial" % "master-SNAPSHOT",
		"com.github.tototoshi" %% "scala-csv" % "0.7.0",
		"com.google.guava" % "guava" % "14.0"
	)

	val main = play.Project(appName, appVersion, appDependencies).settings(
		routesImport += "se.radley.plugin.salat.Binders._",
		templatesImport += "org.bson.types.ObjectId",
		resolvers += "OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/",
		resolvers += Resolver.url("play-plugin-releases", new URL("http://repo.scala-sbt.org/scalasbt/sbt-plugin-releases/"))(Resolver.ivyStylePatterns),
		resolvers += Resolver.url("play-plugin-snapshots", new URL("http://repo.scala-sbt.org/scalasbt/sbt-plugin-snapshots/"))(Resolver.ivyStylePatterns)
	)
}