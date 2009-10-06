package com.razie.secu.test

import org.scalatest.junit._
import com.razie.pub.base.data._
import com.razie.secu._
import java.security.cert._
import java.security._
import java.security.spec._

/** testing the mutant scriptables */
class TestSecu extends JUnit3Suite {

   val pwd = "pass"
   val store  ="./testkeystore"
   val alias  ="alias"
   val secret  ="secret to sign"
     
   def testA = expect (true) {
      val (pk, pubk) = KS.genKeys()
      val key = new PKCS8EncodedKeySpec (pk.getEncoded()).getEncoded()

      val keyFactory = KeyFactory.getInstance("DSA", "SUN");
      val pKeySpec = new PKCS8EncodedKeySpec(key);
      val newpk = keyFactory.generatePrivate(pKeySpec);
      
      true
   }
  
   def testB = expect (true) { 
      // 1. create ks
       val (pk, pubk) = KS.genKeys()

      // 2. sign 
      val signed = KS.sign (secret.getBytes, pk)
      
      // 4. verify
      if (KS.verify(signed, secret.getBytes, pubk)) true else false
    }

   def testC = expect (true) { 
     // 1. create ks
      val ks = KS.create (store, pwd)
      val (pk, pubk) = KS.genKeys()
      ks.store(pwd, alias, pk, pubk)
      ks.save(pwd)

     // 2. sign 
     val signed = KS.sign (secret.getBytes, pk)
     
     // 3. load it
     val ks2 = KS.load (store, pwd)
     val (pk2, puk2) = ks2.loadKeys(pwd, alias)
     
     // 4. verify
     val b = if (KS.verify(signed, secret.getBytes, puk2)) true else false
     true
   }
   
}
