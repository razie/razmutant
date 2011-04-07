/**
 * Razvan's public code. Copyright 2008 based on Apache license (share alike) see LICENSE.txt for
 * details. No warranty implied nor any liability assumed for this code.
 */
package com.razie.valueadd.security;

import java.io._
import java.security._
import java.security.spec._
import java.util.Date
import sun.security.x509._

trait RazProv {
	def signature:Signature
	def keyFactory:KeyFactory
	def keyGen:KeyPairGenerator
}

class SunProv extends RazProv {
  def keyFactory = KeyFactory.getInstance("DSA", "SUN");
  def signature = Signature.getInstance("SHA1withDSA", "SUN");
  def keyGen = {
    val kg = KeyPairGenerator.getInstance("DSA", "SUN");
    val random = SecureRandom.getInstance("SHA1PRNG", "SUN");
    kg.initialize(1024, random);
    kg
  }
}

class BcProv extends RazProv {
  def keyFactory = KeyFactory.getInstance("RSA", "BC");
  def signature = Signature.getInstance("SHA1withRSA", "BC");
  def keyGen = {
    // the simpler BC version
    val kg = KeyPairGenerator.getInstance("RSA", "BC");
       kg.initialize(1024);
       kg
  }
}

/** key store support */
object KS {
  def load (store:String, pwd:String) = { val ks = new JavaKeyStore(store); ks.load(pwd); ks}
  def create (store:String, pwd:String) = { val ks = new JavaKeyStore(store); ks.create(pwd); ks}
   
  var initialized=false
  var prov:RazProv=null;
 
  def init = {
	  if (! KS.initialized)
		    Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
		  KS.initialized=true
       prov=new BcProv
  }
  
  def keyFactory = prov.keyFactory
  def signature = prov.signature

  /** generate a key pair */
  def genKeys () : (PrivateKey, PublicKey) = {
		  init
val keyGen = prov.keyGen

    val pair = keyGen.generateKeyPair();
    val priv = pair.getPrivate();
    val pub = pair.getPublic();
   
    (priv,pub)
  }

  /** restore a public key from encoded bytes */
  def pubKeyFromBytes (encKey:Array[Byte]) = {
    val pubKeySpec = new X509EncodedKeySpec(encKey);
    val pubKey = keyFactory.generatePublic(pubKeySpec);
    pubKey
  }

  /** sign an array of bytes. The result should normally be then Byte64 encoded */
  def sign (in:Array[Byte], pk:PrivateKey):Array[Byte] = {
    /* Create a Signature object and initialize it with the private key */
    val dsa = KS.signature
    dsa.initSign(pk);

    /* Update and sign the data */
    dsa.update(in, 0, in.length);

    /* Now that all the data to be signed has been read in,
     generate a signature for it */

    dsa.sign(); // this returns the signed thing
  }

  /** verify a signed byte array */
  def verify (signed:Array[Byte], original:Array[Byte], pubKey:PublicKey) = {
    /* create a Signature object and initialize it with the public key */
    val sig = KS.signature
    sig.initVerify(pubKey);

    sig.update (original, 0, original.length)
      
    /* Update and verify the data */
    sig.verify(signed);
  }

  /** alias for the main certificate */
  final val CERT = "mainCert"
}

/** abstract keystore functionality. You create a store and keep keys in it... */
abstract class RazKeyStore (val fstore:String) {
	KS.init

  def load(pwd:String)
  def create (pwd:String)
  def save(pwd:String)
  def loadKeys (pwd:String, alias:String ): (PrivateKey, PublicKey)
  def store (pwd:String, alias:String, pk:PrivateKey, pubk:PublicKey)
}

/** very simple storage of keys as files in a folder
 * 
 *  @param store: a folder where the keys will be stored
 */
class SimpleKeyStore (storeDir:String) extends RazKeyStore (storeDir) {
  def load(pwd:String) = {}
  def create (pwd:String) = {new File (fstore).mkdirs()}
  def save(pwd:String) = {}

  def loadKeys (pwd:String, alias:String) : (PrivateKey, PublicKey) = {
    if (new File (fstore+"/"+alias+".pubkey").exists()) {
      val puf = new FileInputStream(fstore+"/"+alias+".pubkey");
      val encKey = new Array[Byte](puf.available());
      puf.read(encKey);
      puf.close();

      val pubKeySpec = new X509EncodedKeySpec(encKey);

      val pubKey = KS.keyFactory.generatePublic(pubKeySpec);

      // priv
      if (new File (fstore+"/"+alias+".pkey").exists()) {
        val pf = new FileInputStream(fstore+"/"+alias+".pkey");
        val pencKey = new Array[Byte](pf.available());
        pf.read(pencKey);
        pf.close();
        val pKeySpec = new PKCS8EncodedKeySpec(pencKey);
        val pKey = KS.keyFactory.generatePrivate(pKeySpec);
        (pKey, pubKey)
      } else
        (null, pubKey)
    } else (null, null)
  }

  def store (pwd:String, alias:String, pk:PrivateKey, pubk:PublicKey) {
    if (pk != null) {
      val key = new PKCS8EncodedKeySpec (pk.getEncoded()).getEncoded()
      val keyfos = new FileOutputStream(fstore+"/"+alias+".pkey");
      keyfos.write(key);
      keyfos.close();
    }

    val key = new X509EncodedKeySpec (pubk.getEncoded()).getEncoded()
    val keyfos = new FileOutputStream(fstore+"/"+alias+".pubkey");
    keyfos.write(key);
    keyfos.close();
  }
}
     
/** trying to get a handle on using the Java keystores 
 * 
 * the store is a folder - add a file :)
 * 
 */
class JavaKeyStore (storeDir:String) extends RazKeyStore (storeDir+"/jks") {
  var ks:KeyStore = KeyStore.getInstance("JCEKS");
  var cert:java.security.cert.Certificate=null

  def load(pwd:String) = {
    val ksbufin = new BufferedInputStream(new FileInputStream(fstore));
    ks.load(ksbufin, if(pwd == null) null else pwd.toCharArray())
    cert = ks.getCertificate(KS.CERT)
  }

  def create (pwd:String) = {
    ks.load(null, pwd.toCharArray());

    val t = KS.genKeys()
    cert = newCert(pwd, KS.CERT, new KeyPair(t._2, t._1))
      
    // store away the keystoreAgents.me().localdir + "/keys"
    var fos:java.io.FileOutputStream = null;
    try {
    	val f = new File(fstore)
    	if (f.exists()) f.delete()
    	f.getParentFile().mkdirs
      fos = new java.io.FileOutputStream(fstore);
      ks.store(fos, pwd.toCharArray);
    } finally {
      if (fos != null) {
        fos.close();
      }
    }

  }
   
  def save(pwd:String) = {
    // store away the keystore
    var fos:java.io.FileOutputStream = null;
    try {
      fos = new java.io.FileOutputStream(fstore);
      ks.store(fos, pwd.toCharArray);
    } finally {
      if (fos != null) {
        fos.close()//;Agents.me().localdir + "/keys"
      }
    }
  }

  private def newCert (pwd:String, alias:String, pair:KeyPair) = {
   // TODO enable this below when trying this class...
//   null
    val x = org.bouncycastle.x509.examples.AttrCertExample.createAcIssuerCert(pair.getPublic(), pair.getPrivate());
    ks.setCertificateEntry (alias, x)
    x
  }

  def loadKeys (pwd:String, alias:String ): (PrivateKey, PublicKey) = {
	// TODO SECU this should actually use the password
    val priv = ks.getKey(alias, alias.reverse.toCharArray())
  
    if (priv.isInstanceOf[PrivateKey]) {

      val cert = ks.getCertificate(alias);
//      val encodedCert = cert.getEncoded();

       val puk = cert.getPublicKey();
       
      (priv.asInstanceOf[PrivateKey], puk)
    } else {
      (null, priv.asInstanceOf[PublicKey])
    }
   
    /* save the certificate in a file named "suecert" */
//   FileOutputStream certfos = new FileOutputStream("suecert");
//   certfos.write(encodedCert);
//   certfos.close();

    // NORMALLY you'd send the certificate over...
   
//   val certfis = new FileInputStream(certName);
//   java.security.cert.CertificateFactory cf =
//      java.security.cert.CertificateFactory.getInstance("X.509");
//   java.security.cert.Certificate cert = cf.generateCertificate(certfis);

  }

  def store (pwd:String, alias:String, pk:PrivateKey, puk:PublicKey) {
	// TODO SECU this should use user password
	if (pk == null)
    ks.setKeyEntry(alias, puk, alias.reverse.toCharArray, null);
	else {
     val cert = newCert(pwd, alias, new KeyPair(puk, pk))
     ks.setKeyEntry(alias, pk, alias.reverse.toCharArray, Array[java.security.cert.Certificate] ( cert ));
	}
    save(pwd)
  }
}