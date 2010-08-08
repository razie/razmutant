/**
 * Razvan's public code. Copyright 2008 based on Apache license (share alike) see LICENSE.txt for
 * details.
 */
package com.razie.media.upnpstuff;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import razie.base.ActionItem;
import com.razie.pub.comms.LightAuth;
import com.razie.pub.http.SoaNotHtml;

/**
 * mark a value as a upnp state variable
 * 
 * @author razvanc99
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target( { ElementType.METHOD })
@Inherited
public @interface UpnpVar {
    /** the value is a description of the thing */
    String descr();

    /**
     * weather this will generate events
     */
    boolean eventing() default false;
}
