/*
 ************************************************************************
 *******************  CANADIAN ASTRONOMY DATA CENTRE  *******************
 **************  CENTRE CANADIEN DE DONNÉES ASTRONOMIQUES  **************
 *
 *  (c) 2016.                            (c) 2016.
 *  Government of Canada                 Gouvernement du Canada
 *  National Research Council            Conseil national de recherches
 *  Ottawa, Canada, K1A 0R6              Ottawa, Canada, K1A 0R6
 *  All rights reserved                  Tous droits réservés
 *
 *  NRC disclaims any warranties,        Le CNRC dénie toute garantie
 *  expressed, implied, or               énoncée, implicite ou légale,
 *  statutory, of any kind with          de quelque nature que ce
 *  respect to the software,             soit, concernant le logiciel,
 *  including without limitation         y compris sans restriction
 *  any warranty of merchantability      toute garantie de valeur
 *  or fitness for a particular          marchande ou de pertinence
 *  purpose. NRC shall not be            pour un usage particulier.
 *  liable in any event for any          Le CNRC ne pourra en aucun cas
 *  damages, whether direct or           être tenu responsable de tout
 *  indirect, special or general,        dommage, direct ou indirect,
 *  consequential or incidental,         particulier ou général,
 *  arising from the use of the          accessoire ou fortuit, résultant
 *  software.  Neither the name          de l'utilisation du logiciel. Ni
 *  of the National Research             le nom du Conseil National de
 *  Council of Canada nor the            Recherches du Canada ni les noms
 *  names of its contributors may        de ses  participants ne peuvent
 *  be used to endorse or promote        être utilisés pour approuver ou
 *  products derived from this           promouvoir les produits dérivés
 *  software without specific prior      de ce logiciel sans autorisation
 *  written permission.                  préalable et particulière
 *                                       par écrit.
 *
 *  This file is part of the             Ce fichier fait partie du projet
 *  OpenCADC project.                    OpenCADC.
 *
 *  OpenCADC is free software:           OpenCADC est un logiciel libre ;
 *  you can redistribute it and/or       vous pouvez le redistribuer ou le
 *  modify it under the terms of         modifier suivant les termes de
 *  the GNU Affero General Public        la “GNU Affero General Public
 *  License as published by the          License” telle que publiée
 *  Free Software Foundation,            par la Free Software Foundation
 *  either version 3 of the              : soit la version 3 de cette
 *  License, or (at your option)         licence, soit (à votre gré)
 *  any later version.                   toute version ultérieure.
 *
 *  OpenCADC is distributed in the       OpenCADC est distribué
 *  hope that it will be useful,         dans l’espoir qu’il vous
 *  but WITHOUT ANY WARRANTY;            sera utile, mais SANS AUCUNE
 *  without even the implied             GARANTIE : sans même la garantie
 *  warranty of MERCHANTABILITY          implicite de COMMERCIALISABILITÉ
 *  or FITNESS FOR A PARTICULAR          ni d’ADÉQUATION À UN OBJECTIF
 *  PURPOSE.  See the GNU Affero         PARTICULIER. Consultez la Licence
 *  General Public License for           Générale Publique GNU Affero
 *  more details.                        pour plus de détails.
 *
 *  You should have received             Vous devriez avoir reçu une
 *  a copy of the GNU Affero             copie de la Licence Générale
 *  General Public License along         Publique GNU Affero avec
 *  with OpenCADC.  If not, see          OpenCADC ; si ce n’est
 *  <http://www.gnu.org/licenses/>.      pas le cas, consultez :
 *                                       <http://www.gnu.org/licenses/>.
 *
 *
 ************************************************************************
 */

package ca.nrc.cadc.beacon.web.view;

import ca.nrc.cadc.auth.AuthenticationUtil;
import ca.nrc.cadc.beacon.web.StorageItemFactory;
import ca.nrc.cadc.date.DateUtil;
import ca.nrc.cadc.net.NetUtil;
import ca.nrc.cadc.reg.client.RegistryClient;
import ca.nrc.cadc.util.StringUtil;
import ca.nrc.cadc.vos.ContainerNode;
import ca.nrc.cadc.vos.Node;
import ca.nrc.cadc.vos.VOS;
import ca.nrc.cadc.vos.VOSURI;
import ca.nrc.cadc.vos.client.VOSpaceClient;

import javax.security.auth.Subject;
import java.net.URI;
import java.security.PrivilegedExceptionAction;
import java.util.*;


public class FullStorageItemIterator extends AbstractStorageItemIterator
{
    private Iterator<StorageItem> cache = null;
    private VOSURI current = null;

    private final int pageSize;
    private final VOSURI folderURI;
    int tally = 0;
    int pageCount = 0;


    public FullStorageItemIterator(final StorageItemFactory storageItemFactory,
                                   final VOSURI folderURI, final int pageSize)
    {
        super(storageItemFactory);

        this.pageSize = pageSize;
        this.folderURI = folderURI;

        fillCache();
    }


    boolean fillCache()
    {
        try
        {
            return makeBabies();
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    /**
     * Returns {@code true} if the iteration has more elements.
     * (In other words, returns {@code true} if {@link #next} would
     * return an element rather than throwing an exception.)
     *
     * @return {@code true} if the iteration has more elements
     */
    @Override
    public boolean hasNext()
    {
        return cache.hasNext() || (fillCache() && cache.hasNext());
    }

    /**
     * Returns the next element in the iteration.
     *
     * @return the next element in the iteration
     * @throws NoSuchElementException if the iteration has no more elements
     */
    @Override
    public StorageItem next()
    {
        final StorageItem next = cache.next();
        current = next.getURI();

        return next;
    }

    boolean makeBabies() throws Exception
    {
        final RegistryClient registryClient = new RegistryClient();
        final VOSpaceClient voSpaceClient =
                new VOSpaceClient(registryClient.getServiceURL(
                        URI.create("ivo://cadc.nrc.ca/vospace"),
                        "http").toExternalForm(), false);

        final Subject subject = AuthenticationUtil.getCurrentSubject();
        final String query = "limit=" + pageSize
                             + ((current == null)
                                 ? "" : "&uri="
                                        + NetUtil.encode(current.toString()));

        final List<StorageItem> items =
                (Subject.doAs(subject, (PrivilegedExceptionAction<List<StorageItem>>) () -> {
                    final ContainerNode containerNode =
                            (ContainerNode) voSpaceClient.getNode(folderURI.getPath(),
                                                                  query);
                    final List<Node> children = containerNode.getNodes();
                    final List<StorageItem> childItems = new ArrayList<>();

                    for (final Node node : children)
                    {
                        final StorageItem nextItem;
                        final VOSURI nodeURI = node.getUri();
                        final long sizeInBytes =
                                Long.parseLong(node.getPropertyValue(
                                        VOS.PROPERTY_URI_CONTENTLENGTH));
                        final Date lastModifiedDate = DateUtil
                                .getDateFormat(DateUtil.IVOA_DATE_FORMAT, DateUtil.UTC)
                                .parse(node.getPropertyValue(VOS.PROPERTY_URI_DATE));
                        final boolean publicFlag =
                                Boolean.parseBoolean(
                                        node.getPropertyValue(VOS.PROPERTY_URI_ISPUBLIC));
                        final String lockedFlagValue =
                                node.getPropertyValue(VOS.PROPERTY_URI_ISLOCKED);
                        final boolean lockedFlag = StringUtil.hasText(lockedFlagValue)
                                                   && Boolean.parseBoolean(lockedFlagValue);

                        final String writeGroupValue =
                                node.getPropertyValue(VOS.PROPERTY_URI_GROUPWRITE);
                        final URI[] writeGroupURIs = extractURIs(writeGroupValue);

                        final String readGroupValue =
                                node.getPropertyValue(VOS.PROPERTY_URI_GROUPREAD);
                        final URI[] readGroupURIs = extractURIs(readGroupValue);
                        final String owner =
                                node.getPropertyValue(VOS.PROPERTY_URI_CREATOR);

                        if (node instanceof ContainerNode)
                        {
                            nextItem = new FolderItem(nodeURI, sizeInBytes,
                                                      lastModifiedDate, publicFlag,
                                                      lockedFlag, writeGroupURIs,
                                                      readGroupURIs, owner, null);
                        }
                        else
                        {
                            nextItem = new FileItem(nodeURI, sizeInBytes,
                                                    lastModifiedDate, publicFlag,
                                                    lockedFlag, writeGroupURIs,
                                                    readGroupURIs, owner);
                        }
                        childItems.add(nextItem);
                    }

                    return childItems;
                }));

        final int size = items.size();
        tally += size;
        pageCount++;

        final boolean dataEndFlag = items.isEmpty()
                                    || ((size == 1)
                                        && items.get(0).getURI().equals(current));
        cache = items.iterator();

        System.out.println("Tally: " + tally + " (" + pageCount + " pages.)");

        return !dataEndFlag;
    }

    URI[] extractURIs(final String groupPropertyValue)
    {
        final URI[] uris;

        if (StringUtil.hasText(groupPropertyValue))
        {
            final String[] uriValues = groupPropertyValue.split(" ");
            final int len = uriValues.length;
            uris = new URI[len];

            for (int i = 0; i < len; i++)
            {
                uris[i] = URI.create(uriValues[i]);
            }
        }
        else
        {
            uris = new URI[0];
        }

        return uris;
    }
}
