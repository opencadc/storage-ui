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

package ca.nrc.cadc.beacon.web.restlet;

import ca.nrc.cadc.auth.AuthenticationUtil;
import ca.nrc.cadc.auth.PrincipalExtractor;
import ca.nrc.cadc.beacon.web.PrincipalExtractorImpl;
import ca.nrc.cadc.beacon.web.SubjectGenerator;
import ca.nrc.cadc.beacon.web.resources.AccessControlServerResource;
import ca.nrc.cadc.beacon.web.resources.MainPageServerResource;
import ca.nrc.cadc.beacon.web.resources.PageServerResource;
import ca.nrc.cadc.beacon.web.resources.StorageItemServerResource;
import org.restlet.*;
import org.restlet.routing.Route;
import org.restlet.routing.Router;
import org.restlet.routing.TemplateRoute;
import org.restlet.routing.Variable;

import javax.security.auth.Subject;
import javax.servlet.ServletContext;
import java.security.PrivilegedAction;
import java.util.Map;

public class VOSpaceApplication extends Application
{
    private static final String SUBJECT_GENERATOR_CLASSNAME =
            "ca.nrc.cadc.beacon.web.subjectGenerator";
    private static final String DEFAULT_SUBJECT_GENERATOR_CLASSNAME =
            "ca.nrc.cadc.beacon.web.SubjectGenerator";


    /**
     * Creates a inbound root Restlet that will receive all incoming calls. In
     * general, instances of Router, Filter or Finder classes will be used as
     * initial application Restlet. The default implementation returns null by
     * default. This method is intended to be overridden by subclasses.
     *
     * @return The inbound root Restlet.
     */
    @Override
    public Restlet createInboundRoot()
    {
        final Router router = new Router()
        {
            private final SubjectGenerator subjectGenerator =
                    createSubjectGenerator();

            /**
             * Effectively handles the call using the selected next {@link Restlet},
             * typically the selected {@link Route}. By default, it just invokes the
             * next Restlet.
             *
             * @param next     The next Restlet to invoke.
             * @param request  The request.
             * @param response The response.
             */
            @Override
            protected void doHandle(final Restlet next, final Request request,
                                    final Response response)
            {
                final PrincipalExtractor principalExtractor =
                        new PrincipalExtractorImpl(request);
                final Subject subject =
                        subjectGenerator.generate(principalExtractor);

                Subject.doAs(subject, new PrivilegedAction<Void>()
                {
                    @Override
                    public Void run()
                    {
                        next.handle(request, response);
                        return null;
                    }
                });
            }
        };

        router.attach("/ac/login", AccessControlServerResource.class);

        final TemplateRoute pageRoute =
                router.attach("/page/{path}", PageServerResource.class);
        final TemplateRoute allRoute =
                router.attach("/all/{path}", StorageItemServerResource.class);

        // Allow for an empty path to be the root.
        router.attach("/list", MainPageServerResource.class);
        final TemplateRoute listRoute =
                router.attach("/list/{path}", MainPageServerResource.class);
        final TemplateRoute rawRoute =
                router.attach("/raw/{path}", MainPageServerResource.class);

        final Map<String, Variable> pageRouteVariables =
                pageRoute.getTemplate().getVariables();
        pageRouteVariables.put("path", new Variable(Variable.TYPE_URI_PATH));

        final Map<String, Variable> allRouteVariables =
                allRoute.getTemplate().getVariables();
        allRouteVariables.put("path", new Variable(Variable.TYPE_URI_PATH));

        final Map<String, Variable> listRouteVariables =
                listRoute.getTemplate().getVariables();
        listRouteVariables.put("path", new Variable(Variable.TYPE_URI_PATH));

        final Map<String, Variable> rawRouteVariables =
                rawRoute.getTemplate().getVariables();
        rawRouteVariables.put("path", new Variable(Variable.TYPE_URI_PATH));

        router.setContext(getContext());
        return router;
    }

    /**
     * Override at will.
     * @return      SubjectGenerator implementation.
     */
    SubjectGenerator createSubjectGenerator()
    {
        return new SubjectGenerator();
    }
}
