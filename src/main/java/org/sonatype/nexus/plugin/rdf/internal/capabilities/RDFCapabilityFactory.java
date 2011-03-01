package org.sonatype.nexus.plugin.rdf.internal.capabilities;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.sonatype.nexus.plugin.rdf.RDFStore;
import org.sonatype.nexus.plugins.capabilities.api.Capability;
import org.sonatype.nexus.plugins.capabilities.api.CapabilityFactory;

@Named( RDFCapability.ID )
@Singleton
public class RDFCapabilityFactory
    implements CapabilityFactory
{

    private final RDFStore rdfStore;
    
    @Inject
    RDFCapabilityFactory(RDFStore rdfStore)
    {
        this.rdfStore = rdfStore;
    }

    public Capability create( final String id )
    {
        return new RDFCapability( id, rdfStore );
    }

}
