/**
 * Razvan's code. Copyright 2008 by Razvan Cojocaru, see LICENSE.txt for
 * details. No warranty implied nor any liability assumed for this code.
 */

package com.razie.agent.network;

import razie.assets.AssetKey;

import com.razie.agent.network.Computer.Type;

public class ComputerImpl extends Device.Impl implements Computer {
     private Type type;

     public ComputerImpl(AssetKey ref, Type type) {
         super(ref);
         this.setType(type);
         this.getBrief().setIcon(type.toString().toLowerCase());
     }

     public Type getType() {
         return this.type;
     }

     public void setType(Type t) {
         this.type = t;
     }
 }