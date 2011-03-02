package org.sonatype.nexus.plugin.rdf.internal.capabilities;

import java.util.Arrays;
import java.util.List;

import javax.inject.Named;
import javax.inject.Singleton;

import org.sonatype.nexus.formfields.FormField;
import org.sonatype.nexus.formfields.RepoOrGroupComboFormField;
import org.sonatype.nexus.plugins.capabilities.api.descriptor.CapabilityDescriptor;

@Singleton
@Named( RDFCapability.ID )
public class RDFCapabilityDescriptor
    implements CapabilityDescriptor
{

    public static final String ID = RDFCapability.ID;

    public static final String REPO_OR_GROUP_ID = "repoOrGroup";

    private final FormField repoOrGroup;

    private final FormField remoteRepositories;
    
    private final FormField projectOwner;

    public RDFCapabilityDescriptor()
    {
        repoOrGroup = new RepoOrGroupComboFormField( REPO_OR_GROUP_ID, FormField.MANDATORY );
        remoteRepositories = new RemoteRepositoriesFormField();
        projectOwner = new ProjectOwnerFormField();
    }

    public String id()
    {
        return ID;
    }

    public String name()
    {
        return "RDF capability";
    }

    public List<FormField> formFields()
    {
        return Arrays.asList( repoOrGroup, remoteRepositories, projectOwner );
    }

    public boolean isExposed()
    {
        return true;
    }

}
