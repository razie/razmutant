/**  ____    __    ____  ____  ____,,___     ____  __  __  ____
 *  (  _ \  /__\  (_   )(_  _)( ___)/ __)   (  _ \(  )(  )(  _ \           Read
 *   )   / /(__)\  / /_  _)(_  )__) \__ \    )___/ )(__)(  ) _ <     README.txt
 *  (_)\_)(__)(__)(____)(____)(____)(___/   (__)  (______)(____/    LICENSE.txt
 */
package com.razie.valueadd.security

import com.razie.pub.agent._
import com.razie.pub.comms._
import com.razie.pub.util._
import java.security._
import com.razie.pub.base._
import com.razie.pub.base.log._


/** you can only reset the authentication - usually set the first time the thing runs
 * 
 * @param store the directory of the store
 * @param prefix "mutant"
 * @param pwd password of keystore, if known
 * @param testing if set to true, only hard authentication is used - local clients are excluded 
 */
class ResetOnlyLightAuth (val store:String, prefix:String, pwd:String, testing:Boolean=false) extends LightAuthBase (prefix) {

	 override  def resetSecurity (password:String) = {
		  require (password != null && password.length > 0)
	      val ks = KS.create(store, password)
	      val t = KS.genKeys()
	      ks.store (password, Agents.me.name, t._1, t._2) 
	      if (LightAuthBase.instance.isInstanceOf[SecuLightAuth])
	    	  LightAuthBase.instance.asInstanceOf[SecuLightAuth].reload(store, password)
	    	  else {
	      val secu = new SecuLightAuth(store, prefix, password)
	      secu.testing=testing
	      LightAuthBase.init(secu)
	      LightAuthBase.lock
	    	  }
	      "Ok - keys regenerated..."
	   }

}
