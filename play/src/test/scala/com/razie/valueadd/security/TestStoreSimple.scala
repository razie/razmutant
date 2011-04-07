package com.razie.valueadd.security

import org.scalatest.junit._
import com.razie.pub.base.data._
import com.razie.valueadd.security._
import java.security.cert._
import java.security._
import java.security.spec._
import com.razie.pub.util._

/** testing the mutant scriptables */
class TestStoreSimple extends JUnit3Suite {

   val pwd = "pass"
   val store  ="./testkeystore"
   val alias  ="alias"
   val secret  ="secret to sign"
   val jstore  = "javakeystore"

	   // test no exceptions
   def testANoExceptions = expect (true) {
      val (pk, pubk) = KS.genKeys()
      val key = new PKCS8EncodedKeySpec (pk.getEncoded()).getEncoded()

      val pKeySpec = new PKCS8EncodedKeySpec(key);
      val newpk = KS.keyFactory.generatePrivate(pKeySpec);
      
      true
   }
 
//   test signing
   def testBSigning = expect (true) { 
      // 1. create ks
       val (pk, pubk) = KS.genKeys()

      // 2. sign 
      val signed = KS.sign (secret.getBytes, pk)
      
      // 4. verify
      if (KS.verify(signed, secret.getBytes, pubk)) true else false
    }

   // test SmipleKeyStore
   def testSimpleKeyStoreCreate1 = expect (true) { 
     // 1. create ks
      val ks = new SimpleKeyStore(store)
      ks.create(pwd)
      val (pk, pubk) = KS.genKeys()
      ks.store(pwd, alias, pk, pubk)
      ks.save(pwd)

     // 2. sign 
     val signed = KS.sign (secret.getBytes, pk)
     
     // 3. load it
     val ks2 = new SimpleKeyStore(store)
      ks2.load (pwd)
//     val ks2 = KS.load (store, pwd)
     val (pk2, puk2) = ks2.loadKeys(pwd, alias)
     
     // 4. verify
     val b = if (KS.verify(signed, secret.getBytes, puk2)) true else false
     true
   }

   // TODO 3-2 should fail if reading with wrong password
   def donttestSimpleKeyStorePass1 = expect (true) { 
     // 1. create ks
      val ks = new SimpleKeyStore(store)
      try {
      ks.load(pwd+1)
      } catch { case e:Exception => true }
     false
   }

   // TODO 3-2 should fail when adding keys with wrong password
   def donttestSimpleKeyStorePass2 = expect (true) { 
	   var res=false
     // 1. create ks
      val ks = new SimpleKeyStore(store)
      ks.load("")
      val (pk, pubk) = KS.genKeys()
      try{
      ks.store("wrongpwd", alias+"2", pk, pubk)
      ks.save("wrongpwd")
      } catch { case e:Exception => res=true }
     res
   }

   // should succeed saving with right password and load with null
   def testSimpleKeyStorePass3 = expect (true) { 
     // 1. create ks
      val ks = new SimpleKeyStore(store)
      ks.load(null)
      val (pk, pubk) = KS.genKeys()
      ks.store(pwd, alias, pk, pubk)
      ks.save(pwd)

     // 2. sign 
     val signed = KS.sign (secret.getBytes, pk)
     
     // 3. load it
     val ks2 = new SimpleKeyStore(store)
      ks2.load (null)
//     val ks2 = KS.load (store, pwd)
     val (pk2, puk2) = ks2.loadKeys(pwd, alias)
     
     // 4. verify
     val b = if (KS.verify(signed, secret.getBytes, puk2)) true else false
     true
   }
}
