package com.razie.agent.network;


/**
 * a computer has a generic operating system, i.e. it's not a specific-functionality limited device
 * 
 * @author razvanc
 * 
 */
public interface Computer extends Device {
   
   /** this is a rough indicator of the device's capablities...*/
    public static enum Type {
        DESKTOP, LAPTOP, TABLET, PROXY, SERVER, ROUTER, DEVICE
    }


    public Type getType();

    public void setType(Type t);

}
