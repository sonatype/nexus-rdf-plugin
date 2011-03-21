package org.sonatype.nexus.plugin.rdf;

import java.util.Date;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.slf4j.Logger;
import org.sonatype.nexus.artifact.NexusItemInfo;
import org.sonatype.nexus.feeds.FeedRecorder;
import org.sonatype.nexus.feeds.NexusArtifactEvent;
import org.sonatype.nexus.plugin.rdf.internal.SoftwarePoliceFeedSource;
import org.sonatype.nexus.proxy.maven.MavenRepository;
import org.sonatype.sisu.rdf.maven.MAVEN;
import org.sonatype.sisu.rdf.maven.MavenToRDF;
import org.sonatype.sisu.rdf.query.Parameter;
import org.sonatype.sisu.rdf.query.QueryDiff;
import org.sonatype.sisu.rdf.query.QueryResultBindingSet;
import org.sonatype.sisu.rdf.query.QueryResultDiff;
import org.sonatype.sisu.rdf.query.helper.QueryFile;

@Named
@Singleton
public class SoftwarePolice
{

    @Inject
    Logger logger;

    private final QueryDiff queryDiff;

    private org.openrdf.repository.Repository federatedRepository;

    private final FeedRecorder feedRecorder;

    private final MavenToRDF mavenToRDF;

    @Inject
    public SoftwarePolice( FeedRecorder feedRecorder, QueryDiff queryDiff, MavenToRDF mavenToRDF )
    {
        this.feedRecorder = feedRecorder;
        this.queryDiff = queryDiff;
        this.mavenToRDF = mavenToRDF;
    }

    public void check( final MavenRepository repository, final String vulnerabilitiesSPARQLEndpoints )
    {
        logger.debug( String.format(
            "About to check vulnerabilities from repository [%s] against SPARQL endpoints [%s]",
            repository.getId(), vulnerabilitiesSPARQLEndpoints ) );

        if ( vulnerabilitiesSPARQLEndpoints == null )
        {
            logger.warn( String.format(
                "Cannot check vulnerabilities from repository [%s] against SPARQL endpoints as there is no endpoint to check against",
                repository.getId() ) );
        }

        QueryFile queryFile = QueryFile.fromClasspath( "queries/vulnerabilities.sparql" );

        for ( String vulnerabilitiesSPARQLEndpoint : vulnerabilitiesSPARQLEndpoints.split( "," ) )
        {
            QueryResultDiff diff =
                queryDiff.diffPrevious( federatedRepository, queryFile.query(), queryFile.queryLanguage(),
                    Parameter.parameter( "nexusSPARQLEndpoint", null ),
                    Parameter.parameter( "vulnerabilitiesSPARQLEndpoint", vulnerabilitiesSPARQLEndpoint ) );
            if ( diff != null )
            {
                for ( QueryResultBindingSet bindingSet : diff.added() )
                {
                    String message = "New vulnerability <%2$s> found for artifact %1$s due to dependency %3$s";
                    record( bindingSet, message, repository );

                }
                for ( QueryResultBindingSet bindingSet : diff.removed() )
                {
                    String message = "Artifact %1$s does not longer has vulnerability <%2$s>";
                    record( bindingSet, message, repository );

                }
            }
        }

    }

    private void record( QueryResultBindingSet bindingSet, String message, MavenRepository repository )
    {
        String projectVersion = bindingSet.get( "projectVersion" ).value().replace( MAVEN.URI_NAMESPACE, "" );
        String vulnerability = bindingSet.get( "vulnerability" ).value();
        String dependency = bindingSet.get( "dependencyProjectVersion" ).value().replace( MAVEN.URI_NAMESPACE, "" );

        NexusItemInfo ai = new NexusItemInfo();
        ai.setPath( repository.getGavCalculator().gavToPath( mavenToRDF.gavOfProjectVersion( projectVersion ) ) );
        ai.setRepositoryId( repository.getId() );

        NexusArtifactEvent nae =
            new NexusArtifactEvent( new Date(), SoftwarePoliceFeedSource.ACTION, String.format( message,
                projectVersion, vulnerability, dependency ), ai );

        feedRecorder.addNexusArtifactEvent( nae );
    }
}