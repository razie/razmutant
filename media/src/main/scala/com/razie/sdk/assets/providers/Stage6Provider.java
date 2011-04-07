package com.razie.sdk.assets.providers;

import com.razie.pub.comms.Comms;
import com.razie.pub.playground.Translator;
import com.razie.pub.playground.TranslatorImpl;
import com.razie.pubstage.comms.HtmlContents;
import com.razie.pubstage.comms.ObjectStream;

/** mutant as a remote provider */
public class Stage6Provider extends Provider.Impl implements Provider {

    public Stage6Provider() {
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
        return true;
    }

    public ObjectStream browse(String type, String what) {
        // TODO Auto-generated method stub
        return null;
    }

    public ObjectStream list(String type, String category, String channel, String tags, String location) {
        String url = "http://www.stage6.com/" + channel + "/videos/";
        String otherList = (Comms.readUrl(url));
        otherList = HtmlContents.justBody(otherList);

        // replace the play targets

        return new ObjectStream.Impl(otherList);
    }
}