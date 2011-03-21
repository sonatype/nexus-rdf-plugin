package org.sonatype.nexus.plugin.rdf.internal.capabilities;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.sonatype.nexus.plugin.rdf.internal.SPARQLEndpoints;
import org.sonatype.nexus.plugins.capabilities.api.Capability;
import org.sonatype.nexus.plugins.capabilities.api.CapabilityFactory;

@Named( SPARQLEndpointCapability.ID )
@Singleton
public class SPARQLEndpointCapabilityFactory
    implements CapabilityFactory
{

    private final SPARQLEndpoints sparqlEndpoints;

    @Inject
    SPARQLEndpointCapabilityFactory(SPARQLEndpoints sparqlEndpoints)
    {
        this.sparqlEndpoints = sparqlEndpoints;
    }

    public Capability create( final String id )
    {
        return new SPARQLEndpointCapability( id, sparqlEndpoints );
    }

}
