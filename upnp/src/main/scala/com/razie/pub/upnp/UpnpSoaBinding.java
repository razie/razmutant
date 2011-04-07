/**
 * Razvan's public code. Copyright 2008 based on Apache license (share alike) see LICENSE.txt for
 * details.
 */
package com.razie.pub.upnp;

import org.cybergarage.upnp.Action;

import razie.base.AttrAccess;
import razie.base.AttrAccessImpl;
import com.razie.pub.base.log.Log;
import com.razie.pub.lightsoa.SoaBinding;
import com.razie.pub.lightsoa.SoaMethod;
import com.razie.pub.lightsoa.SoaResponse;

/**
 * call services via upnp. Will receive the upnp call (if listener properly registered in the Device
 * and will forward to the method, extracting all input parms. The result is populated as output
 * parms.
 * 
 * No validation is done as to in/out parms etc.
 * 
 * SoaStreamables are not supported yet by the underlying library. Anything you stream will be piled
 * into the Result
 * 
 * @author razvanc
 */
public class UpnpSoaBinding extends SoaBinding {

    public UpnpSoaBinding(Object service, String serviceName) {
        super(service, serviceName);
    }

    public boolean actionControlReceived(Action action) {
        String actionName = action.getName();
        if (methods.containsKey(actionName)) {
            logger.log("UPNP_SOA_" + actionName + ": ");
            action.print();

            AttrAccess args = new AttrAccessImpl();

            // setup the parms
            SoaMethod mdesc = methods.get(actionName).getAnnotation(SoaMethod.class);
            for (String arg : mdesc.args()) {
                args.setAttr(arg, action.getArgumentValue(arg));
            }

            SoaResponse resp = null;

            Object res = invoke(this.service, actionName, args);

            if (res != null && res instanceof SoaResponse) {
                resp = (SoaResponse) res;
            } else if (res != null && res instanceof String) {
                resp = new SoaResponse();
                resp.setAttr("Result", res.toString());
            } else if (res != null) {
                // whatever did it return?
                throw new IllegalArgumentException("lightsoa method didn't return a LightSoaResponse method="
                        + action + " on target class=" + service.getClass().getName());
            }

            for (String arg : resp.getPopulatedAttr()) {
                action.getArgument(arg).setValue((String) resp.getAttr(arg));
            }

            return true;
        }

        logger.log("UPNP_SOA_UNKWNOWNACTION: ");
        action.print();
        return false;
    }
    
    protected final static Log logger = Log.factory.create("soa", UpnpSoaBinding.class.getSimpleName());
}
