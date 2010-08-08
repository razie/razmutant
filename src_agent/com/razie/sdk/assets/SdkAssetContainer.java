package com.razie.sdk.assets;

import java.util.ArrayList;
import java.util.List;

import razie.assets.AssetBrief;
import razie.assets.AssetImpl;

import razie.base.AttrAccess;
import razie.base.AttrAccessImpl;

/**
 * upnp has a concept of generic containers containing containers and items...make sense.
 * 
 * containers can represent folders, categories, channels god knows what else...
 * 
 * @author razvanc
 */
public class SdkAssetContainer extends AssetImpl {

    public CType                    t;
    public List<SdkAssetContainer> containers = new ArrayList<SdkAssetContainer>();
    public List<AssetBrief>     items      = new ArrayList<AssetBrief>();

    public SdkAssetContainer(CType type) {
        this.t = type;
    }

    public SdkAssetContainer(AssetBrief brief) {
        super(brief);
        this.t = CType.valueOf(brief.getKey().getMeta());
    }

    public static boolean isContainer (AssetBrief brief) {
        if (CType.valueOf(brief.getKey().getMeta()) == null) {
            return false;
        }
        return true;
    }
    
    private static AttrAccess UPNP_TYPES_MAP;
    static {
        UPNP_TYPES_MAP = new AttrAccessImpl(CType.FOLDER.toString(), "", CType.CATEGORY.toString(), "",
                CType.CHANNEL.toString(), "", CType.SERIES.toString(), "");
    }
}
