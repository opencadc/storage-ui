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

package ca.nrc.cadc.beacon.web.application;

import ca.nrc.cadc.auth.HttpPrincipal;
import ca.nrc.cadc.beacon.web.StorageItemFactory;
import ca.nrc.cadc.beacon.web.URIExtractor;
import ca.nrc.cadc.beacon.web.view.FolderItem;
import ca.nrc.cadc.beacon.web.view.FullStorageItemIterator;
import ca.nrc.cadc.beacon.web.view.StorageItem;
import ca.nrc.cadc.beacon.web.view.StorageItemIterator;
import ca.nrc.cadc.reg.client.RegistryClient;
import ca.nrc.cadc.vos.*;
import ca.nrc.cadc.vos.client.VOSpaceClient;
import freemarker.template.Configuration;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.ext.freemarker.TemplateRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;

import javax.security.auth.Subject;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URI;
import java.security.PrivilegedAction;
import java.util.*;


public class MainPageServerResource extends NodeServerResource
{
    private static final URIExtractor URI_EXTRACTOR = new URIExtractor();

    @Get
    public Representation represent() throws Exception
    {
        final Configuration configuration =
                new Configuration(Configuration.getVersion());
        final StorageItemFactory storageItemFactory =
                new StorageItemFactory(URI_EXTRACTOR);

        configuration.setDirectoryForTemplateLoading(new File("src/html"));

        final boolean isRaw =
                getRequest().getResourceRef().getPath().contains("raw");

        final VOSURI folderURI = getCurrentItemURI();
        final ContainerNode containerNode =
                getContainerNode(folderURI, isRaw ? 1 : 19);
        final List<Node> childNodes = containerNode.getNodes();
        final VOSURI startNextPageURI =
                childNodes.isEmpty() ? null :
                childNodes.get(childNodes.size() - 1).getUri();
        final Iterator<StorageItem> childIterator =
                isRaw ? new FullStorageItemIterator(storageItemFactory,
                                                    folderURI, 400)
                      : new StorageItemIterator(childNodes.iterator(),
                                                storageItemFactory);

        final FolderItem folderItem =
                new FolderItem(folderURI, randomSize(), randomDate(), true,
                               false,
                               URI_EXTRACTOR.extract(
                                       VOS.PROPERTY_URI_GROUPWRITE),
                               URI_EXTRACTOR.extract(
                                       VOS.PROPERTY_URI_GROUPREAD),
                               childIterator);

        final Map<String, Object> dataModel = new HashMap<>();

        dataModel.put("folder", folderItem);
        if (startNextPageURI != null)
        {
            dataModel.put("startURI", startNextPageURI);
        }

        final Set<HttpPrincipal> httpPrincipals =
                getCurrent().getPrincipals(HttpPrincipal.class);

        if (!httpPrincipals.isEmpty())
        {
            dataModel.put("username", httpPrincipals.toArray(
                    new HttpPrincipal[httpPrincipals.size()])[0].getName());
        }

        return new TemplateRepresentation(isRaw ? "raw.ftl" : "index.ftl",
                                          configuration, dataModel,
                                          MediaType.TEXT_HTML);
    }

    ContainerNode getContainerNode(final VOSURI folderURI,
                                   final int pageSize) throws Exception
    {
        final Subject subject = getCurrent();
        final String query = "limit=" + pageSize;
        final RegistryClient registryClient = new RegistryClient();
        if (subject.getPrincipals().isEmpty())
        {
            return getContainerNode(registryClient, folderURI, query, "http");
        }
        else
        {
            return Subject.doAs(subject, new PrivilegedAction<ContainerNode>()
            {
                @Override
                public ContainerNode run()
                {
                    try
                    {
                        return getContainerNode(registryClient, folderURI,
                                                query, "http");
                    }
                    catch (NodeNotFoundException e)
                    {
                        getResponse().setStatus(Status.CLIENT_ERROR_NOT_FOUND);
                        return null;
                    }
                    catch (MalformedURLException e)
                    {
                        getResponse().setStatus(Status.SERVER_ERROR_INTERNAL);
                        return null;
                    }
                }
            });
        }
    }

    final ContainerNode getContainerNode(final RegistryClient registryClient,
                                         final VOSURI folderURI,
                                         final String query,
                                         final String protocol)
            throws NodeNotFoundException, MalformedURLException
    {
        final VOSpaceClient voSpaceClient =
                new VOSpaceClient(registryClient.getServiceURL(
                        URI.create("ivo://cadc.nrc.ca/vospace"), protocol).
                        toExternalForm(), false);
        return (ContainerNode) voSpaceClient.getNode(
                folderURI.getPath(), query);
    }

    static long randomSize()
    {
        final int min = 1024 * 3;

        return (long) Math.abs(new Random().nextInt((Integer.MAX_VALUE - min)
                                                    + 1) + min);
    }

    static Date randomDate()
    {
        final Calendar cal = Calendar.getInstance();
        final Random random = new Random();
        final int randomYear = Math.abs(random.nextInt((2016 - 1998) + 1)
                                        + 1998);
        final int randomMonth = Math.abs(random.nextInt(12));
        final int randomDay = Math.abs(random.nextInt(29));

        cal.set(Calendar.YEAR, randomYear);
        cal.set(Calendar.MONTH, randomMonth);
        cal.set(Calendar.DAY_OF_MONTH, randomDay);

        return cal.getTime();
    }
}
