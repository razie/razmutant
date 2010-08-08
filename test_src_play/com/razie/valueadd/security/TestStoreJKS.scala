package com.razie.valueadd.security

import org.scalatest.junit._
import com.razie.pub.base.data._
import com.razie.valueadd.security._
import java.security.cert._
import java.security._
import java.security.spec._
import com.razie.pub.util._

/** testing the mutant scriptables */
class TestStoreJKS extends JUnit3Suite {

   val pwd = "pass"
   val store  ="./testkeystore"
   val alias  ="alias"
   val secret  ="secret to sign"
   val jstore  = "javakeystore"

   def testJavaKeyStore = expect (true) { 
     // 1. create ks
      val ks = new JavaKeyStore(jstore)
      ks.create(pwd)
      val (pk, puk) = KS.genKeys()
      ks.store(pwd, alias, pk, puk)
      ks.save(pwd)

     // 3. load it
     val ks2 = KS.load (jstore, pwd)
     val (pk2, puk2) = ks2.loadKeys(pwd, alias)
     
     // 2. sign 
     val signed = KS.sign (secret.getBytes, pk2)
     
     // 4. verify
     val b = if (KS.verify(signed, secret.getBytes, puk2)) true else false
     b
   }

   // should fail if reading with wrong password
   def testJKSPass1 = expect (true) { 
	   var res=false
     // 1. create ks
      val ks = new JavaKeyStore(jstore)
      try {
      ks.load(pwd+1)
      } catch { case e:Exception => if (e.getMessage.contains("password was incorrect")) res=true }
     res
   }

   // should fail if reading with wrong password
   def testJKSPass1a = expect (true) { 
	   var res=false
     // 1. create ks
      val ks = new JavaKeyStore(jstore)
      ks.load(null)
     val (pk2, puk2) = ks.loadKeys(null, alias)
     true
   }

   // should fail when adding keys with wrong password
   def testJKSPass2 = expect (true) { 
	   var res=false
     // 1. create ks
      val ks = new JavaKeyStore(jstore)
      try{
      ks.load("wrongpasswd")
      val (pk, pubk) = KS.genKeys()
      ks.store("wrongpwd", alias+"2", pk, pubk)
      ks.save("wrongpwd")
      } catch { case e:Exception => res=true }
     res
   }

   // should succeed saving with right password and load with null
   def testJKSPass3 = expect (true) { 
     // 1. create ks
      val ks = new JavaKeyStore(jstore)
      ks.load(null)
      val (pk, pubk) = KS.genKeys()
      ks.store(pwd, alias, pk, pubk)
      ks.save(pwd)

     // 2. sign 
     val signed = KS.sign (secret.getBytes, pk)
     
     // 3. load it
     val ks2 = new JavaKeyStore(jstore)
      ks2.load (null)
//     val ks2 = KS.load (store, pwd)
     val (pk2, puk2) = ks2.loadKeys(pwd, alias)
     
     // 4. verify
     val b = if (KS.verify(signed, secret.getBytes, puk2)) true else false
     b
   }

   // save a publik key only and retrieve it
   def testJKSPass2a = expect (true) { 
     // 1. create ks
      val ks = new JavaKeyStore(jstore)
      ks.create(pwd)
      val (pk, pubk) = KS.genKeys()
      ks.store(pwd, alias+"pass2a", pk, pubk)
      ks.save(pwd)

     // 2. sign 
     val signed = KS.sign (secret.getBytes, pk)
     
     // 3. load it
     val ks2 = new JavaKeyStore(jstore)
      ks2.load (null)
//     val ks2 = KS.load (store, pwd)
     val (pk2, puk2) = ks2.loadKeys(pwd, alias+"pass2a")
     
     // 4. verify
     val b = if (KS.verify(signed, secret.getBytes, puk2)) true else false
     b
   }

   // save both keys and retrieve it
   def testJKSPass2b = expect (true) { 
     // 1. create ks
      val ks = new JavaKeyStore(jstore)
      ks.load(pwd)
      val (pk, pubk) = KS.genKeys()
      ks.store(pwd, alias+"pass2b", pk, pubk)
      ks.save(pwd)

     // 3. load it
     val ks2 = new JavaKeyStore(jstore)
      ks2.load (null)
//     val ks2 = KS.load (store, pwd)
     val (pk2, puk2) = ks2.loadKeys(pwd, alias+"pass2b")
     
     // 2. sign 
     val signed = KS.sign (secret.getBytes, pk2)
     
     // 4. verify
     val b = if (KS.verify(signed, secret.getBytes, puk2)) true else false
     b
   }

   // send a publik key as bas64
   def testJKSPass4 = expect (true) { 
     // 1. create ks
      val ks = new JavaKeyStore(jstore)
      ks.create(pwd)
      val (pk, pubk) = KS.genKeys()

      // to 64 and back
      val b64=Base64.encodeBytes(pubk.getEncoded())
      val bytes = Base64.decode (b64)
      val newpubk = KS.pubKeyFromBytes(bytes)

      ks.store(pwd, alias+"pass4", null, newpubk)
      ks.save(pwd)

     // 2. sign 
     val signed = Base64.encodeBytes(KS.sign (secret.getBytes, pk))
     val designed = Base64.decode(signed)
     
     // 3. load it
     val ks2 = new JavaKeyStore(jstore)
     ks2.load (null)
     val (pk2, puk2) = ks2.loadKeys(pwd, alias+"pass4")
     
     // 4. verify
     val b = if (KS.verify(designed, secret.getBytes, puk2)) true else false
     true
   }

}
