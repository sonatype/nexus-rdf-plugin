package org.sonatype.nexus.plugin.rdf.internal;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.plexus.component.annotations.Component;
import org.sonatype.nexus.formfields.FormField;
import org.sonatype.nexus.formfields.RepoOrGroupComboFormField;
import org.sonatype.nexus.formfields.StringTextFormField;
import org.sonatype.nexus.tasks.descriptors.AbstractScheduledTaskDescriptor;
import org.sonatype.nexus.tasks.descriptors.ScheduledTaskDescriptor;

@Component( role = ScheduledTaskDescriptor.class, hint = "SoftwarePolice", description = "Software Police Check" )
public class SoftwarePoliceTaskDescriptor
    extends AbstractScheduledTaskDescriptor
{

    public static final String ID = "SoftwarePoliceTask";

    public static final String REPO_OR_GROUP_FIELD_ID = "repositoryId";

    public static final String VULNERABILITIES_SPARQL_ENDPOINTS = "vulnerabilitiesSPARQLEndpoints";

    private final RepoOrGroupComboFormField repoField = new RepoOrGroupComboFormField( REPO_OR_GROUP_FIELD_ID,
        FormField.MANDATORY );

    private final StringTextFormField vulnerabilitiesSPARQLEndpoints = new StringTextFormField(
        VULNERABILITIES_SPARQL_ENDPOINTS,
        "Vulnerabilities SPARQL Endpoints URLs",
        "Enter a comma separated list of vulnerabilities SPARQL Endpoints URLs to check against",
        FormField.MANDATORY );

    public String getId()
    {
        return ID;
    }

    public String getName()
    {
        return "Software Police Check";
    }

    @Override
    public List<FormField> formFields()
    {
        List<FormField> fields = new ArrayList<FormField>();

        fields.add( repoField );
        fields.add( vulnerabilitiesSPARQLEndpoints );

        return fields;
    }

}
