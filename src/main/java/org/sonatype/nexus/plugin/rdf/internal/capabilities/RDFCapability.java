package org.sonatype.nexus.plugin.rdf.internal.capabilities;

import static org.sonatype.nexus.plugin.rdf.internal.capabilities.RDFCapabilityDescriptor.TYPE_ID;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.sonatype.nexus.plugin.rdf.RDFStore;
import org.sonatype.nexus.plugins.capabilities.Condition;
import org.sonatype.nexus.plugins.capabilities.support.CapabilitySupport;
import org.sonatype.nexus.plugins.capabilities.support.condition.Conditions;

@Singleton
@Named( TYPE_ID )
public class RDFCapability
    extends CapabilitySupport
{

    private final RDFStore rdfStore;

    private final Conditions conditions;

    private RDFConfiguration configuration;

    @Inject
    public RDFCapability( final RDFStore rdfStore,
                          final Conditions conditions )
    {
        this.rdfStore = rdfStore;
        this.conditions = conditions;
    }

    @Override
    public void onActivate()
    {
        configuration = new RDFConfiguration( context().properties() );
        rdfStore.addConfiguration( configuration );
    }

    @Override
    public void onPassivate()
    {
        rdfStore.removeConfiguration( configuration );
    }

    @Override
    public Condition activationCondition()
    {
        return conditions.capabilities().passivateCapabilityDuringUpdate( context().id() );
    }

}
