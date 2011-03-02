package org.sonatype.nexus.plugin.rdf.internal.capabilities;

import org.sonatype.nexus.formfields.FormField;

public class ProjectOwnerFormField
    implements FormField
{

    public static final String ID = "projectOwner";

    public String getId()
    {
        return ID;
    }

    public String getLabel()
    {
        return "Project owner";
    }

    public String getType()
    {
        return "string";
    }

    public String getHelpText()
    {
        return "Specify name of organization that owns artifacts from this repository";
    }

    public String getRegexValidation()
    {
        return null;
    }

    public boolean isRequired()
    {
        return false;
    }

}
