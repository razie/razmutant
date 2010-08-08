package com.razie.playground

import com.razie.pub.comms._
import com.razie.valueadd.security._

/** initialize the playground...and/or valueadd services */
class Init1 {
   // TODO SECURITY use password to validate the store on reading as well...
   if (new java.io.File (Agents.me().localdir + "/keys"+"/jks").exists()) {
      LightAuthBase.init(new SecuLightAuth(Agents.me().localdir + "/keys", "mutant", null))
      LightAuth.lock
	} else
      LightAuthBase.init(new ResetOnlyLightAuth(Agents.me().localdir + "/keys", "mutant", null))
}
