package org.sonatype.nexus.plugin.rdf.internal;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.plexus.component.annotations.Component;
import org.sonatype.nexus.formfields.FormField;
import org.sonatype.nexus.formfields.RepoOrGroupComboFormField;
import org.sonatype.nexus.formfields.StringTextFormField;
import org.sonatype.nexus.tasks.descriptors.AbstractScheduledTaskDescriptor;
import org.sonatype.nexus.tasks.descriptors.ScheduledTaskDescriptor;

@Component( role = ScheduledTaskDescriptor.class, hint = "RDFReindex", description = "Regenerate RDF Index" )
public class RDFReindexTaskDescriptor
    extends AbstractScheduledTaskDescriptor
{

    public static final String ID = "RDFReindexTask";

    public static final String REPO_OR_GROUP_FIELD_ID = "repositoryId";

    public static final String RESOURCE_STORE_PATH_FIELD_ID = "resourceStorePath";

    private final RepoOrGroupComboFormField repoField = new RepoOrGroupComboFormField( REPO_OR_GROUP_FIELD_ID,
        FormField.MANDATORY );

    private final StringTextFormField resourceStorePathField = new StringTextFormField( RESOURCE_STORE_PATH_FIELD_ID,
        "Repository path",
        "Enter a repository path to run the task in recursively (ie. \"/\" for root or \"/org/apache\")",
        FormField.OPTIONAL );

    public String getId()
    {
        return ID;
    }

    public String getName()
    {
        return "Regenerate RDF Index";
    }

    @Override
    public List<FormField> formFields()
    {
        List<FormField> fields = new ArrayList<FormField>();

        fields.add( repoField );
        fields.add( resourceStorePathField );

        return fields;
    }

}
