package org.sonatype.nexus.plugin.rdf.internal.capabilities;

import org.sonatype.nexus.formfields.FormField;

public class DefaultLicenseFormField
    implements FormField
{

    public static final String ID = "defaultLicense";

    public String getId()
    {
        return ID;
    }

    public String getLabel()
    {
        return "Default License";
    }

    public String getType()
    {
        return "string";
    }

    public String getHelpText()
    {
        return "Specify a default license for artifacts from this repository";
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
