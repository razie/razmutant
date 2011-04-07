/**
 * Razvan's code. Copyright 2008 based on Apache (share alike) see LICENSE.txt for details.
 */
package com.razie.media.assets

import com.razie.media.MediaUtils
import com.razie.pub.assets._
import razie.draw.DrawStream
import com.razie.pub.lightsoa.SoaAsset
import com.razie.pub.lightsoa.SoaMethod
import com.razie.pub.lightsoa.SoaResponse
import com.razie.pub.lightsoa.SoaStreamable
import razie.assets._

object MediaLocationStatics {

	final val META = "razie.media.MediaLocation"
	
   def brief (ref:AssetKey) = {
	  val b = new FileAssetBriefImpl () 
	   b.setKey(ref)
	   b
	  // TODO add more info
   }
}

/**
 * a location containing media. has functionality to browse and find the 
 * respective media, as well as managing/organizing it.
 * 
 * @author razvanc99
 * 
 */
@SoaAsset(meta = MediaLocationStatics.META, descr = "a location containing media")
class MediaLocation (val key:AssetKey) {

    @SoaMethod(descr = "list media", args = Array("movie"))
    def list (mediaType:String ) : List[AssetKey] = {
        List()
    }

    @SoaMethod(descr = "concatenate two values", args = Array("parm1", "parm2" ))
    def concatenate(parm1:String, parm2:String) :SoaResponse ={
        // that's how it's done in UPnP
        new SoaResponse("Result", parm1 + parm2);
    }

    /** details */
    @SoaMethod(descr = "display the details screen")
    @SoaStreamable
    def details(out:DrawStream) ={
        // TODO figure out protocol
        MediaUtils.browse("http", key, "", out);
    }
}
