package ca.nrc.cadc.beacon.web.view;

import ca.nrc.cadc.vos.VOSURI;

import java.net.URI;
import java.util.Date;

/**
 * Created by dustin on 28/06/16.
 */
public class LinkItem extends StorageItem
{
    public LinkItem(VOSURI uri, long sizeInBytes, Date lastModified,
                    boolean publicFlag, boolean lockedFlag,
                    URI[] writeGroupURIs, URI[] readGroupURIs,
                    String owner)
    {
        super(uri, sizeInBytes, lastModified, publicFlag, lockedFlag,
                writeGroupURIs, readGroupURIs, owner);
    }


    @Override
    public String getItemIconCSS() {
        return "glyphicon-link";
    }

    @Override
    public String getLinkURI() {
        return "/vospace/nodes" + uri.getPath();
    }
}
