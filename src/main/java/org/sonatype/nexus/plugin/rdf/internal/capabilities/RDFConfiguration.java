package org.sonatype.nexus.plugin.rdf.internal.capabilities;

import java.util.Map;

public class RDFConfiguration
{

    private final String repositoryId;


    RDFConfiguration( final Map<String, String> properties )
    {
        repositoryId = getRepository( properties );
    }

    public String getRepositoryId()
    {
        return repositoryId;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ( ( repositoryId == null ) ? 0 : repositoryId.hashCode() );
        return result;
    }

    @Override
    public boolean equals( final Object obj )
    {
        if ( this == obj )
        {
            return true;
        }
        if ( obj == null )
        {
            return false;
        }
        if ( getClass() != obj.getClass() )
        {
            return false;
        }
        final RDFConfiguration other = (RDFConfiguration) obj;
        if ( repositoryId == null )
        {
            if ( other.repositoryId != null )
            {
                return false;
            }
        }
        else if ( !repositoryId.equals( other.repositoryId ) )
        {
            return false;
        }
        return true;
    }

    private static String getRepository( final Map<String, String> properties )
    {
        String repositoryId = properties.get( RDFCapabilityDescriptor.REPO_OR_GROUP_ID );
        repositoryId = repositoryId.replaceFirst( "repo_", "" );
        repositoryId = repositoryId.replaceFirst( "group_", "" );
        return repositoryId;
    }

}
