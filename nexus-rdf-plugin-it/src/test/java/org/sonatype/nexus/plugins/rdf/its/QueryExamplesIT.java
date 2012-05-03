/**
 * Copyright (c) 2008-2011 Sonatype, Inc.
 * All rights reserved. Includes the third-party code listed at http://www.sonatype.com/products/nexus/attributions.
 *
 * This program is free software: you can redistribute it and/or modify it only under the terms of the GNU Affero General
 * Public License Version 3 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License Version 3
 * for more details.
 *
 * You should have received a copy of the GNU Affero General Public License Version 3 along with this program.  If not, see
 * http://www.gnu.org/licenses.
 *
 * Sonatype Nexus (TM) Open Source Version is available from Sonatype, Inc. Sonatype and Sonatype Nexus are trademarks of
 * Sonatype, Inc. Apache Maven is a trademark of the Apache Foundation. M2Eclipse is a trademark of the Eclipse Foundation.
 * All other trademarks are the property of their respective owners.
 */
package org.sonatype.nexus.plugins.rdf.its;

import static org.hamcrest.MatcherAssert.assertThat;

import java.io.IOException;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.sonatype.sisu.rdf.query.QueryResult;
import org.sonatype.sisu.rdf.query.QueryResultBindingSet;
import com.google.common.base.Throwables;

public class QueryExamplesIT
    extends RDFITSupport
{

    static final String NO_CLASSIFIER = null;

    public QueryExamplesIT()
    {
        super( "query-examples" );
    }

    @Before
    @Override
    public void setUp()
    {
        super.setUp();

        try
        {
            repositoriesNRC().createMavenHostedReleaseRepository( getTestRepositoryId() );
        }
        catch ( IOException e )
        {
            throw Throwables.propagate( e );
        }
    }

    /**
     * Find all artifacts that have a category "foo".
     *
     * @throws Exception unexpected
     */
    @Test
    public void example01()
        throws Exception
    {
        createRDFCapability();
        createSPARQLEndpointCapability();

        deployPom( "p1" );
        deployPom( "p2v1" );
        deployPom( "p2v2" );

        final QueryResult queryResult = executeQuery( resolveTestFile( "example01.sparql" ) );
        assertThat( "Number of results", queryResult, Matchers.<QueryResultBindingSet>iterableWithSize( 1 ) );
    }

    /**
     * Find all artifacts with groupI "org.sonatype.rdf.test" and artifactId "p2" with a category "foo" and
     * version >= 1 and < 4
     *
     * @throws Exception unexpected
     */
    @Test
    public void example02()
        throws Exception
    {
        createRDFCapability();
        createSPARQLEndpointCapability();

        deployPom( "p1" );
        deployPom( "p2v1" );
        deployPom( "p2v2" );
        deployPom( "p2v3" );
        deployPom( "p2v4" );

        final QueryResult queryResult = executeQuery( resolveTestFile( "example02.sparql" ) );
        assertThat( "Number of results", queryResult, Matchers.<QueryResultBindingSet>iterableWithSize( 2 ) );
    }

    /**
     * All projects with a category "foo" that depends on component "c1" with version >= 2 and <= 3, inclusive
     * transitive dependencies
     *
     * @throws Exception unexpected
     */
    @Test
    public void example03()
        throws Exception
    {
        createRDFCapability();
        createSPARQLEndpointCapability();

        deployPom( "c1v1" );
        deployPom( "c1v2" );
        deployPom( "c1v3" );
        deployPom( "c1v4" );
        deployPom( "c2v1" );
        deployPom( "p1v1" );
        deployPom( "p1v2" );
        deployPom( "p1v3" );
        deployPom( "p1v4" );
        deployPom( "p1v5" );
        deployPom( "p1v6" );
        deployPom( "p1v7" );

        //executeQuery( resolveTestFile( "queries/all-statements.sparql" ) );
        final QueryResult queryResult = executeQuery( resolveTestFile( "example03.sparql" ) );
        assertThat( "Number of results", queryResult, Matchers.<QueryResultBindingSet>iterableWithSize( 4 ) );
    }

}
