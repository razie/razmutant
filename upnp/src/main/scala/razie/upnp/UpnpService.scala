package razie.upnp

import org.cybergarage.upnp.Argument
import org.cybergarage.upnp.Action
import com.razie.pub.upnp._
import com.razie.pub.lightsoa._
import com.razie.pub.base.log._
import com.razie.pub.base._
import com.razie.pub.base.data._
//import razie._
import razie.base._
import scala.collection.JavaConversions._

/** just a static factory */
object UpnpServiceFactory {
  /** create a bridge upnp service instance from an annotated SoaService class. NOTE that after this, you must fill in the upnp service attributes. The alternative is to derive from the default service and override attributes */
  def fromAnnotation(o: AnyRef, attrs: UpnpServiceAttrs): UpnpService = {
    // TODO implement
    val binding = new UpnpSoaBinding(o, "bibiku")
    new DefaultService(binding, attrs)
  }
}

object UpnpService {
  def fromCyber(d: org.cybergarage.upnp.Service) = {
    new UpnpService() {
      icyberService = d
      override val binding: UpnpSoaBinding = null

      override def xmlns: String = ""
      override def serviceType: String = d.getServiceType
      override def serviceId: String = d.getServiceID
      override def SCPDURL: String = d.getSCPDURL
      override def controlURL: String = d.getControlURL
      override def eventSubURL: String = d.getEventSubURL

    }
  }
}

/** bag of upnp service attributes. Base class of the real thing since you can add them separately as well.
 * 
 * TODO add link with description of the attributes
 * 
 * */
trait UpnpServiceAttrs {
  def xmlns: String
  def serviceType: String
  def serviceId: String
  def SCPDURL: String
  def controlURL: String
  def eventSubURL: String
}

/** stands in for a upnp service. For services, you don't have to derive your class, just use the SoaService and SoaMethod annotations 
 * 
 * */
trait UpnpService extends UpnpServiceAttrs {
  val binding: UpnpSoaBinding

  def actions: List[UpnpAction] = { cyberService.getActionList.toList.asInstanceOf[List[Action]].map (new UpnpAction(_)) }

  //   def stateVars : List[AnyRef]

  /** to be included in the device's descriptor */
  def toBriefUpnpXml() = {
    <service>
      <serviceType>{ serviceType }</serviceType>
      <serviceId>{ serviceId }</serviceId>
      <SCPDURL>{ SCPDURL }</SCPDURL>
      <controlURL>{ controlURL }</controlURL>
      <eventSubURL>{ eventSubURL }</eventSubURL>
    </service>
  }

  /** full service descriptor */
  def toFullUpnpXml = {
    //	   <?xml version="1.0"?>
    <scpd xmlns={ xmlns }>
      <specVersion>
        <major>1</major>
        <minor>0</minor>
      </specVersion>
      {
        if (binding != null && binding.methods.size > 0) {
          <actionList>
            {
              for (a <- razie.M(binding.methods.values))
                yield actionXml(a)
            }
          </actionList>
        }
      }
      <serviceStateTable>
      </serviceStateTable>
    </scpd>
  }

  def actionXml(m: java.lang.reflect.Method) = {
    if (m.getAnnotation(classOf[SoaMethod]) != null) {
      <action>
        <name>{ m.getName() }</name>
        <argumentList>
          {
            for (a: String <- m.getAnnotation(classOf[SoaMethod]).args()) {
              <argument>
                <name>{ a }</name>
                //<relatedStateVariable>Time</relatedStateVariable>
                <direction>in</direction>
              </argument>
            }
          }
          <argument>
            <name>Result</name>
            <relatedStateVariable>Result</relatedStateVariable>
            <direction>out</direction>
          </argument>
        </argumentList>
      </action>
    }
  }

  def varXml = {
    <stateVariable sendEvents="yes">
      <name>Time</name>
      <dataType>string</dataType>
    </stateVariable>
  }

  /** generic sink for actions */
  def actionInvoked(action: String, args: AnyRef) = {
    Log.alarmThis ("NOT IMPLEMENTED!!!")
  }

  /** generic sink for state vars */
  def stateVar(varName: String): AnyRef = {
    Log.alarmThis ("NOT IMPLEMENTED!!!")
    null
  }

  protected var icyberService: org.cybergarage.upnp.Service = null // TODO
  def cyberService: org.cybergarage.upnp.Service = icyberService
}

/** sample default service - you should reset the upnp attributes */
class DefaultService(val binding: UpnpSoaBinding, src: UpnpServiceAttrs) extends UpnpService {
  val xmlns = Option (src.xmlns) getOrElse "urn:schemas-upnp-org:service-1-0"
  val serviceType = Option (src.serviceType) getOrElse "urn:schemas-upnp-org:service:mutant:1"
  val serviceId = Option (src.serviceId) getOrElse "urn:schemas-upnp-org:serviceId:mutant:1"
  val SCPDURL = Option (src.SCPDURL) getOrElse "/service/agent-description.xml"
  val controlURL = Option (src.controlURL) getOrElse "/service/timer/control"
  val eventSubURL = Option (src.eventSubURL) getOrElse "/service/timer/eventSub"

  override val actions: List[UpnpAction] = List()
  val stateVars: List[AnyRef] = List()
}

class UpnpAction(val cyber: Action) {
  def name = cyber.getName

  lazy val inArgs: razie.AA = {
    val aa = razie.AA()
    cyber.getInputArgumentList.toList.asInstanceOf[List[Argument]].foreach (
      x => aa.set(x.getName, x.getValue, AttrType.STRING))
    aa
  }

  def execute = {
    inArgs.foreach ((x, y) => cyber.setArgumentValue(x, y.toString))
    cyber.postControlAction
  }

  def controlResponse = cyber.getActionData.getControlResponse

  lazy val outArgs: razie.AA = {
    val aa = razie.AA()
    cyber.getOutputArgumentList.toList.asInstanceOf[List[Argument]].foreach (
      x => aa.set(x.getName, x.getValue, AttrType.STRING))
    aa
  }

  override def toString = cyber.getActionNode.toString
}

