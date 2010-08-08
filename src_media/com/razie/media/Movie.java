package com.razie.media;

import razie.assets.AssetBrief;
import razie.assets.AssetImpl;
import razie.assets.FileAssetBrief;

import razie.base.ActionItem;
import com.razie.pub.lightsoa.SoaAsset;

/**
 * stands for a movie asset. movies can be played with a player
 * 
 * @author razvanc
 * 
 */
@SoaAsset(meta=Movie.sCLASS, descr="a movie")
public class Movie extends AssetImpl {
    public static final String     sCLASS    = "Movie";
    public static final ActionItem META      = new ActionItem(sCLASS, "/public/pics/IceAgeScrat.png");

    public Movie(AssetBrief brief) {
        super(brief);
        
        brief.setLargeDesc(brief.player());
        brief.setName(nameFromFile(((FileAssetBrief)brief).getFileName()));
    }

	      // TODO this was commented out in AssetImpl - is it needed here?
	      // this is set now in Mmovie

	      // String desc = "Players: ";
	      // for (SdkPlayer player : PlayerRegistry.getPlayers().values()) {
	      // if (player.canPlay(b)) {
	      // desc += player.getBrief().getName() + "-";
	      // }
	      // }
	      // b.setLargeDesc(desc);


	/** the preferred naming convention is polish: ThisIsTheMovie */
	private String nameFromFile(String fileName) {
		// cut the extension
		String name = fileName.replaceFirst("\\.[A-Za-z0-9]*$", "");

		// remove stuff in [] - usually are tags
		// TODO add to tags...of course...
		name = name.replaceAll("\\[.*\\]", "");
		
		// replace special chars with spaces
		name = name.replaceAll("[_\\[\\]\\(\\)]", " ");

		// TODO find caps/lower and insert spaces

		return name;
	}

}
