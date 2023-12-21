
/*
 ************************************************************************
 *******************  CANADIAN ASTRONOMY DATA CENTRE  *******************
 **************  CENTRE CANADIEN DE DONNÉES ASTRONOMIQUES  **************
 *
 *  (c) 2019.                            (c) 2019.
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

package net.canfar.storage.web;

import net.canfar.storage.PathUtils;
import net.canfar.storage.web.config.VOSpaceServiceConfig;
import org.opencadc.gms.GroupURI;
import org.opencadc.vospace.NodeProperty;
import net.canfar.storage.AbstractUnitTest;
import net.canfar.storage.web.view.StorageItem;
import org.opencadc.vospace.DataNode;
import org.opencadc.vospace.VOS;
import org.opencadc.vospace.VOSURI;

import org.junit.Test;
import org.junit.Assert;

import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;


public class StorageItemFactoryTest extends AbstractUnitTest<StorageItemFactory> {
    @Test
    public void translate() {
        final VOSURI testDataNodeURI = new VOSURI(URI.create("vos://cadc.nrc.ca~vault/myroot/path/file.txt"));
        final DataNode testDataNode = new DataNode(testDataNodeURI.getName());
        PathUtils.augmentParents(Paths.get(testDataNodeURI.getPath()), testDataNode);

        final String contextPath = "/warehouse";
        final Set<GroupURI> writeGroupURIs =
                Collections.singleton(new GroupURI(URI.create("ivo://cadc.nrc.ca/gms/mygroups?GROUP1")));
        final Set<GroupURI> readGroupURIs = new HashSet<>();
        readGroupURIs.add(new GroupURI(URI.create("ivo://cadc.nrc.ca/gms/mygroups?GROUP2")));
        readGroupURIs.add(new GroupURI(URI.create("ivo://cadc.nrc.ca/gms/mygroups?GROUP3")));

        testDataNode.getReadOnlyGroup().addAll(readGroupURIs);
        testDataNode.getReadWriteGroup().addAll(writeGroupURIs);
        testDataNode.getProperties().add(new NodeProperty(VOS.PROPERTY_URI_CONTENTLENGTH, "88000"));
        testDataNode.getProperties().add(new NodeProperty(VOS.PROPERTY_URI_READABLE, Boolean.TRUE.toString()));

        final VOSpaceServiceConfig testServiceConfig =
                new VOSpaceServiceConfig("vault", URI.create("ivo://example.org/vault"),
                                         URI.create("vos://example.org~vault"), new VOSpaceServiceConfig.Features());

        testSubject = new StorageItemFactory(contextPath, testServiceConfig);

        final StorageItem storageItemResult = testSubject.translate(testDataNode);
        Assert.assertEquals("Wrong target URL.",
                            "/warehouse/vault/file/myroot/path/file.txt",
                            storageItemResult.getTargetPath());

        final String readGroupNames = "GROUP2 GROUP3";
        Assert.assertEquals("Wrong Read groups.", readGroupNames, storageItemResult.getReadGroupNames());

        final String writeGroupNames = "GROUP1";
        Assert.assertEquals("Wrong Write groups.", writeGroupNames, storageItemResult.getWriteGroupNames());

        Assert.assertEquals("Wrong size in bytes.", 88000L, storageItemResult.getSizeInBytes());
        Assert.assertEquals("Wrong size in human readable.", "85.94 KB",
                            storageItemResult.getSizeHumanReadable());
    }

    @Test
    public void translateTargetFallback() {
        final VOSURI testDataNodeURI = new VOSURI(URI.create("vos://cadc.nrc.ca~vault/myroot/path/file.txt"));
        final DataNode testDataNode = new DataNode(testDataNodeURI.getName());
        PathUtils.augmentParents(Paths.get(testDataNodeURI.getPath()), testDataNode);

        final String contextPath = "/warehouse";
        final Set<GroupURI> writeGroupURIs =
                Collections.singleton(new GroupURI(URI.create("ivo://cadc.nrc.ca/gms/mygroups?GROUP1")));
        final Set<GroupURI> readGroupURIs = new HashSet<>();
        readGroupURIs.add(new GroupURI(URI.create("ivo://cadc.nrc.ca/gms/mygroups?GROUP2")));
        readGroupURIs.add(new GroupURI(URI.create("ivo://cadc.nrc.ca/gms/mygroups?GROUP3")));

        testDataNode.getReadOnlyGroup().addAll(readGroupURIs);
        testDataNode.getReadWriteGroup().addAll(writeGroupURIs);
        testDataNode.getProperties().add(new NodeProperty(VOS.PROPERTY_URI_CONTENTLENGTH, "88000"));
        testDataNode.getProperties().add(new NodeProperty(VOS.PROPERTY_URI_READABLE, Boolean.TRUE.toString()));

        final VOSpaceServiceConfig testServiceConfig =
                new VOSpaceServiceConfig("vault", URI.create("ivo://example.org/vault"),
                                         URI.create("vos://example.org~vault"), new VOSpaceServiceConfig.Features());

        testSubject = new StorageItemFactory(contextPath, testServiceConfig);

        final StorageItem storageItemResult = testSubject.translate(testDataNode);
        Assert.assertEquals("Wrong target URL.",
                            "/warehouse/vault/file/myroot/path/file.txt",
                            storageItemResult.getTargetPath());

        final String readGroupNames = "GROUP2 GROUP3";
        Assert.assertEquals("Wrong Read groups.", readGroupNames, storageItemResult.getReadGroupNames());

        final String writeGroupNames = "GROUP1";
        Assert.assertEquals("Wrong Write groups.", writeGroupNames, storageItemResult.getWriteGroupNames());

        Assert.assertEquals("Wrong size in bytes.", 88000L, storageItemResult.getSizeInBytes());
        Assert.assertEquals("Wrong size in human readable.", "85.94 KB",
                            storageItemResult.getSizeHumanReadable());
    }

    @Test
    public void translateNoGroups() {
        final VOSURI testDataNodeURI = new VOSURI(URI.create("vos://cadc.nrc.ca~vault/myroot/path/file.txt"));
        final DataNode testDataNode = new DataNode(testDataNodeURI.getName());
        PathUtils.augmentParents(Paths.get(testDataNodeURI.getPath()), testDataNode);

        final String contextPath = "/warehouse";

        testDataNode.getProperties().add(new NodeProperty(VOS.PROPERTY_URI_CONTENTLENGTH, "88000"));
        testDataNode.getProperties().add(new NodeProperty(VOS.PROPERTY_URI_READABLE, Boolean.TRUE.toString()));

        final VOSpaceServiceConfig testServiceConfig =
                new VOSpaceServiceConfig("vault", URI.create("ivo://example.org/vault"),
                                         URI.create("vos://example.org~vault"), new VOSpaceServiceConfig.Features());

        testSubject = new StorageItemFactory(contextPath, testServiceConfig);

        final StorageItem storageItemResult = testSubject.translate(testDataNode);
        Assert.assertEquals("Wrong target URL.",
                            "/warehouse/vault/file/myroot/path/file.txt",
                            storageItemResult.getTargetPath());

        Assert.assertEquals("Wrong Read groups.", "", storageItemResult.getReadGroupNames());
        Assert.assertEquals("Wrong Write groups.", "", storageItemResult.getWriteGroupNames());

        Assert.assertEquals("Wrong size in bytes.", 88000L, storageItemResult.getSizeInBytes());
        Assert.assertEquals("Wrong size in human readable.", "85.94 KB",
                            storageItemResult.getSizeHumanReadable());
    }
}
