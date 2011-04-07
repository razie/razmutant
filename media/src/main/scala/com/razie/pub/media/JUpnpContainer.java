package com.razie.pub.media;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import razie.assets.AssetBrief;
import razie.assets.AssetKey;
import razie.assets.AssetLocation;
import razie.base.ActionItem;
import razie.base.ActionToInvoke;
import razie.base.AttrAccessImpl;
import razie.draw.DrawStream;
import razie.draw.Drawable3;
import razie.draw.Renderer;
import razie.draw.Technology;

import com.razie.pub.base.log.Log;
import com.razie.pub.comms.ServiceActionToInvoke;
import com.razie.pub.resources.RazIcons;

/**
 * this is really a generic container, but it matches UPNP 1:1
 * 
 * it's an already built upnp container
 * 
 * @author razvanc
 */
public class JUpnpContainer extends AttrAccessImpl implements UpnpNode, Drawable3, razie.assets.IReferenceable {
    public String contents;
    public String assetType; // setup in the scala version - i hate to...

    protected List<JUpnpContainer> jcontainers;
    protected List<AssetBrief> jitems;

    public List<JUpnpContainer> getContainers() {
        return jcontainers != null ? jcontainers : Collections.EMPTY_LIST;
    }

    // invoked before serving to client, if the container was cached
    public void refresh() {}
    public void refreshMeta() {}
    public void clear() {jcontainers = null; jitems = null;}
    
    public void addContainer(JUpnpContainer c) {
        if (jcontainers == null)
            jcontainers = new ArrayList<JUpnpContainer>();
        jcontainers.add(c);
        this.childCount++;
    }

    public List<AssetBrief> getItems() {
        return jitems != null ? jitems : Collections.EMPTY_LIST;
    }

    public void addItem(AssetBrief c) {
        if (jitems == null)
            jitems = new ArrayList<AssetBrief>();
        jitems.add(c);
        this.childCount++;
    }

    /**
     * @param title
     * @param id
     * @param parentID
     * @param contents comma separated strings: music,movie,photo
     */
    public JUpnpContainer(String title, AssetKey ref, String parentID, int children, String contents) {
        setAttr("dc\\:title", title);
        setAttr("upnp\\:class", "object.container.storageFolder");
        setAttr("upnp\\:storageUsed", "21");
        setAttr("upnp\\:writeStatus", "NOT_WRITABLE");

        this.ref = ref;
        this.parentID = parentID;
        this.assetType = this.contents = contents;
        this.childCount = children;
    }

    /**
     * @param title
     * @param id
     * @param parentID
     * @param contents comma separated strings: music,movie,photo
     */
    public JUpnpContainer(ActionItem title, AssetKey ref, String parentID, int children, String contents) {
        setAttr("dc\\:title", title.label);
        setAttr("upnp\\:class", "object.container.storageFolder");
        setAttr("upnp\\:storageUsed", "21");
        setAttr("upnp\\:writeStatus", "NOT_WRITABLE");

        this.ref = ref;
        this.parentID = parentID;
        this.assetType = this.contents = contents;
        this.childCount = children;
    }

    public String upnpID () { return ref.toUrlEncodedString(); }
    
    public String toUpnpXml(String... overwriteID) {
        String s = "\n<container ";
        s += attr("id", overwriteID.length > 0 ? overwriteID[0] : upnpID());
        s += attr("parentID", parentID);
        s += attr("childCount", String.valueOf(childCount));
        s += attr("restricted", restricted);
        s += attr("searchable", searchable);
        s += ">\n";

        s += this.toXml();

        if (contents != null) {
            for (String c : contents.split(",")) {
                if ("music".equals(c)) {
                    s += "\n<upnp:searchClass includeDerived=\"false\">object.item.audioItem.musicTrack</upnp:searchClass>";
                } else if ("movie".equals(c)) {
                    s += "\n<upnp:searchClass includeDerived=\"false\">object.item.videoItem.movie</upnp:searchClass>";
                } else if ("photo".equals(c)) {
                    s += "\n<upnp:searchClass includeDerived=\"false\">object.item.imageItem.photo</upnp:searchClass>";
                } else {
                    Log.logThis("WARN_UNKOWNCONTENTS: " + c);
                }
            }
        }

        s += "\n</container>\n";

        return s;
    }

    public String toString() {
        return this.toUpnpXml();
    }

    public static String attr(String name, String val) {
        return " " + name + "=\"" + val + "\" ";
    }

    public AssetKey   ref;
    public String parentID   = "";
    public int    childCount = 0;
    public String restricted = "0";
    public String searchable = "0";

    /** shortcut to render self - don't like controllers that much */
    public Object render(Technology t, DrawStream stream) {
        return Renderer.Helper.draw(this, t, stream);
    }

    public Renderer<JUpnpContainer> getRenderer(Technology technology) {
        return MyRenderer.singleton;
    }

    /** my renderer, MT-Safe */
    private static class MyRenderer implements Renderer<JUpnpContainer> {
        static MyRenderer singleton = new MyRenderer();

        public Object render(JUpnpContainer c, Technology technology, DrawStream stream) {
            if (Technology.UPNP.equals(technology)) {
               // THE META upnp
                return c.toUpnpXml();
            } else {
                if ("Folder".equals(c.ref.getType())) {
                    String lp = c.ref.getLocation().getLocalPath() == null ? "" : c.ref.getLocation()
                            .getLocalPath();

                    lp = lp + (lp.length() <= 0 || lp.endsWith("/") || lp.endsWith("\\") ? "" : "/");

                    // TODO reconcile GREF creation and avoid redoing this, use the one from
                    // container...
                    AssetKey ref = new AssetKey("Folder", lp + c.ref.getId(), AssetLocation.mutantEnv(c.ref
                            .getLocation().getHost(), ""));

                    // NavButton b = new NavButton(new ActionItem(c.ref.getEntityKey(),
                    // RazIcons.FOLDER),
                    // new MutantCmdActionToInvoke(cmdBROWSE, "ref", ref));
                    ActionToInvoke b1 = new ServiceActionToInvoke("cmd", new ActionItem(cmdBROWSE, c.ref
                            .getId()), "ref", ref, "type", c.assetType);

                    return b1.render(technology, stream);
                } else if ("UPNPBROWSER".equals(c.ref.getType())) {
                   ActionToInvoke b1 = new ServiceActionToInvoke("media", new ActionItem("Browse", c.ref
                         .getId()), "ref", c.ref, "type", c.assetType);
                    return b1.render(technology, stream);
                }
            }

            return c.toString();
        }
    }

    @Override
    public JUpnpContainer container () { return this; }

    @SuppressWarnings("rawtypes")
    public void writeContents (DrawStream out, int start, int count) {
       List l = new ArrayList();
       l.addAll(getContainers());
       l.addAll(getItems());

       MediaServerService.writesection(out, l.toArray(), start, count);
    }

    public AssetKey key() { return ref;}
    public AssetKey getKey() { return ref;}

    public static final ActionItem cmdBROWSE = new ActionItem("browse", RazIcons.FOLDER.name());
}
