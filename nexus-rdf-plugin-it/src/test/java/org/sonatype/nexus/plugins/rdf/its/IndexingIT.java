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

public class IndexingIT
    extends RDFITSupport
{

    public IndexingIT()
    {
        super( "indexing" );
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
     * TODO
     *
     * @throws Exception re-thrown
     */
    @Test
    public void simple()
        throws Exception
    {
        createRDFCapability();
        createSPARQLEndpointCapability();

        deployPom( "p1" );

        final QueryResult queryResult = executeQuery( resolveTestFile( "queries/all-statements.sparql" ) );
        assertThat( "Number of statements", queryResult, Matchers.<QueryResultBindingSet>iterableWithSize( 17 ) );
    }

    /**
     * TODO
     *
     * @throws Exception re-thrown
     */
    @Test
    public void withParent()
        throws Exception
    {
        createRDFCapability();
        createSPARQLEndpointCapability();

        deployPom( "p1" );
        deployPom( "p11" );

        final QueryResult queryResult = executeQuery( resolveTestFile( "queries/all-statements.sparql" ) );
        assertThat( "Number of statements", queryResult, Matchers.<QueryResultBindingSet>iterableWithSize( 35 ) );
    }

    /**
     * TODO
     *
     * @throws Exception re-thrown
     */
    @Test
    public void withProperties()
        throws Exception
    {
        createRDFCapability();
        createSPARQLEndpointCapability();

        deployPom( "p1" );

        final QueryResult queryResult = executeQuery( resolveTestFile( "queries/all-statements.sparql" ) );
        assertThat( "Number of statements", queryResult, Matchers.<QueryResultBindingSet>iterableWithSize( 18 ) );
    }

}
