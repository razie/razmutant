import sbt._

class Plugins(info: ProjectInfo) extends PluginDefinition (info) {
  val scalafs   = "com.razie" % "scalafs_2.7.7" % "0.1-SNAPSHOT"
}

