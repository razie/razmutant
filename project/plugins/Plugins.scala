import sbt._

class Plugins(info: ProjectInfo) extends PluginDefinition (info) {
  val scalafs   = "com.razie" %% "scalafs" % "0.1-SNAPSHOT"
}

