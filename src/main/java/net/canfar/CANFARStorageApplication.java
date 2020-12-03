/*
 ************************************************************************
 *******************  CANADIAN ASTRONOMY DATA CENTRE  *******************
 **************  CENTRE CANADIEN DE DONNÉES ASTRONOMIQUES  **************
 *
 *  (c) 2017.                            (c) 2017.
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

package net.canfar;

import ca.nrc.cadc.beacon.web.restlet.StorageApplication;
import ca.nrc.cadc.beacon.web.view.FreeMarkerConfiguration;
import ca.nrc.cadc.net.ResourceNotFoundException;
import ca.nrc.cadc.reg.client.RegistryClient;
import ca.nrc.cadc.util.StringUtil;
import freemarker.template.TemplateModelException;
import org.restlet.Context;

import java.io.IOException;
import java.net.URI;
import java.net.URL;


public class CANFARStorageApplication extends StorageApplication {

    private static final String DEFAULT_APP_REGISTRY_URL = "https://www.canfar.net/reg/applications";
    private static final URI DEFAULT_APP_ROOT_REGISTRY_KEY = URI.create("ivo://cadc.nrc.ca/canfar-dashboard");


    public CANFARStorageApplication(final Context context) {
        super(context);
    }

    @Override
    public FreeMarkerConfiguration createFreemarkerConfig() {
        final FreeMarkerConfiguration freeMarkerConfiguration = super.createFreemarkerConfig();

        try {
            final URL webHost = getWebHost();

            freeMarkerConfiguration.setSharedVariable("canfarWebHost", webHost.toString());
            freeMarkerConfiguration.addTemplateLoader(new CANFARURLTemplateLoader(webHost));
        } catch (IOException | ResourceNotFoundException | TemplateModelException e) {
            throw new IllegalArgumentException(e);
        }

        return freeMarkerConfiguration;
    }

    private URL getWebHost() throws IOException, ResourceNotFoundException {
        final RegistryClient registryClient = new RegistryClient(getAppRegistryURL());
        return registryClient.getAccessURL(DEFAULT_APP_ROOT_REGISTRY_KEY);
    }

    private URL getAppRegistryURL() throws IOException {
        final String envAppRegistryURL = System.getenv("APP_REGISTRY_URL");
        return StringUtil.hasLength(envAppRegistryURL) ? new URL(envAppRegistryURL) : new URL(DEFAULT_APP_REGISTRY_URL);
    }
}
