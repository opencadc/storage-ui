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


import ca.nrc.cadc.dlm.DownloadDescriptor;
import ca.nrc.cadc.dlm.client.ManifestReader;
import ca.nrc.cadc.net.HttpDownload;
import ca.nrc.cadc.net.InputStreamWrapper;
import ca.nrc.cadc.reg.client.RegistryClient;
import ca.nrc.cadc.vos.client.VOSpaceClient;
import org.json.JSONArray;
import org.json.JSONObject;
import org.restlet.data.MediaType;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.OutputRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.WriterRepresentation;
import org.restlet.resource.Get;
import org.restlet.resource.Post;

import javax.security.auth.Subject;
import java.io.*;
import java.net.URI;
import java.security.PrivilegedExceptionAction;
import java.util.Iterator;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


public class BatchDownloadResource extends StorageItemServerResource
{
    /**
     * Represent the various download methods.
     */
    enum DownloadMethod
    {
        URL_LIST("URL List"), ZIP_FILE("ZIP File");

        private final String requestPropertyValue;

        DownloadMethod(String requestPropertyValue)
        {
            this.requestPropertyValue = requestPropertyValue;
        }


        static DownloadMethod valueFromRequest(
                final String requestPropertyValue)
        {
            for (final DownloadMethod downloadMethod : values())
            {
                if (downloadMethod.requestPropertyValue.equals(
                        requestPropertyValue))
                {
                    return downloadMethod;
                }
            }

            throw new IllegalArgumentException("No such Download Method: "
                                               + requestPropertyValue);
        }
    }


    /**
     * Empty constructor needed for Restlet to manage it.  Needs to be public.
     */
    public BatchDownloadResource()
    {
    }

    /**
     * Complete constructor for testing.
     *
     * @param registryClient The Registry client to use.
     * @param voSpaceClient  The VOSpace Client to use.
     */
    BatchDownloadResource(final RegistryClient registryClient,
                          final VOSpaceClient voSpaceClient)
    {
        super(registryClient, voSpaceClient);
    }


    @Get
    public Representation represent() throws Exception
    {
        final String downloadMethodValue = getQueryValue("method");
        final String[] uriValues = getQuery().getValuesArray("uris");

        final URI[] uris = new URI[uriValues.length];

        for (int i = 0; i < uriValues.length; i++)
        {
            uris[i] = URI.create(uriValues[i]);
        }

        final DownloadMethod requestedDownloadMethod =
                DownloadMethod.valueFromRequest(downloadMethodValue);

        return handleDownload(requestedDownloadMethod, uris);
    }


    @Post("json")
    public Representation process(final JsonRepresentation payload)
            throws Exception
    {
        final JSONObject jsonObject = payload.getJsonObject();
        final String downloadMethodValue = jsonObject.getString("method");
        final JSONArray uriValues = jsonObject.getJSONArray("uris");
        final URI[] uris = new URI[uriValues.length()];

        for (int i = 0; i < uriValues.length(); i++)
        {
            uris[i] = URI.create(uriValues.getString(i));
        }

        final DownloadMethod requestedDownloadMethod =
                DownloadMethod.valueFromRequest(downloadMethodValue);

        return handleDownload(requestedDownloadMethod, uris);
    }

    Representation handleDownload(final DownloadMethod downloadMethod,
                                  final URI[] uris) throws Exception
    {
        final Subject currentUser = getCurrentUser();
        final Representation representation;
        final Writer manifestStringWriter = new StringWriter();
        for (final URI uri : uris)
        {
            voSpaceClient.getManifest(uri.getPath(), manifestStringWriter);
        }

        switch (downloadMethod)
        {
            case ZIP_FILE:
            {
                representation = new OutputRepresentation(
                        MediaType.APPLICATION_ZIP)
                {
                    @Override
                    public void write(final OutputStream outputStream)
                            throws IOException
                    {
                        final ZipOutputStream zos =
                                new ZipOutputStream(outputStream);

                        final ManifestReader manifestReader =
                                new ManifestReader();
                        for (final Iterator<DownloadDescriptor> downloadDescriptorIterator =
                             manifestReader.read(
                                     manifestStringWriter.toString());
                             downloadDescriptorIterator.hasNext();)
                        {
                            final DownloadDescriptor downloadDescriptor =
                                    downloadDescriptorIterator.next();
                            if (downloadDescriptor.url != null)
                            {
                                final InputStreamWrapper inputStreamWrapper =
                                        inputStream ->
                                        {
                                            int length;

                                            // create byte buffer
                                            byte[] buffer = new byte[1024];

                                            // Begin writing a new ZIP entry, positions
                                            // the stream to the start of the entry
                                            // data.
                                            zos.putNextEntry(new ZipEntry(
                                                    downloadDescriptor.destination));

                                            while ((length =
                                                    inputStream.read(buffer)) > 0)
                                            {
                                                zos.write(buffer, 0, length);
                                            }

                                            zos.closeEntry();

                                            inputStream.close();
                                        };

                                final HttpDownload httpDownload =
                                        new HttpDownload(downloadDescriptor.url,
                                                         inputStreamWrapper);

                                httpDownload.setFollowRedirects(true);

                                //
                                // Because this anonymous execution is taken
                                // out of context, we need to re-submit the
                                // current user with the GET request.
                                //
                                try
                                {
                                    Subject.doAs(currentUser,
                                                 new PrivilegedExceptionAction<Object>()
                                                 {
                                                     @Override
                                                     public Object run()
                                                             throws Exception
                                                     {
                                                         httpDownload.run();
                                                         return null;
                                                     }
                                                 });
                                }
                                catch (Exception e)
                                {
                                    throw new IOException(e);
                                }
                            }
                        }

                        // close the ZipOutputStream
                        zos.close();
                    }

                };

                break;
            }

            case URL_LIST:
            {
                representation = new WriterRepresentation(
                        MediaType.TEXT_URI_LIST)
                {
                    @Override
                    public void write(final Writer writer) throws IOException
                    {
                        writer.write(manifestStringWriter.toString());
                    }
                };

                break;
            }

            default:
            {
                throw new UnsupportedOperationException("Unsupported download: "
                                                        + downloadMethod);
            }
        }

        return representation;
    }
}
