import sbt._

class Project(info: ProjectInfo) extends ParentProject(info) {

  override def managedStyle = ManagedStyle.Maven
  val publishTo = "Scala Tools Nexus" at "http://nexus.scala-tools.org/content/repositories/snapshots/"
  //val publishTo = "Scala Tools Nexus" at "http://nexus.scala-tools.org/content/repositories/releases/"
  Credentials(Path.userHome / ".ivy2.credentials", log)
    
  // need to use defs for dependencies - thejy're used in sub-projects
  def scalatest = "org.scalatest" % "scalatest" % "1.2"
  def junit = "junit" % "junit" % "4.5"
  def json = "org.json" % "json" % "20090211"

  def razpub = "com.razie" %% "razpub" % "0.1-SNAPSHOT"
  val scrip   = "com.razie" %% "scripster" % "0.6-SNAPSHOT"

  lazy val upnp  = project("upnp", "upnp", new UpnpProject(_))
  lazy val agent = project("agent", "agent", new AgentProject(_), upnp)
  lazy val media = project("media", "media", new MediaProject(_), upnp, agent)
  lazy val mutant = project("mutant", "mutant", new MutantProject(_), upnp, agent, media)

  class UpnpProject(info: ProjectInfo) extends DefaultProject(info) {
    override def libraryDependencies = Set(scalatest, junit, json, razpub)
    override def unmanagedClasspath =
      super.unmanagedClasspath +++
        (Path.fromFile("lib_upnp") / "clink170.jar")
  }

  class AgentProject(info: ProjectInfo) extends DefaultProject(info) {
    override def libraryDependencies = Set(scalatest, junit, json, razpub, scrip)
    override def unmanagedClasspath =
      super.unmanagedClasspath +++
        (Path.fromFile("lib_upnp") / "clink170.jar")
  }

  class MutantProject(info: ProjectInfo) extends DefaultProject(info) {
    override def libraryDependencies = Set(scalatest, junit, json, razpub, scrip)
  }

  class MediaProject(info: ProjectInfo) extends DefaultProject(info) {
    override def libraryDependencies = Set(scalatest, junit, json, razpub, scrip)
  }

  //class SwingProject(info: ProjectInfo) extends DefaultProject(info) {
    //def scalaSwing = "org.scala-lang" % "scala-swing" % "2.8.1"
    //override def libraryDependencies = Set(scalatest, junit, scalaSwing)
  //}

  //class WebProject(info: ProjectInfo) extends DefaultProject(info) {
    //override def libraryDependencies = Set(scalatest, junit)

    //override def unmanagedClasspath =
      //super.unmanagedClasspath +++
        //(Path.fromFile("lib") / "mime-util.jar")
  //}

  def mkdirs (s:Seq[String]) = {}//s foreach _.mkdir 

  def dist = {
    val m = Path.fromFile ("mdist")

    this mkdirs (("lib plugins cfg user" split " ") map (m / _))
    (m / "upgrade").mkdir
    this mkdirs ("lib plugins cfg" split " ") foreach (m / "upgrade" / _).mkdir

  }
}

