package org.sonatype.nexus.plugin.rdf;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.openrdf.repository.Repository;
import org.sonatype.nexus.plugin.rdf.internal.capabilities.SPARQLEndpointConfiguration;
import org.sonatype.sisu.rdf.RepositoryHub;
import org.sonatype.sisu.rdf.RepositoryIdentity;
import org.sonatype.sisu.sparql.endpoint.RepositoryHubSparqlRepositorySource;

@Named
@Singleton
public class SPARQLEndpoints
    extends RepositoryHubSparqlRepositorySource
{

    private final Map<String, SPARQLEndpointConfiguration> configurations;

    @Inject
    public SPARQLEndpoints( RepositoryHub repositoryHub )
    {
        super( repositoryHub );
        configurations = new HashMap<String, SPARQLEndpointConfiguration>();
    }

    public boolean isEnabledFor( String repositoryId )
    {
        return configurations.containsKey( repositoryId );
    }

    @Override
    public Repository get( RepositoryIdentity id )
    {
        final SPARQLEndpointConfiguration matchingConfig = configurations.get( id.stringValue() );
        if ( matchingConfig == null )
        {
            return null;
        }
        return super.get( id );
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
