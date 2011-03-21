package org.sonatype.nexus.plugin.rdf.internal.capabilities;

import java.util.Arrays;
import java.util.List;

import javax.inject.Named;
import javax.inject.Singleton;

import org.sonatype.nexus.formfields.FormField;
import org.sonatype.nexus.formfields.RepoOrGroupComboFormField;
import org.sonatype.nexus.plugins.capabilities.api.descriptor.CapabilityDescriptor;

@Singleton
@Named( SPARQLEndpointCapability.ID )
public class SPARQLEndpointCapabilityDescriptor
    implements CapabilityDescriptor
{

    public static final String ID = SPARQLEndpointCapability.ID;

    static final String REPO_OR_GROUP_ID = "repoOrGroup";

    private final FormField repoOrGroup;

    public SPARQLEndpointCapabilityDescriptor()
    {
        repoOrGroup = new RepoOrGroupComboFormField( REPO_OR_GROUP_ID, FormField.MANDATORY );
    }

    public String id()
    {
        return ID;
    }

    public String name()
    {
        return "SPARQL Endpoint capability";
    }

    public List<FormField> formFields()
    {
        return Arrays.asList( repoOrGroup );
    }

    public boolean isExposed()
    {
        return true;
    }

}
