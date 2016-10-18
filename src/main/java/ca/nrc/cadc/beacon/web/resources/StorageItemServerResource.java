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

package ca.nrc.cadc.beacon.web.resources;

import ca.nrc.cadc.beacon.web.StorageItemFactory;
import ca.nrc.cadc.beacon.web.URIExtractor;
import ca.nrc.cadc.beacon.web.restlet.VOSpaceApplication;
import ca.nrc.cadc.reg.client.RegistryClient;
import ca.nrc.cadc.util.StringUtil;
import ca.nrc.cadc.vos.*;
import ca.nrc.cadc.vos.client.VOSpaceClient;
import org.restlet.data.Status;
import org.restlet.resource.Delete;
import org.restlet.resource.ResourceException;

import java.net.MalformedURLException;
import java.net.URI;
import java.util.List;


public class StorageItemServerResource extends SecureServerResource
{
    static final String VOSPACE_NODE_URI_PREFIX = "vos://ca.nrc.cadc!vospace";


    // Page size for the initial page display.
    private static final int DEFAULT_DISPLAY_PAGE_SIZE = 35;
    private static final URIExtractor URI_EXTRACTOR = new URIExtractor();

    StorageItemFactory storageItemFactory;
    VOSpaceClient voSpaceClient;


    /**
     * Empty constructor needed for Restlet to manage it.
     */
    public StorageItemServerResource()
    {
    }

    /**
     * Complete constructor for testing.
     * @param registryClient        The Registry client to use.
     * @param voSpaceClient         The VOSpace Client to use.
     */
    StorageItemServerResource(final RegistryClient registryClient,
                              final VOSpaceClient voSpaceClient)
    {
        initialize(registryClient, voSpaceClient);
    }


    /**
     * Set-up method.  This ensures there is a context first before pulling
     * out some necessary objects for further work.
     *
     * Tester
     */
    @Override
    protected void doInit() throws ResourceException
    {
        initialize(((RegistryClient) getContext().getAttributes().get(
                VOSpaceApplication.REGISTRY_CLIENT_KEY)),
                   ((VOSpaceClient) getContext().getAttributes().get(
                           VOSpaceApplication.VOSPACE_CLIENT_KEY)));
    }

    private void initialize(final RegistryClient registryClient,
                            final VOSpaceClient voSpaceClient)
    {
        try
        {
            this.storageItemFactory =
                    new StorageItemFactory(URI_EXTRACTOR, registryClient,
                                           getServletContext()
                                                   .getContextPath());
        }
        catch (MalformedURLException e)
        {
            throw new ResourceException(Status.SERVER_ERROR_INTERNAL, e);
        }

        this.voSpaceClient = voSpaceClient;
    }


    String getCurrentPath()
    {
        final Object pathInRequest = getRequestAttributes().get("path");
        return "/" + ((pathInRequest == null) ? "" : pathInRequest.toString());
    }

    VOSURI getCurrentItemURI()
    {
        return toURI(getCurrentPath());
    }

    final ContainerNode getCurrentNode()
            throws NodeNotFoundException, MalformedURLException
    {
        return (ContainerNode) getNode(getCurrentItemURI(),
                                       DEFAULT_DISPLAY_PAGE_SIZE);
    }

    VOSURI toURI(final String path)
    {
        return new VOSURI(URI.create(VOSPACE_NODE_URI_PREFIX + path));
    }

    private Node getNode(final VOSURI folderURI, final int pageSize)
            throws MalformedURLException, NodeNotFoundException
    {
        final String query = "limit=" + pageSize + "&detail=max";

        return voSpaceClient.getNode(folderURI.getPath(), query);
    }

    void setInheritedPermissions(final VOSURI newNodeURI)
            throws NodeNotFoundException, MalformedURLException
    {
        final ContainerNode parentNode = getCurrentNode();
        final Node newNode = getNode(newNodeURI, -1);
        final List<NodeProperty> newNodeProperties = newNode.getProperties();

        // Clean slate.
        newNodeProperties.remove(
                new NodeProperty(VOS.PROPERTY_URI_GROUPREAD, ""));
        newNodeProperties.remove(
                new NodeProperty(VOS.PROPERTY_URI_GROUPWRITE, ""));
        newNodeProperties.remove(
                new NodeProperty(VOS.PROPERTY_URI_ISPUBLIC, ""));

        final String parentReadGroupURIValue =
                parentNode.getPropertyValue(
                        VOS.PROPERTY_URI_GROUPREAD);
        if (StringUtil.hasText(parentReadGroupURIValue))
        {
            newNodeProperties.add(
                    new NodeProperty(VOS.PROPERTY_URI_GROUPREAD,
                                     parentReadGroupURIValue));
        }

        final String parentWriteGroupURIValue =
                parentNode.getPropertyValue(
                        VOS.PROPERTY_URI_GROUPWRITE);
        if (StringUtil.hasText(parentWriteGroupURIValue))
        {
            newNodeProperties.add(
                    new NodeProperty(VOS.PROPERTY_URI_GROUPWRITE,
                                     parentWriteGroupURIValue));
        }

        final String isPublicValue =
                parentNode.getPropertyValue(
                        VOS.PROPERTY_URI_ISPUBLIC);
        if (StringUtil.hasText(isPublicValue))
        {
            newNodeProperties.add(
                    new NodeProperty(VOS.PROPERTY_URI_ISPUBLIC,
                                     isPublicValue));
        }

        setNodeSecure(newNode);
    }

    /**
     * Perform the HTTPS command.
     *
     * @param newNode The newly created Node.
     */
    private void setNodeSecure(final Node newNode) throws MalformedURLException
    {
        voSpaceClient.setNode(newNode);
    }

    void createNode(final Node newNode, final boolean checkForDuplicate)
    {
        voSpaceClient.createNode(newNode, checkForDuplicate);
    }

    @Delete
    public void deleteNode()
    {
        voSpaceClient.deleteNode(getCurrentPath());
    }
}
