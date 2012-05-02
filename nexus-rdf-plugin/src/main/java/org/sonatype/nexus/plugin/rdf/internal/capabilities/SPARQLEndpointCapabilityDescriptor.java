package org.sonatype.nexus.plugin.rdf.internal.capabilities;

import static org.sonatype.nexus.plugin.rdf.internal.capabilities.SPARQLEndpointCapabilityDescriptor.*;
import static org.sonatype.nexus.plugins.capabilities.CapabilityType.capabilityType;

import java.util.Arrays;
import java.util.List;
import javax.inject.Named;
import javax.inject.Singleton;

import org.sonatype.nexus.formfields.FormField;
import org.sonatype.nexus.formfields.RepoOrGroupComboFormField;
import org.sonatype.nexus.plugins.capabilities.CapabilityDescriptor;
import org.sonatype.nexus.plugins.capabilities.CapabilityType;
import org.sonatype.nexus.plugins.capabilities.support.CapabilityDescriptorSupport;

@Singleton
@Named( TYPE_ID )
public class SPARQLEndpointCapabilityDescriptor
    extends CapabilityDescriptorSupport
    implements CapabilityDescriptor
{

    public static final String TYPE_ID = "sparql-endpoint";

    private static final CapabilityType TYPE = capabilityType( TYPE_ID );

    public static final String REPOSITORY = "repositoryId";

    public SPARQLEndpointCapabilityDescriptor()
    {
        super(
            TYPE,
            "SPARQL endpoint capability",
            "Exposes Nexus Repository indexed statements as an SPARQL endpoint",
            new RepoOrGroupComboFormField( REPOSITORY, FormField.MANDATORY )
        );
    }

}
