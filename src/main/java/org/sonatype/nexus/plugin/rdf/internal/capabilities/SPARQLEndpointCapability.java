package org.sonatype.nexus.plugin.rdf.internal.capabilities;

import java.util.Map;

import org.sonatype.nexus.plugin.rdf.internal.SPARQLEndpoints;
import org.sonatype.nexus.plugins.capabilities.api.AbstractCapability;

public class SPARQLEndpointCapability
    extends AbstractCapability
{

    public static final String ID = "sparqlEndpointCapability";

    private final SPARQLEndpoints sparqlEndpoints;

    private SPARQLEndpointConfiguration configuration;

    public SPARQLEndpointCapability( final String id, final SPARQLEndpoints sparqlEndpoints )
    {
        super( id );
        this.sparqlEndpoints = sparqlEndpoints;
    }

    @Override
    public void create( final Map<String, String> properties )
    {
        load( properties );
    }

    @Override
    public void load( final Map<String, String> properties )
    {
        configuration = new SPARQLEndpointConfiguration( properties );
        sparqlEndpoints.addConfiguration( configuration );
    }

    @Override
    public void update( final Map<String, String> properties )
    {
        final SPARQLEndpointConfiguration newConfiguration =
            new SPARQLEndpointConfiguration( properties );
        if ( !configuration.equals( newConfiguration ) )
        {
            remove();
            create( properties );
        }
    }

    @Override
    public void remove()
    {
        sparqlEndpoints.removeConfiguration( configuration );
    }

}
