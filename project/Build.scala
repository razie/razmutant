import sbt._
import Keys._

object V {
  val version      = "0.3-SNAPSHOT"
  val scalaVersion = "2.9.1"
  val organization = "com.razie"

  def snap = (if (V.version endsWith "-SNAPSHOT") "-SNAPSHOT" else "")

  def SCALAVER   = scalaVersion

  def RAZBASEVER  = "0.6" + snap
  def SNAKKVER    = "0.4" + snap
  def LIGHTSOAVER = "0.6" + snap
}

object MyBuild extends Build {

  def scalatest = "org.scalatest"  % "scalatest_2.9.1" % "1.6.1"
  def junit     = "junit"          % "junit"           % "4.5"      % "test->default"
  def json      = "org.json"       % "json"            % "20090211"
  
  def scalaSwing = "org.scala-lang" % "scala-swing"    % V.SCALAVER
  def scalaComp  = "org.scala-lang" % "scala-compiler" % V.SCALAVER 
  def scalaLib   = "org.scala-lang" % "scala-library"  % V.SCALAVER 

  def scalazCore = "org.scalaz" % "scalaz-core_2.9.1"  % "6.0.3"

  val snakk    = "com.razie" %% "snakk-core"      % V.SNAKKVER
  def razBase  = "com.razie" %% "razbase"         % V.RAZBASEVER
  def swing20  = "com.razie" %% "20swing"         % V.RAZBASEVER
  def lightsoa = "com.razie" %% "lightsoa-core"   % V.LIGHTSOAVER

  lazy val root = Project(id="razmutant",    base=file("."),
                          settings = defaultSettings ++ Seq()
                  ) aggregate (upnp, agent, media, mutant) dependsOn (mutant)

  lazy val upnp = Project(id="upnp", base=file("upnp"),
                          settings = defaultSettings ++ 
                          Seq(libraryDependencies ++= Seq(scalatest, junit, json))
                  )

  lazy val agent  = Project(id="agent", base=file("agent"),
                          settings = defaultSettings ++ 
                          Seq(libraryDependencies ++= Seq(scalatest, junit))
                  ) dependsOn (upnp)

  lazy val media = Project(id="media", base=file("media"),
                          settings = defaultSettings ++ 
                          Seq(libraryDependencies ++= Seq(scalatest, junit, scalaSwing))
                  ) dependsOn (upnp, agent)

  lazy val mutant = Project(id="mutant", base=file("mutant"),
                          settings = defaultSettings ++ 
                          Seq( libraryDependencies ++= Seq(scalatest, junit))
                  ) dependsOn (upnp, agent, media)


  def defaultSettings = Defaults.defaultSettings ++ Seq (
    scalaVersion         := V.scalaVersion,
    version              := V.version,

    organization         := V.organization,
    organizationName     := "Razie's Pub",
    organizationHomepage := Some(url("http://www.razie.com")),

    commands += distCommand,

    publishTo <<= version { (v: String) =>
      if(v endsWith "-SNAPSHOT")
        Some ("Sonatype" at "https://oss.sonatype.org/content/repositories/snapshots/")
      else
        Some ("Sonatype" at "https://oss.sonatype.org/content/repositories/releases/")
    }  )


  // TODO when scalafs is final, make these imports automatic in the scalafs sbt.plugins file - read on wiki

  import razie.fs.proto2._
  import razie.fs.proto2.DefaultShell._

  def distCommand = Command.command ("dist") { state =>
    val p = FP apply Project.extract(state).get(baseDirectory) // get project's directory
    val d = p / "mdist"

    out << "Creating distribution in %s \n".format(d.path)

    val mr = p / "mutant/src/main/resources"
    val ar = p / "agent/src/main/resources"
    val er = p / "media/src/main/resources"

    "lib plugins cfg user" split " " map (d / _) foreach (_.mkdirs)
    "lib plugins cfg" split " " map (d / "upgrade" / _) foreach (_.mkdirs)

    // TODO tstamp

    d << ar / "cfg" / "agent.xml"
    d << ar / "cfg" / "assets.xml"
    d << ar / "cfg" / "template_agent.xml"
    d << er / "cfg" / "media.xml"
    d << mr / "cfg" / "user.xml"

    d << mr / "README.txt"

    state // how to indicate success/failure?
    }

}


