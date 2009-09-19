/**
 * 
 */
package com.razie.mutant;

import com.razie.pub.base.data.HtmlRenderUtils.HtmlTheme;

public class MutantDarkTheme extends HtmlTheme {
    static String[] tags = {
                                 "<head><link rel=\"stylesheet\" type=\"text/css\" href=\"/mutant/style.css\" /></head><body link=\"yellow\" vlink=\"yellow\">",
                                 "</body>", "<html>", "</html>" };

    public String get(int what) {
        return tags[what];
    }
}