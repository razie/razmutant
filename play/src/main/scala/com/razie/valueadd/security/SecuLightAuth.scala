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
import razie.base._

/** 
 * PK-based authentication of the agent nation
 * 
 * Keys are generated locally and all public keys shared among the agents. Each agent requesting something from another, will authenticate its request with its private key. The target will verify that indeed, the remote is who he/she claims.
 * 
 * Read more on security at http://wiki.homecloud.ca/security
 * 
 * TODO 1 SECU - someone could copy the store: use hardware id to secure it
 * 
 * @param store the directory of the store
 * @param prefix "mutant"
 * @param pwd password of keystore, if known
 */
@NoStaticSafe
class SecuLightAuth (val store:String, prefix:String, pwd:String) extends LightAuthBase (prefix) {
   /** if set to true, only hard authentication is used - local clients are excluded */
   var testing = false
   var ks = KS.load (store, pwd)
  
   // TODO 3 PERF implement this cache so we don't load every time
   val keys = new scala.collection.mutable.HashMap[String, (PrivateKey, PublicKey)]()
   
   private[this] var (mypk, mypubk) = ks.loadKeys(pwd, Agents.me.name)
   val ARG2 = "mutant.secret"
   val ARG1 = "mutant.name"

   def reload (st:String, pwd:String) = {
      ks = KS.load (st, pwd)
    val (mypk1, mypubk1) = ks.loadKeys(pwd, Agents.me.name)
    mypk=mypk1
    mypubk=mypubk1
   }
   
   override def httpSecParms (url: java.net.URL):AttrAccess = {
      if (mypk != null)
         razie.AA(ARG1, Agents.me.name, ARG2, sign(url.getPath()))
      else 
         null
   }

   /** figure out authorization credentials in one request. 
    * 
    * NOTE this valueadd option knows SHARED SECRET
    * 
    * @see {LightAuth.iauthorize} 
    * 
    * @param socket - the socket involved in the request
    * @param url - the url of the request
    * @param httpArgs - args of the http request
    * @return the auth level of the other end
    */
   override def iauthorize(socket:java.net.Socket, url:String, httpArgs:AttrAccess):LightAuthType = {
      try {
      val clientip = socket.getInetAddress().getHostAddress();

      val newurl = if (url != null) url.replaceAll (" HTTP/.*", "") else null
       
      // if Agents doesn't know myself, this should succeed, it's not a proper server but maybe
      // some sort of a test???

      // TODO this auth is really weak anyways...
      val debug1 = Agents.getMyHostName()

      var res = LightAuthType.ANYBODY
     
      Log.traceThis ("AUTH_RECON: "+clientip + " / me="+Agents.me + " / "+debug1)
      
      // TODO is this correct in linux?
      if (!testing && Comms.isLocalhost(clientip)) {
         res = LightAuthType.INHOUSE;
      } else if (!testing && (clientip.startsWith(Agents.getHomeNetPrefix())
          || Agents.agent(Agents.me.hostname) == null /*TODO what is this condition? */
          || clientip.equals(Agents.me.ip))) {
         res = LightAuthType.INHOUSE;
      } else {
         if (httpArgs != null && httpArgs.isPopulated(ARG1) && httpArgs.isPopulated(ARG2)) {
            try {
            if (verify (httpArgs.sa(ARG2), newurl, httpArgs.sa(ARG1))) {
               Log.traceThis ("AUTH_RECON: accepted remote agent: "+httpArgs.sa(ARG1) )
               res = LightAuthType.SHAREDSECRET;
            } } catch {
         case e:Throwable => {
            razie.Log.alarmThis ("can't authorize as SHAREDSECRET, ALTHOUGH SECRET PRESENT: ", e)
         } } }
            if (res == LightAuthType.SHAREDSECRET) { //done
            } else if (Agents.agentByIp(clientip) != null && Agents.agentByIp(clientip).isUp()) {
               res = LightAuthType.INCLOUD;
            } else if (false) {
               // TODO SECU identify friends
               res = LightAuthType.FRIEND;
            }
         }
      
      res
      } catch {
         case e:Throwable => {
            razie.Log.alarmThis ("can't authorize, will treat as anybody: ", e)
            LightAuthType.ANYBODY
         }
      }
   }  
  
   def sign (secret:String) = {
      var s: String=null
      try {
         s = Base64.encodeBytes(KS.sign(secret.getBytes, mypk))
      } catch {
         case e:Exception => Log.logThis ("ERR_AUTH: cant sign secret", e)
      }
      s
   }

   private[this] def verify (signed:String, original:String, agent:String) = {
      val a = Agents.agent(agent)
      var verified=false
           
      if (a == null) {
         Log.logThis ("ERR_AUTH SECURITY_ISSUE: don't know remote agent: "+agent )
      } else {
         // TODO SECU use password
         val (pk, puk) = KS.load(store, null).loadKeys (null, agent)
         verified = if (KS.verify(Base64.decode (signed), original.getBytes, puk)) true else false
      }
      verified
   }
   
   override def resetSecurity (password:String) = {
      require (password != null && password.length > 0)
      val ks = KS.create(store, password)
      val t = KS.genKeys()
      ks.store (password, Agents.me.name, t._1, t._2)
      // TODO unlock lightauth before init again
      LightAuthBase.init(new SecuLightAuth(store, prefix, password))
      "Ok - keys regenerated..."
   }

   override	def accept (password:String, agent:AgentHandle, pubk:String) = {
      require (password != null && password.length > 0)
      // copy remote pub key
      val bytes = Base64.decode (pubk)
      val ks = KS.load(store, password)
      ks.store (password,agent.name, null, KS.pubKeyFromBytes(bytes))
      "Ok - stored public key for " + agent.name + " at " + agent.url + " ..."
   }

   override	def pubkey (agent:AgentHandle)=
      Base64.encodeBytes(KS.load(store, null).loadKeys (null, agent.name)._2.getEncoded())

   override def toString = "SecuLightAuth - PK acceptance based cloud trust"
}
