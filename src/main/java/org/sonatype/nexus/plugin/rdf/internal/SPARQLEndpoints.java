package org.sonatype.nexus.plugin.rdf.internal;

import static org.sonatype.sisu.rdf.RepositoryIdentity.repositoryIdentity;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.openrdf.repository.Repository;
import org.sonatype.nexus.plugin.rdf.internal.capabilities.SPARQLEndpointConfiguration;
import org.sonatype.sisu.rdf.RepositoryHub;
import org.sonatype.sisu.sparql.endpoint.RequestPathSparqlRepositorySource;
import org.sonatype.sisu.sparql.endpoint.SparqlRepositorySource;

@Named
@Singleton
public class SPARQLEndpoints
    extends RequestPathSparqlRepositorySource
    implements SparqlRepositorySource
{

    private final Map<String, SPARQLEndpointConfiguration> configurations;

    private final RepositoryHub repositoryHub;

    @Inject
    public SPARQLEndpoints( RepositoryHub repositoryHub )
    {
        this.repositoryHub = repositoryHub;
        configurations = new HashMap<String, SPARQLEndpointConfiguration>();
    }

    public boolean isEnabledFor( String repositoryId )
    {
        return configurations.containsKey( repositoryId );
    }

    @Override
    protected Repository findRepository( String repositoryId )
    {
        final SPARQLEndpointConfiguration matchingConfig = configurations.get( repositoryId );
        if ( matchingConfig == null )
        {
            return null;
        }
        return repositoryHub.repository( repositoryIdentity( matchingConfig.repositoryId() ) );
    }

    public void addConfiguration( final SPARQLEndpointConfiguration configuration )
    {
        configurations.put( configuration.repositoryId(), configuration );
    }

    public void removeConfiguration( final SPARQLEndpointConfiguration configuration )
    {
        configurations.remove( configuration.repositoryId() );
    }

}
