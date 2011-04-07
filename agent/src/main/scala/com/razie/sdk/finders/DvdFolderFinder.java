package com.razie.sdk.finders;

import java.io.File;

import org.w3c.dom.Element;

import razie.assets.AssetBrief;

import com.razie.pub.base.files.SSFilesRazie;

/**
 * search dvd folders - actually it should be the IFO file and then extract the name from the folder
 * 
 */
public class DvdFolderFinder extends AssetFinder {
    public DvdFolderFinder() {
    }

    /**
     * c-tor from xml config
     * 
     * @param e is the <asset> element from user.xml
     */
    public void init(Element e) {
        super.init(e);

        SSFilesRazie.ORFileFilter f = new SSFilesRazie.ORFileFilter();
        f.add(new SSFilesRazie.RegExpFileFilter("VIDEO_TS\\.IFO"));

        this.filter = new AFFileFilter(f);
    }

    public AssetBrief getBrief(File file) {
        AssetBrief b = super.getBrief(file);

        // TODO leave the filename intact - it's used to infer jpg icons for instance as well as
        // find assets - too much code depends on the filename being an actual file :(

        // TODO grab the movie name somehow from the folder name
        b.setName("whatname????from folder???");

        return b;
    }

    public String toString() {
        return "DvdFolderFinder";
    }
}
