/**
 * Razvan's code. Copyright 2008 based on Apache (share alike) see LICENSE.txt for details.
 */
package com.razie.assets.samples

import razie.draw.DrawStream
import com.razie.pub.lightsoa.SoaAsset
import com.razie.pub.lightsoa.SoaMethod
import com.razie.pub.lightsoa.SoaResponse
import com.razie.pub.lightsoa.SoaStreamable
import razie.assets._

/**
 * a sample lightsoa service
 * 
 * @author razvanc99
 * 
 */
//TODO @SoaAsset() {val meta = "raz.test.Player", val descr = "test player asset"}
@SoaAsset(meta = "raz.test.Player", descr = "test player asset")
class SampleAsset (val key:AssetKey){

    //TODO @SoaMethod(){val descr = "play a movie", val args = Array("movie")}
    @SoaMethod(descr = "play a movie", args = Array("movie"))
    def play(movie:String ) :String ={
        val key = AssetKey.fromString(movie);
        if (key == null) {
            throw new IllegalArgumentException("Movie not found: " + movie);
        }
        key.getId();
    }

    //TODO @SoaMethod(){val descr = "concatenate two values", val args = Array("parm1", "parm2" )}
    @SoaMethod(descr = "concatenate two values", args = Array("parm1", "parm2" ))
    def concatenate(parm1:String, parm2:String) :SoaResponse ={
        // that's how it's done in UPnP
        new SoaResponse("Result", parm1 + parm2);
    }

    //TODO @SoaMethod(){val descr = "does nothing", val args = Array("parm1", "parm2" )}
    @SoaMethod(descr = "does nothing", args = Array("parm1", "parm2" ))
    def doNothing(parm1:String, parm2:String) ={
    }

    /** note that it cant have the same name as the other one */
    //TODO @SoaMethod(){val descr = "concatenate two values", val args = Array( "parm1", "parm2" )}
    @SoaMethod(descr = "concatenate two values", args = Array( "parm1", "parm2" ))
    @SoaStreamable
    def concatenateStream(out:DrawStream, parm1:String, parm2:String) ={
        out.write(parm1 + parm2);
    }
}
