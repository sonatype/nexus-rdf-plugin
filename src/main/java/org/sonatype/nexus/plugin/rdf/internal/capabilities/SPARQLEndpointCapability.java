package org.sonatype.nexus.plugin.rdf.internal.capabilities;

import static org.sonatype.nexus.plugin.rdf.internal.capabilities.SPARQLEndpointCapabilityDescriptor.TYPE_ID;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.sonatype.nexus.plugin.rdf.SPARQLEndpoints;
import org.sonatype.nexus.plugins.capabilities.Condition;
import org.sonatype.nexus.plugins.capabilities.support.CapabilitySupport;
import org.sonatype.nexus.plugins.capabilities.support.condition.Conditions;

@Singleton
@Named( TYPE_ID )
public class SPARQLEndpointCapability
    extends CapabilitySupport
{

    private final SPARQLEndpoints sparqlEndpoints;

    private final Conditions conditions;

    private SPARQLEndpointConfiguration configuration;

    @Inject
    public SPARQLEndpointCapability( final SPARQLEndpoints sparqlEndpoints,
                                     final Conditions conditions )
    {
        this.sparqlEndpoints = sparqlEndpoints;
        this.conditions = conditions;
    }

    @Override
    public void onActivate()
    {
        configuration = new SPARQLEndpointConfiguration( context().properties() );
        sparqlEndpoints.addConfiguration( configuration );
    }

    @Override
    public void onPassivate()
    {
        sparqlEndpoints.removeConfiguration( configuration );
    }

    @Override
    public Condition activationCondition()
    {
        return conditions.capabilities().passivateCapabilityDuringUpdate( context().id() );
    }

}
