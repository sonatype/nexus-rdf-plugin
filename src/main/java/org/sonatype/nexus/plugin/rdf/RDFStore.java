package org.sonatype.nexus.plugin.rdf;

import static org.sonatype.sisu.rdf.RepositoryIdentity.repositoryIdentity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.openrdf.model.Statement;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.slf4j.Logger;
import org.sonatype.nexus.plugin.rdf.internal.capabilities.RDFConfiguration;
import org.sonatype.sisu.rdf.RepositoryHub;
import org.sonatype.sisu.rdf.maven.MavenToRDF;

@Named
@Singleton
public class RDFStore
{

    @Inject
    private Logger logger;

    private final Map<String, RDFConfiguration> configurations;

    private final RepositoryHub repositoryHub;

    private final List<StatementsProducer> statementsProducers;

    private final MavenToRDF mavenToRDF;

    @Inject
    public RDFStore( RepositoryHub repositoryHub, MavenToRDF mavenToRDF, List<StatementsProducer> statementsProducers )
    {
        this.repositoryHub = repositoryHub;
        this.mavenToRDF = mavenToRDF;
        this.statementsProducers = statementsProducers;
        configurations = new HashMap<String, RDFConfiguration>();
    }

    public void index( final ItemPath path )
    {
        final RDFConfiguration matchingConfig = configurations.get( path.repository().getId() );
        if ( matchingConfig == null )
        {
            return;
        }

        logger.debug( String.format( "About to index item [%s] as RDF statements", path ) );

        Collection<Statement> statements = new ArrayList<Statement>();
        for ( StatementsProducer producer : statementsProducers )
        {
            statements.addAll( producer.parse( path, matchingConfig.remoteRepositories() ) );
        }
        if ( !statements.isEmpty() )
        {
            Repository repository = repositoryHub.repository( repositoryIdentity( matchingConfig.repositoryId() ) );
            try
            {
                RepositoryConnection conn = repository.getConnection();
                conn.add( statements, mavenToRDF.contextFor( matchingConfig.repositoryId(), path.path() ) );
                conn.commit();
                conn.close();
            }
            catch ( RepositoryException e )
            {
                logger.warn( String.format( "Could not index item [%s] as RDF statements", path ), e );
            }
        }
    }

    public void remove( final ItemPath path )
    {
        final RDFConfiguration matchingConfig = configurations.get( path.repository().getId() );
        if ( matchingConfig == null )
        {
            return;
        }
        logger.debug( String.format( "About to remove RDF statements for item [%s]", path ) );
        Repository repository = repositoryHub.repository( repositoryIdentity( matchingConfig.repositoryId() ) );
        try
        {
            RepositoryConnection conn = repository.getConnection();
            conn.clear( mavenToRDF.contextFor( matchingConfig.repositoryId(), path.path() ) );
            conn.commit();
            conn.close();
        }
        catch ( RepositoryException e )
        {
            logger.warn( String.format( "Could not remove RDF statements for item [%s]", path ), e );
        }
    }

    public void addConfiguration( final RDFConfiguration configuration )
    {
        repositoryHub.repository( repositoryIdentity( configuration.repositoryId() ) );
        configurations.put( configuration.repositoryId(), configuration );
    }

    public void removeConfiguration( final RDFConfiguration configuration )
    {
        configurations.remove( configuration.repositoryId() );
        repositoryHub.shutdown( repositoryIdentity( configuration.repositoryId() ) );
    }

}