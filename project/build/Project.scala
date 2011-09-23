import sbt._

class Project(info: ProjectInfo) extends ParentProject(info) {

  override def managedStyle = ManagedStyle.Maven
  val publishTo =
    if (version.toString endsWith "-SNAPSHOT")
      "Scala Tools Nexus" at "http://nexus.scala-tools.org/content/repositories/snapshots/"
    else
      "Scala Tools Nexus" at "http://nexus.scala-tools.org/content/repositories/releases/"
  Credentials(Path.userHome / ".ivy2.credentials", log)

  val snap = (if (version.toString endsWith "-SNAPSHOT") "-SNAPSHOT" else "")

  def scalatest = "org.scalatest" % "scalatest_2.9.1" % "1.6.1"
  def junit     = "junit"         % "junit"           % "4.5" % "test->default"
  def json      = "org.json"      % "json"            % "20090211"
  def slf4jApi  = "org.slf4j"     % "slf4j-api"       % "1.6.1"

  def razpub    = "com.razie"    %% "razpub"          % ("0.2" + snap)

  lazy val upnp   = project ("upnp",   "upnp",   new UpnpProject(_))
  lazy val agent  = project ("agent",  "agent",  new AgentProject(_),  upnp)
  lazy val media  = project ("media",  "media",  new MediaProject(_),  upnp, agent)
  lazy val mutant = project ("mutant", "mutant", new MutantProject(_), upnp, agent, media)

  class UpnpProject(info: ProjectInfo) extends DefaultProject(info) {
    override def libraryDependencies = Set(scalatest, junit, json, razpub)
    override def unmanagedClasspath =
      super.unmanagedClasspath +++
        (Path.fromFile("lib_upnp") / "clink170.jar")
  }

  class AgentProject(info: ProjectInfo) extends DefaultProject(info) {
    override def libraryDependencies = Set(scalatest, junit, json, razpub)
    override def unmanagedClasspath =
      super.unmanagedClasspath +++
        (Path.fromFile("lib_upnp") / "clink170.jar")
  }

  class MutantProject(info: ProjectInfo) extends DefaultProject(info) {
    override def libraryDependencies = Set(scalatest, junit, json, razpub)
  }

  class MediaProject(info: ProjectInfo) extends DefaultProject(info) {
    override def libraryDependencies = Set(scalatest, junit, json, razpub)
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

  import razie.fs.proto2._
  import razie.fs.proto2.DefaultShell._

  implicit def toSFS (p:sbt.Path) : FP = { println (p); FP (p.toString()) }

  lazy val dist = task { distAction } //dependsOn (compile) //describedAs("Create a distribution")

  def distAction = {
    "mdist".mkdir
    val m  = Path.fromFile ("mdist")
    val ms = Path.fromFile ("mutant/src/main/resources")
    val as = Path.fromFile ("agent/src/main/resources")
    val es = Path.fromFile ("media/src/main/resources")

    "lib plugins cfg user" split " " map (m / _) foreach (_.mkdir)
    "lib plugins cfg" split " " map (m / "upgrade" / _) foreach (_.mkdir)

    // TODO tstamp 

    as / "cfg" / "agent.xml"          -> m
    as / "cfg" / "assets.xml"         -> m
    as / "cfg" / "template_agent.xml" -> m
    es / "cfg" / "media.xml"          -> m
    ms / "cfg" / "user.xml"           -> m

    None // indicates success to SBT
  }
}

