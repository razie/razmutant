/**
 * Razvan's public code. Copyright 2008 based on Apache license (share alike) see LICENSE.txt for
 * details.
 */
package com.razie.pub.upnpscala;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * mark a class with UPNP service attributes
 * 
 * @author razvanc99
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target( { ElementType.TYPE })
@Inherited
public @interface IsUpnpService {
	String serviceType();

	String serviceId();

	String SCPDURL();

	String controlURL();

	String eventSubURL();
}
