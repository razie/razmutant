/*
 * $Id: RiCatalog.java,v 1.3 2007/07/11 20:10:25 razie Exp $
 * 
 * Copyright 2006 - 2007 The Members of the OSS through Java(TM) Initiative. All rights reserved.
 * Use is subject to license terms.
 */
package com.razie.dist.db;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import razie.base.AttrAccess;
import razie.base.AttrAccessImpl;
import com.razie.pub.base.data.RiXmlUtils;
import com.razie.pub.base.data.XmlDoc;
import com.razie.pub.base.log.Log;

/**
 * represents an updateable xml document, includes a list of changes and sync() capability
 * 
 * @author razvanc
 * 
 */
public class XmlDb extends XmlDoc implements DistDoc {
    public static final String ATTR_NAME     = "name";

    protected boolean          needsSave     = false;
    protected String           name;
    protected boolean          persistInFile = true;

    /** list of change elements, see Change() */
    List<Element>              changes       = new ArrayList<Element>();

    public XmlDoc load(String name, URL url) {
        super.load(name, url);
        if (AgentDbService.upgradeDb(this.root)) {
            this.needsSave = true;
        }
        return this;
    }

    /** initializes the database - use if you're not loading from URL or createFromString */
    public void initialize(String name) {
        this.name = name;

        // gotta create the database, instead
        this.document = RiXmlUtils.createDocument();
        this.root = this.document.createElement("db");
        this.root.setAttribute("dbver", String.valueOf(AgentDbService.curVersion));
        this.document.appendChild(this.root);
        this.root = this.document.getDocumentElement();

        this.getEntity("/db").appendChild(make("settings", new AttrAccessImpl("ver", "0")));
        this.getEntity("/db/settings").setAttribute("lastsyncver", "0");
        this.getEntity("/db/settings").appendChild(
                make("diffs", new AttrAccessImpl("someattr", "somevalue")));
        this.needsSave = true;
    }

    /**
     * to add, you'll find the parent somehow and then call this. don't recommend adding it yourself
     * via DOM...
     */
    public void add(String parentPath, String tagName, AttrAccess aa) {
        Element e = getEntity(parentPath);
        Element child = make(tagName, aa);
        e.appendChild(child);
        this.changes.add(Change(parentPath, tagName, aa));
        checkandincver();
    }

    /**
     * to add, you'll find the parent somehow and then call this. don't recommend adding it yourself
     * via DOM...
     */
    public void add(String parentPath, String tagName, Object... pairs) {
        add(parentPath, tagName, new AttrAccessImpl(pairs));
    }

    /**
     * to delete you need a uniquely identified node, pass the matching attr via the AA.
     * 
     * TODO support a set of IDs for matching, not just one...
     */
    public void delete(String parentPath, String tagName, AttrAccess aa) {
        internalDelete(parentPath, tagName, aa);
        this.changes.add(Change(parentPath, tagName, aa, true));
        checkandincver();
    }

    /**
     * to delete you need a uniquely identified node, pass the matching attr via the AA.
     * 
     * TODO support a set of IDs for matching, not just one...
     */
    private void internalDelete(String parentPath, String tagName, AttrAccess aa) {
        Element p = getEntity(parentPath);
        NodeList nodes = p.getElementsByTagName(tagName);
        String aname = aa.getPopulatedAttr().iterator().next();
        String avalue = (String) aa.getAttr(aname);

        for (int i = 0; i < nodes.getLength(); i++) {
            Element e = (Element) nodes.item(i);
            if (e.hasAttribute(aname) && avalue.equals(e.getAttribute(aname))) {
                p.removeChild(e);
            }
        }
    }

    /**
     * to add, you'll find the parent somehow and then call this. don't recommend adding it yourself
     * via DOM...
     */
    public void setAttr(String parentPath, String attrnm, String value) {
        Element e = getEntity(parentPath);
        e.setAttribute(attrnm, value);
        // TODO handle value null
        // TODO optimize and do not add a change if there's no actual change in value
        this.changes.add(Change(parentPath, attrnm, value));
        checkandincver();
    }

    /**
     * to add, you'll find the parent somehow and then call this. don't recommend adding it yourself
     * via DOM...
     */
    public void setAttr(String parentPath, Object... pairs) {
        Element e = getEntity(parentPath);
        for (int i = 0; i < pairs.length / 2; i++) {
            String name = (String) pairs[2 * i];
            String value = pairs[2 * i + 1].toString();
            e.setAttribute(name, value);
        // TODO handle value null
        // TODO optimize and do not add a change if there's no actual change in value
            this.changes.add(Change(parentPath, name, value));

            if (i == 0)
                checkandincver();
        }
    }

    private Element make(String tagName, AttrAccess... aa) {
        Element e = this.document.createElement(tagName);
        if (aa != null && aa.length > 0)
            for (String parm : aa[0].getPopulatedAttr()) {
                e.setAttribute(parm, aa[0].getAttr(parm).toString());
            }
        return e;
    }

    Element Change(String parentPath, String tagName, AttrAccess aa) {
        return Change(parentPath, tagName, aa, false);
    }

    Element Change(String parentPath, String tagName, AttrAccess aa, boolean remove) {
        Element diff = make("diff", new AttrAccessImpl("parentPath", parentPath, "tagName", tagName,
                "remove", (remove ? "y" : "n")));
        diff.appendChild(make("newnode", aa));
        return diff;
    }

    Element Change(String parentPath, String attr, String newval) {
        return make("diff", new AttrAccessImpl("parentPath", parentPath, "attr", attr, "newval", newval));
    }

    /**
     * increment version and move diffs into doc structure, clean current diffs, prepare for save to
     * disk
     */
    public boolean prepareForSave() {
        if (this.changes.size() > 0) {
            Element diffs = getEntity("/db/settings/diffs");
            Element diff = make("diff", new AttrAccessImpl("ver", this.getAttr("/db/settings/@ver"),
                    "tstamp", String.valueOf(System.currentTimeMillis())));
            diffs.appendChild(diff);

            for (Element e : changes) {
                diff.appendChild(e);
            }

            this.changes.clear();
            return true;
        }

        boolean temp = needsSave;
        needsSave = false;
        return temp;
    }

    private void checkandincver() {
        if (this.changes.size() == 1) {
            // inc the ver
            Element settings = getEntity("/db/settings");
            int myver = Integer.parseInt(settings.getAttribute("ver"));
            settings.setAttribute("ver", String.valueOf(myver + 1));
        }
    }

    /**
     * TODO somehow notify the master that the copy has been updated...
     */
    protected void syncfrom(XmlDb source) {
        int srcver = Integer.parseInt(source.getAttr("/db/settings/@ver"));
        int myver = Integer.parseInt(getAttr("/db/settings/@ver"));
        int mylsver = Integer.parseInt(getAttr("/db/settings/@lastsyncver"));
        if (srcver == myver) {
            Log.logThis("two db syncd identically");
        } else if (srcver < myver) {
            Log.logThis("ERR_SOURCEDB_OLDER source db IS OILDER");
        } else {
            if (mylsver != myver) {
                // not sure why this is relevant...
                Log.logThis("WARN_DB_CONFLICT BOTH DB CHANGED - CONFLICT");
            }

            Element diffs = getEntity("/db/settings/diffs");
            for (int ver = myver + 1; ver <= srcver; ver++) {
                String tstamp = source.getAttr("/db/settings/diffs/diff[@ver='" + String.valueOf(ver)
                        + "']/@tstamp");
                Element newdiff = make("diff", new AttrAccessImpl("ver", String.valueOf(ver), "tstamp",
                        tstamp));
                diffs.appendChild(newdiff);

                for (Element diff : source.listEntities("/db/settings/diffs/diff[@ver='"
                        + String.valueOf(ver) + "']/diff")) {
                    if (diff.hasAttribute("tagName") && diff.hasAttribute("remove")
                            && diff.getAttribute("remove").equals("y")) {
                        Element node = XmlDoc.listEntities(diff, "newnode").get(0);
                        AttrAccess aa = new AttrAccessImpl();
                        Element d = make("diff", new AttrAccessImpl("parentPath", diff
                                .getAttribute("parentPath"), "tagName", diff.getAttribute("tagName"),
                                "remove", diff.getAttribute("remove")));
                        Element nn = make("newnode");
                        for (int a = 0; a < node.getAttributes().getLength(); a++) {
                            String n = node.getAttributes().item(a).getNodeName();
                            String v = node.getAttributes().item(a).getNodeValue();
                            aa.setAttr(n, v);
                            nn.setAttribute(n, v);
                        }
                        d.appendChild(nn);
                        newdiff.appendChild(d);
                        this
                                .internalDelete(diff.getAttribute("parentPath"),
                                        diff.getAttribute("tagName"), aa);
                    } else if (diff.hasAttribute("tagName")) {
                        Element node = XmlDoc.listEntities(diff, "newnode").get(0);
                        Element newnode = make(diff.getAttribute("tagName"));
                        Element d = make("diff", new AttrAccessImpl("parentPath", diff
                                .getAttribute("parentPath"), "tagName", diff.getAttribute("tagName")));
                        Element nn = make("newnode");
                        for (int a = 0; a < node.getAttributes().getLength(); a++) {
                            String n = node.getAttributes().item(a).getNodeName();
                            String v = node.getAttributes().item(a).getNodeValue();
                            newnode.setAttribute(n, v);
                            nn.setAttribute(n, v);
                        }
                        d.appendChild(nn);
                        newdiff.appendChild(d);
                        this.getEntity(diff.getAttribute("parentPath")).appendChild(newnode);
                    } else {
                        this.getEntity(diff.getAttribute("parentPath")).setAttribute(
                                diff.getAttribute("attr"), diff.getAttribute("newval"));
                        Element d = make("diff", new AttrAccessImpl("parentPath", diff
                                .getAttribute("parentPath"), "attr", diff.getAttribute("attr"), "newval",
                                diff.getAttribute("newval")));
                        newdiff.appendChild(d);
                    }
                }
            }

            Element settings = getEntity("/db/settings");
            settings.setAttribute("ver", String.valueOf(srcver));
            settings.setAttribute("lastsyncver", String.valueOf(srcver));
        }
    }

    /** can disable persisting in file - call it right after constructor */
    public void setPersistInFile(boolean f) {
        this.persistInFile = f;
    }

    /**
     * xmlize into file. If file NULL, return xmlized string. don't call this without
     * prepareForSave()
     */
    public String xmlize(String fname) {
        return RiXmlUtils.writeDoc(this.getDocument(), fname == null ? null : new File(fname));
    }

    /* ------------------------------ DistDb vvv ------------------------------ */

    public void applyDiffs(int fromVersion, int toVersion, String sdiffs) {
        XmlDoc source = XmlDoc.createFromString("diffs", sdiffs);

        int srcver = Integer.parseInt(source.getAttr("/db/settings/@ver"));
        int myver = Integer.parseInt(getAttr("/db/settings/@ver"));
        int mylsver = Integer.parseInt(getAttr("/db/settings/@lastsyncver"));
        if (srcver == myver) {
            Log.logThis("two db syncd identically");
        } else if (srcver < myver) {
            Log.logThis("ERR_SOURCEDB_OLDER source db IS OILDER");
        } else {
            if (mylsver != myver) {
                // not sure why this is relevant...
                Log.logThis("WARN_DB_CONFLICT BOTH DB CHANGED - CONFLICT");
            }

            Element diffs = getEntity("/db/settings/diffs");
            for (int ver = myver + 1; ver <= srcver; ver++) {
                String tstamp = source.getAttr("/db/settings/diffs/diff[@ver='" + String.valueOf(ver)
                        + "']/@tstamp");
                Element newdiff = make("diff", new AttrAccessImpl("ver", String.valueOf(ver), "tstamp",
                        tstamp));
                diffs.appendChild(newdiff);

                for (Element diff : source.listEntities("/db/settings/diffs/diff[@ver='"
                        + String.valueOf(ver) + "']/diff")) {
                    if (diff.hasAttribute("tagName") && diff.hasAttribute("remove")
                            && diff.getAttribute("remove").equals("y")) {
                        Element node = XmlDoc.listEntities(diff, "newnode").get(0);
                        AttrAccess aa = new AttrAccessImpl();
                        Element d = make("diff", new AttrAccessImpl("parentPath", diff
                                .getAttribute("parentPath"), "tagName", diff.getAttribute("tagName"),
                                "remove", diff.getAttribute("remove")));
                        Element nn = make("newnode");
                        for (int a = 0; a < node.getAttributes().getLength(); a++) {
                            String n = node.getAttributes().item(a).getNodeName();
                            String v = node.getAttributes().item(a).getNodeValue();
                            aa.setAttr(n, v);
                            nn.setAttribute(n, v);
                        }
                        d.appendChild(nn);
                        newdiff.appendChild(d);
                        this
                                .internalDelete(diff.getAttribute("parentPath"),
                                        diff.getAttribute("tagName"), aa);
                    } else if (diff.hasAttribute("tagName")) {
                        Element node = XmlDoc.listEntities(diff, "newnode").get(0);
                        Element newnode = make(diff.getAttribute("tagName"));
                        Element d = make("diff", new AttrAccessImpl("parentPath", diff
                                .getAttribute("parentPath"), "tagName", diff.getAttribute("tagName")));
                        Element nn = make("newnode");
                        for (int a = 0; a < node.getAttributes().getLength(); a++) {
                            String n = node.getAttributes().item(a).getNodeName();
                            String v = node.getAttributes().item(a).getNodeValue();
                            newnode.setAttribute(n, v);
                            nn.setAttribute(n, v);
                        }
                        d.appendChild(nn);
                        newdiff.appendChild(d);
                        this.getEntity(diff.getAttribute("parentPath")).appendChild(newnode);
                    } else {
                        this.getEntity(diff.getAttribute("parentPath")).setAttribute(
                                diff.getAttribute("attr"), diff.getAttribute("newval"));
                        Element d = make("diff", new AttrAccessImpl("parentPath", diff
                                .getAttribute("parentPath"), "attr", diff.getAttribute("attr"), "newval",
                                diff.getAttribute("newval")));
                        newdiff.appendChild(d);
                    }
                }
            }

            Element settings = getEntity("/db/settings");
            settings.setAttribute("ver", String.valueOf(srcver));
            settings.setAttribute("lastsyncver", String.valueOf(srcver));
        }
    }

    // TODO implement this properly
    public String TODOgetDiffs(int fromVersion, int toVersion) {
        prepareForSave();

        XmlDb diffsdb = new XmlDb();
        diffsdb.persistInFile = false;
        diffsdb.initialize("diffs_" + this.name);

        diffsdb.setAttr("/db/settings/@ver", getAttr("/db/settings/@ver"));
        diffsdb.setAttr("/db/settings/@lastsyncver", getAttr("/db/settings/@lastsyncver"));

        Element diffs = diffsdb.getEntity("/db/settings/diffs");

        for (int ver = fromVersion+1; ver <= toVersion; ver++) {
            Element d = this.getEntity("/db/settings/diffs/diff[@ver='" + String.valueOf(ver) + "']");

            // mean bastard - don't have all the diffs...
            if (d == null)
                return null;

            // deep clone
            diffs.appendChild(d.cloneNode(true));
        }

        String s = diffsdb.xmlize(null);
        return s;
    }

    // now i just get myself - need to optimize this
    public String getDiffs(int fromVersion, int toVersion) {
        prepareForSave();

        String s = this.xmlize(null);
        return s;
    }

    public int getFormatVersion() {
        int ver = this.root.hasAttribute("dbver") ? Integer.parseInt(this.root.getAttribute("dbver")) : 0;
        return ver;
    }

    public int getLocalVersion() {
        int myver = Integer.parseInt(getAttr("/db/settings/@ver"));
        return myver;
    }

    public void upgradeFormat(int fromFormatVer, int toFromatVer) {
        // TODO implement properly -
        if (AgentDbService.upgradeDb(this.root)) {
            this.needsSave = true;
        }
    }

    public int getLastSyncVersion() {
        int myver = Integer.parseInt(getAttr("/db/settings/@lastsyncver"));
        return myver;
    }

    public void purgeDiffs(int fromVersion, int toVersion) {
        // TODO Auto-generated method stub
        
    }

    /* ------------------------------ DistDb ^^^ ------------------------------ */
}
