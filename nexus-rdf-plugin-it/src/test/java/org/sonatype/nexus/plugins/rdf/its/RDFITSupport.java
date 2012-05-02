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

import static java.util.Arrays.asList;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import javax.inject.Inject;
import javax.inject.Named;

import org.junit.Before;
import org.openrdf.repository.sparql.SPARQLRepository;
import org.sonatype.inject.BeanScanning;
import org.sonatype.nexus.bundle.launcher.NexusBundleConfiguration;
import org.sonatype.nexus.bundle.launcher.NexusRunningITSupport;
import org.sonatype.nexus.integrationtests.NexusRestClient;
import org.sonatype.nexus.integrationtests.TestContext;
import org.sonatype.nexus.plugin.rdf.internal.capabilities.RDFCapabilityDescriptor;
import org.sonatype.nexus.plugin.rdf.internal.capabilities.SPARQLEndpointCapabilityDescriptor;
import org.sonatype.nexus.plugins.capabilities.internal.rest.dto.CapabilityPropertyResource;
import org.sonatype.nexus.plugins.capabilities.internal.rest.dto.CapabilityResource;
import org.sonatype.nexus.plugins.capabilities.test.CapabilitiesNexusRestClient;
import org.sonatype.nexus.test.utils.DeployUtils;
import org.sonatype.nexus.test.utils.EventInspectorsUtil;
import org.sonatype.nexus.test.utils.GavUtil;
import org.sonatype.nexus.test.utils.RepositoriesNexusRestClient;
import org.sonatype.nexus.test.utils.TasksNexusRestClient;
import org.sonatype.sisu.rdf.query.QueryResult;
import org.sonatype.sisu.rdf.query.QueryResultBinding;
import org.sonatype.sisu.rdf.query.QueryResultBindingSet;
import org.sonatype.sisu.rdf.query.QueryResultFactory;
import org.sonatype.sisu.rdf.query.QueryRunner;
import org.sonatype.sisu.rdf.query.helper.ExtractingQueryResultsProcessor;
import org.sonatype.sisu.rdf.query.helper.PrintingQueryResultsProcessor;
import org.sonatype.sisu.rdf.query.helper.QueryFile;

public class RDFITSupport
    extends NexusRunningITSupport
{

    protected static final String NO_CLASSIFIER = null;

    @Inject
    @Named( "${NexusITSupport.nexus-rdf-plugin-coordinates}" )
    private String rdfPluginCoordinates;

    @Inject
    @Named( "${NexusITSupport.nexus-capabilities-plugin-coordinates}" )
    private String capabilitiesPluginCoordinates;

    @Inject
    @Named( "${NexusITSupport.nexus-maven-bridge-plugin-coordinates}" )
    private String mavenBridgePluginCoordinates;

    @Inject
    private QueryRunner queryRunner;

    @Inject
    private QueryResultFactory queryResultFactory;

    private final String testRepositoryId;

    private CapabilitiesNexusRestClient capabilitiesNRC;

    private TasksNexusRestClient tasksNRC;

    private RepositoriesNexusRestClient repositoriesNRC;

    private DeployUtils deployNRC;

    private NexusRestClient nexusRestClient;

    protected RDFITSupport( final String testRepositoryId )
    {
        this.testRepositoryId = testRepositoryId;
    }

    protected String getTestRepositoryId()
    {
        return testRepositoryId;
    }

    @Override
    public BeanScanning scanning()
    {
        return BeanScanning.INDEX;
    }

    @Override
    protected NexusBundleConfiguration configureNexus( final NexusBundleConfiguration configuration )
    {
        return configuration.addPlugins(
            resolveArtifact( mavenBridgePluginCoordinates ),
            resolveArtifact( capabilitiesPluginCoordinates ),
            resolveArtifact( rdfPluginCoordinates )
        );
    }

    @Before
    @Override
    public void setUp()
    {
        super.setUp();
        nexusRestClient = new NexusRestClient(
            new TestContext()
                .setNexusUrl( nexus().getUrl().toExternalForm() )
                .setSecureTest( true )
        );
        capabilitiesNRC = new CapabilitiesNexusRestClient( nexusRestClient );
        tasksNRC = new TasksNexusRestClient( nexusRestClient );
        final EventInspectorsUtil events = new EventInspectorsUtil( nexusRestClient );
        repositoriesNRC = new RepositoriesNexusRestClient( nexusRestClient, tasksNRC, events );
        deployNRC = new DeployUtils( nexusRestClient );
    }

    protected void createRDFCapability( final CapabilityPropertyResource... properties )
        throws Exception
    {
        final CapabilityPropertyResource[] cprs = new CapabilityPropertyResource[properties.length + 1];
        cprs[0] = property( RDFCapabilityDescriptor.REPOSITORY, getTestRepositoryId() );
        System.arraycopy( properties, 0, cprs, 1, properties.length );
        final CapabilityResource capability = capability(
            RDFCapabilityDescriptor.TYPE_ID, RDFITSupport.class.getName(), cprs
        );
        getCapabilitiesNRC().create( capability );
    }

    protected void createSPARQLEndpointCapability( final CapabilityPropertyResource... properties )
        throws Exception
    {
        final CapabilityPropertyResource[] cprs = new CapabilityPropertyResource[properties.length + 1];
        cprs[0] = property( SPARQLEndpointCapabilityDescriptor.REPOSITORY, getTestRepositoryId() );
        System.arraycopy( properties, 0, cprs, 1, properties.length );
        final CapabilityResource capability = capability(
            SPARQLEndpointCapabilityDescriptor.TYPE_ID, RDFITSupport.class.getName(), cprs
        );
        getCapabilitiesNRC().create( capability );
    }

    protected File downloadArtifact( String groupId, String artifact, String version, String type, String classifier )
        throws IOException
    {
        final URL url = new URL(
            nexus().getUrl().toExternalForm() + "content/repositories/" + getTestRepositoryId() + "/"
                + GavUtil.getRelitiveArtifactPath( groupId, artifact, version, type, classifier )
        );

        String classifierPart = ( classifier != null ) ? "-" + classifier : "";

        return nexusRestClient.downloadFile(
            url,
            methodSpecificDirectory( "downloads" ) + "/" + artifact + "-" + version + classifierPart + "." + type
        );
    }

    protected QueryResult executeQuery( final File file )
        throws IOException
    {
        final URL url = new URL( nexus().getUrl().toExternalForm() + "sparql/" + getTestRepositoryId() );
        final SPARQLRepository sparqlRepository = new SPARQLRepository( url.toExternalForm() );
        final QueryFile queryFile = QueryFile.fromFile( file );

        final ExtractingQueryResultsProcessor extractingQueryResultsProcessor =
            new ExtractingQueryResultsProcessor( queryResultFactory );

        queryRunner.execute(
            sparqlRepository,
            queryFile.query(),
            queryFile.queryLanguage(),
            new PrintingQueryResultsProcessor()
        );

        queryRunner.execute(
            sparqlRepository,
            queryFile.query(),
            queryFile.queryLanguage(),
            extractingQueryResultsProcessor
        );

        return extractingQueryResultsProcessor.queryResult();
    }

    protected QueryResultBinding qrb( final String name, final String value )
    {
        return queryResultFactory.createQueryResultBinding( name, value );
    }

    protected QueryResultBindingSet qrbs( final QueryResultBinding... binding )
    {
        return queryResultFactory.createQueryResultBindingSet( asList( binding ) );
    }

    protected QueryResult qr( final QueryResultBindingSet... bindingSets )
    {
        return queryResultFactory.createQueryResult( asList( bindingSets ) );
    }

    public static CapabilityResource capability( final String type,
                                                 final String notes,
                                                 final CapabilityPropertyResource... properties )
    {
        final CapabilityResource cr = new CapabilityResource();

        cr.setTypeId( type );
        cr.setNotes( notes );

        for ( final CapabilityPropertyResource cpr : properties )
        {
            cr.addProperty( cpr );
        }

        return cr;
    }

    public static CapabilityPropertyResource property( final String key, final String value )
    {
        final CapabilityPropertyResource cpr = new CapabilityPropertyResource();

        cpr.setKey( key );
        cpr.setValue( value );

        return cpr;
    }

    public CapabilitiesNexusRestClient getCapabilitiesNRC()
    {
        return capabilitiesNRC;
    }

    public DeployUtils deployNRC()
    {
        return deployNRC;
    }

    public RepositoriesNexusRestClient repositoriesNRC()
    {
        return repositoriesNRC;
    }

    protected TasksNexusRestClient tasksNRC()
    {
        return tasksNRC;
    }

    protected void deployPom( final String name )
        throws IOException
    {
        deployNRC().deployUsingPomWithRest(
            getTestRepositoryId(),
            resolveTestFile( "artifacts/" + name + ".pom" ),
            resolveTestFile( "artifacts/" + name + ".pom" ),
            NO_CLASSIFIER,
            "pom"
        );
    }

}
