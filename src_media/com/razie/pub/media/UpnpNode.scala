package com.razie.pub.media

/** this builds a upnp node in the upnp tree */
trait UpnpNode {
   def container () : JUpnpContainer
}