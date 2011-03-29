package org.sonatype.nexus.plugin.rdf;

import java.util.Date;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.openrdf.repository.RepositoryException;
import org.slf4j.Logger;
import org.sonatype.nexus.artifact.NexusItemInfo;
import org.sonatype.nexus.feeds.FeedRecorder;
import org.sonatype.nexus.feeds.NexusArtifactEvent;
import org.sonatype.nexus.plugin.rdf.internal.SPARQLEndpoints;
import org.sonatype.nexus.plugin.rdf.internal.SoftwarePoliceLicenseViolationsFeedSource;
import org.sonatype.nexus.plugin.rdf.internal.SoftwarePoliceVulnerabilitiesFeedSource;
import org.sonatype.nexus.proxy.maven.MavenRepository;
import org.sonatype.sisu.rdf.maven.MAVEN;
import org.sonatype.sisu.rdf.maven.MavenToRDF;
import org.sonatype.sisu.rdf.query.Parameter;
import org.sonatype.sisu.rdf.query.QueryDiff;
import org.sonatype.sisu.rdf.query.QueryHistoryId;
import org.sonatype.sisu.rdf.query.QueryResultBindingSet;
import org.sonatype.sisu.rdf.query.QueryResultDiff;
import org.sonatype.sisu.rdf.query.helper.QueryFile;
import org.sonatype.sisu.rdf.sesame.jena.SPARQLFederationRepository;

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

    private final SPARQLEndpoints nexusSPARQLEndpoints;

    @Inject
    public SoftwarePolice( FeedRecorder feedRecorder, SPARQLEndpoints nexusSPARQLEndpoints, QueryDiff queryDiff,
                           MavenToRDF mavenToRDF )
    {
        this.feedRecorder = feedRecorder;
        this.nexusSPARQLEndpoints = nexusSPARQLEndpoints;
        this.queryDiff = queryDiff;
        this.mavenToRDF = mavenToRDF;
    }

    public void check( final MavenRepository repository, final String sparqlEndpoint )
    {
        logger.debug( String.format(
            "About to check repository [%s] against SPARQL endpoint [%s]",
            repository.getId(), sparqlEndpoint ) );

        if ( !nexusSPARQLEndpoints.isEnabledFor( repository.getId() ) )
        {
            logger.debug( String.format(
                "Cannot check repository [%s] against SPARQL endpoint as repository does not have an SPARQL capability",
                repository.getId() ) );
            return;
        }

        if ( sparqlEndpoint == null )
        {
            logger.warn( String.format(
                "Cannot check repository [%s] against SPARQL endpoint as there is no endpoint to check against",
                repository.getId() ) );
            return;
        }

        checkVulnerabilities( repository, sparqlEndpoint );
        checkLicenseViolations( repository, sparqlEndpoint );
    }

    private void checkVulnerabilities( final MavenRepository repository, final String sparqlEndpoint )
    {
        QueryFile queryFile = QueryFile.fromClasspath( "queries/vulnerabilities.sparql" );

        QueryResultDiff diff =
                queryDiff.diffPrevious(
                    QueryHistoryId.hashOf( "nexus:/vulnerabilities/" + repository.getId() ),
                    federatedRepository(),
                    queryFile.query(),
                    queryFile.queryLanguage(),
                    Parameter.parameter( "nexusSPARQLEndpoint",
                        "http://localhost:8081/nexus/sparql/" + repository.getId() ),
                    Parameter.parameter( "sparqlEndpoint", sparqlEndpoint ) );
        if ( diff != null )
        {
            for ( QueryResultBindingSet bindingSet : diff.added() )
            {
                String message =
                        "New vulnerability <a href=\"%2$s\">%2$s</a> found for artifact %1$s due to dependency %3$s";
                recordVulnerabilities( bindingSet, message, repository );

            }
            for ( QueryResultBindingSet bindingSet : diff.removed() )
            {
                String message = "Artifact %1$s does not longer has vulnerability <a href=\"%2$s\">%2$s</a>";
                recordVulnerabilities( bindingSet, message, repository );

            }
        }
    }

    private void recordVulnerabilities( QueryResultBindingSet bindingSet, String message, MavenRepository repository )
    {
        String projectVersion = bindingSet.get( "projectVersion" ).value().replace( MAVEN.URI_NAMESPACE, "" );
        String vulnerability = bindingSet.get( "vulnerability" ).value();
        String dependency = bindingSet.get( "dependencyProjectVersion" ).value().replace( MAVEN.URI_NAMESPACE, "" );

        NexusItemInfo ai = new NexusItemInfo();
        ai.setPath( repository.getGavCalculator().gavToPath( mavenToRDF.gavOfProjectVersion( projectVersion ) ) );
        ai.setRepositoryId( repository.getId() );

        NexusArtifactEvent nae =
            new NexusArtifactEvent( new Date(), SoftwarePoliceVulnerabilitiesFeedSource.ACTION, String.format( message,
                projectVersion, vulnerability, dependency ), ai );

        feedRecorder.addNexusArtifactEvent( nae );
    }

    private void checkLicenseViolations( final MavenRepository repository, final String sparqlEndpoint )
    {
        QueryFile queryFile = QueryFile.fromClasspath( "queries/licenseViolations.sparql" );

        QueryResultDiff diff =
                queryDiff.diffPrevious(
                    QueryHistoryId.hashOf( "nexus:/licenseViolations/" + repository.getId() ),
                    federatedRepository(),
                    queryFile.query(),
                    queryFile.queryLanguage(),
                    Parameter.parameter( "nexusSPARQLEndpoint",
                        "http://localhost:8081/nexus/sparql/" + repository.getId() ),
                    Parameter.parameter( "sparqlEndpoint", sparqlEndpoint ) );
        if ( diff != null )
        {
            for ( QueryResultBindingSet bindingSet : diff.added() )
            {
                String message =
                        "New license violation <a href=\"%2$s\">%2$s</a> found for artifact %1$s due to dependency %3$s";
                recordLicenseViolation( bindingSet, message, repository );

            }
            for ( QueryResultBindingSet bindingSet : diff.removed() )
            {
                String message = "Artifact %1$s does not longer has license violation <a href=\"%2$s\">%2$s</a>";
                recordLicenseViolation( bindingSet, message, repository );

            }
        }
    }

    private void recordLicenseViolation( QueryResultBindingSet bindingSet, String message, MavenRepository repository )
    {
        String projectVersion = bindingSet.get( "projectVersion" ).value().replace( MAVEN.URI_NAMESPACE, "" );
        String license = bindingSet.get( "license" ).value();
        String dependency = bindingSet.get( "dependencyProjectVersion" ).value().replace( MAVEN.URI_NAMESPACE, "" );

        NexusItemInfo ai = new NexusItemInfo();
        ai.setPath( repository.getGavCalculator().gavToPath( mavenToRDF.gavOfProjectVersion( projectVersion ) ) );
        ai.setRepositoryId( repository.getId() );

        NexusArtifactEvent nae =
            new NexusArtifactEvent( new Date(), SoftwarePoliceLicenseViolationsFeedSource.ACTION, String.format(
                message,
                projectVersion, license, dependency ), ai );

        feedRecorder.addNexusArtifactEvent( nae );
    }

    private org.openrdf.repository.Repository federatedRepository()
    {
        if ( federatedRepository == null )
        {
            federatedRepository = new SPARQLFederationRepository();
            try
            {
                federatedRepository.initialize();
            }
            catch ( RepositoryException e )
            {
                throw new RuntimeException( e );
            }
        }
        return federatedRepository;
    }

}
