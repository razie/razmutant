package com.razie.sdk.assets.providers;

import razie.assets.AssetKey;
import razie.assets.AssetLocation;

import com.razie.pub.comms.Agents;
import com.razie.pub.playground.Translator;
import com.razie.pub.playground.TranslatorImpl;
import com.razie.pubstage.comms.HtmlContents;
import com.razie.pubstage.comms.ObjectStream;
import com.razie.pubstage.comms.StrFilter;

/**
 * mutant as a remote provider
 * 
 * @author razvanc
 * 
 */
public class MutantProvider extends Provider.Impl implements Provider {
    private String name;
    String         ip;
    String         port;

    /** @parm name the agent's name; not its hostname */
    public MutantProvider(String name) {
        this.name = name;
        ip = Agents.agent(name).ip;
        port = Agents.agent(name).port;
    }

    public Translator getBrowseTranslator() {
        return new TranslatorImpl() {
            public ObjectStream translate(ObjectStream input) {
                return input;
            }
        };
    }

    public Translator getListTranslator() {
        return new TranslatorImpl() {
            public ObjectStream translate(ObjectStream input) {
                return input;
            }
        };
    }

    public boolean isUp() {
        return Agents.agent(name).isUpNow();
    }

    // TODO acutally stream stuff one by one as they're read...
    public ObjectStream browse(String type, String what) {
        // all good - can go there
        AssetKey folder = new AssetKey("Folder", what, AssetLocation.mutantEnv(name, ""));
        String url = "http://" + ip + ":" + port + "/mutant/cmd/browse?ref="
                + folder.toUrlEncodedString();

        StrFilter f1 = makeBrowseFilter(ip, name, Agents.me().ip);
        String otherList = new HtmlContents(url, f1).readAll();

        // replace the play targets
//        otherList = otherList.replaceAll("/mutant/cmd/details\\?", "/mutant/cmd/remoteDetails?host=" + name
//                + "&");

        return new ObjectStream.Impl(otherList);
    }

    // TODO acutally stream stuff one by one as they're read...
    public ObjectStream list(String type, String category, String channel, String tags, String location) {
        String targetcmd = "http://" + ip + ":" + port + "/mutant/cmd/";
        String url = targetcmd + "list?type=" + type;
        if (category != null)
            url += "&category=" + category;
        if (location != null)
            url += "&location=" + location;

        StrFilter f1 = makeAssetsFilter(ip, name, Agents.me().ip);
        String otherList = new HtmlContents(url, f1).readAll();

        return new ObjectStream.Impl(otherList);
    }
    
    public static StrFilter makeAssetsFilter(String remoteIp, String remoteHost, String myIp) {
        String port = Agents.me().port;
        String rport = Agents.agent(remoteHost).port;
        StrFilter f1 = StrFilter.regexp(remoteIp + ":" + rport + "/mutant/cmd/play", myIp + ":" + port + "/mutant/cmd/play", remoteIp + ":" + rport + "/mutant/assets/details\\?", myIp + ":" + port + "/mutant/cmd/remoteDetails?host=" + remoteHost + "&");
        return f1;
     }

     public static StrFilter makeBrowseFilter(String remoteIp, String remoteHost, String myIp) {
        String port = Agents.me().port;
        String rport = Agents.agent(remoteHost).port;
        StrFilter f1 = StrFilter.regexp(remoteIp + ":" + rport + "/mutant/cmd/play", myIp + ":" + port + "/mutant/cmd/play", remoteIp + ":" + rport + "/mutant/cmd/browse", myIp + ":" + port + "/mutant/cmd/browse", remoteIp + ":" + rport + "/mutant/assets/details\\?", myIp + ":" + port + "/mutant/cmd/remoteDetails?host=" + remoteHost + "&");
        return f1;
     }

}