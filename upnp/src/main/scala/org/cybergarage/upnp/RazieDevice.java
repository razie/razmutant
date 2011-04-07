/******************************************************************
*
*   custom derived class to workaround sbt's lack of jar file order
*
******************************************************************/

package org.cybergarage.upnp;

import java.net.*;
import java.io.*;
import java.util.*;

import org.cybergarage.net.*;
import org.cybergarage.http.*;
import org.cybergarage.util.*;
import org.cybergarage.xml.*;
import org.cybergarage.soap.*;

import org.cybergarage.upnp.ssdp.*;
import org.cybergarage.upnp.device.*;
import org.cybergarage.upnp.control.*;
import org.cybergarage.upnp.event.*;
import org.cybergarage.upnp.xml.*;

public class RazieDevice extends Device
{
    ////////////////////////////////////////////////
    //  Constructor
    ////////////////////////////////////////////////

    public RazieDevice(Node root, Node device)
    {
        super (root, device);
    }

    public RazieDevice()
    {
        this(null, null);
    }
    
    public RazieDevice(Node device)
    {
        this(null, device);
    }

    public RazieDevice(File descriptionFile) throws InvalidDescriptionException
    {
        super(descriptionFile);
    }

    /** RazvanC 2008-03 allows descr files to be in jar files in the classpath, for compact runnables */
    public RazieDevice(URL descriptionURL) throws InvalidDescriptionException, IOException
    {
        super(descriptionURL);
    }

    public RazieDevice(String descriptionFileName) throws InvalidDescriptionException
    {
        super(descriptionFileName);
    }

}

