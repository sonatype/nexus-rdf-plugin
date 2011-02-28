package org.sonatype.nexus.plugin.rdf.internal.capabilities;

import java.util.Map;

import org.sonatype.nexus.plugin.rdf.RDFStore;
import org.sonatype.nexus.plugins.capabilities.api.AbstractCapability;

public class RDFCapability
    extends AbstractCapability
{

    public static final String ID = "rdfCapability";

    private final RDFStore rdfStore;

    private RDFConfiguration configuration;

    public RDFCapability( final String id, final RDFStore rdfStore )
    {
        super( id );
        this.rdfStore = rdfStore;
    }

    @Override
    public void create( final Map<String, String> properties )
    {
        load( properties );
    }

    @Override
    public void load( final Map<String, String> properties )
    {
        configuration = new RDFConfiguration( properties );
        rdfStore.addConfiguration( configuration );
    }

    @Override
    public void update( final Map<String, String> properties )
    {
        final RDFConfiguration newConfiguration =
            new RDFConfiguration( properties );
        if ( !configuration.equals( newConfiguration ) )
        {
            remove();
            create( properties );
        }
    }

    @Override
    public void remove()
    {
        rdfStore.removeConfiguration( configuration );
    }

}
