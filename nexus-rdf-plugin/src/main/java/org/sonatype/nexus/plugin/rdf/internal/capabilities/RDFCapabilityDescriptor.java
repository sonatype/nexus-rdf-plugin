package org.sonatype.nexus.plugin.rdf.internal.capabilities;

import static org.sonatype.nexus.plugin.rdf.internal.capabilities.RDFCapabilityDescriptor.*;
import static org.sonatype.nexus.plugins.capabilities.CapabilityType.capabilityType;

import javax.inject.Named;
import javax.inject.Singleton;

import org.sonatype.nexus.formfields.FormField;
import org.sonatype.nexus.formfields.RepoOrGroupComboFormField;
import org.sonatype.nexus.plugins.capabilities.CapabilityDescriptor;
import org.sonatype.nexus.plugins.capabilities.CapabilityType;
import org.sonatype.nexus.plugins.capabilities.support.CapabilityDescriptorSupport;

@Singleton
@Named( TYPE_ID )
public class RDFCapabilityDescriptor
    extends CapabilityDescriptorSupport
    implements CapabilityDescriptor
{

    public static final String TYPE_ID = "rdf";

    private static final CapabilityType TYPE = capabilityType( TYPE_ID );

    public static final String REPOSITORY = "repositoryId";

    public RDFCapabilityDescriptor()
    {
        super(
            TYPE,
            "RDF capability",
            "Indexes Maven artifacts as RDF",
            new RepoOrGroupComboFormField( REPOSITORY, FormField.MANDATORY ),
            new RemoteRepositoriesFormField()
        );
    }

}
