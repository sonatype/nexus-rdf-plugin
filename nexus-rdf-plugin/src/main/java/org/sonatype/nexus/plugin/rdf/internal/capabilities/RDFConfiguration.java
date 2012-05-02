package org.sonatype.nexus.plugin.rdf.internal.capabilities;

import java.util.Arrays;
import java.util.Map;

import org.codehaus.plexus.util.StringUtils;
import org.sonatype.sisu.rdf.StatementsProducerContext;

public class RDFConfiguration
    implements StatementsProducerContext
{

    private final String repositoryId;

    private final String[] remoteRepositoriesIds;

    public RDFConfiguration( final Map<String, String> properties )
    {
        repositoryId = repository( properties );
        remoteRepositoriesIds = remoteRepositoriesIds( properties );
    }

    public String repositoryId()
    {
        return repositoryId;
    }

    public String[] remoteRepositories()
    {
        return remoteRepositoriesIds;
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( !( o instanceof RDFConfiguration ) )
        {
            return false;
        }

        final RDFConfiguration that = (RDFConfiguration) o;

        if ( !Arrays.equals( remoteRepositoriesIds, that.remoteRepositoriesIds ) )
        {
            return false;
        }
        if ( repositoryId != null ? !repositoryId.equals( that.repositoryId ) : that.repositoryId != null )
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        int result = repositoryId != null ? repositoryId.hashCode() : 0;
        result = 31 * result + ( remoteRepositoriesIds != null ? Arrays.hashCode( remoteRepositoriesIds ) : 0 );
        return result;
    }

    @Override
    public String toString()
    {
        final StringBuilder sb = new StringBuilder();
        sb.append( "RDFConfiguration" );
        sb.append( "[remoteRepositoriesIds=" ).append(
            remoteRepositoriesIds == null ? "null" : Arrays.asList( remoteRepositoriesIds ).toString()
        );
        sb.append( ", repositoryId='" ).append( repositoryId ).append( '\'' );
        sb.append( ']' );
        return sb.toString();
    }

    private static String repository( final Map<String, String> properties )
    {
        String repositoryId = properties.get( RDFCapabilityDescriptor.REPOSITORY );
        return repositoryId;
    }

    private static String[] remoteRepositoriesIds( final Map<String, String> properties )
    {
        final String repository = repository( properties );
        final String remotes = properties.get( RemoteRepositoriesFormField.ID );
        if ( StringUtils.isBlank( remotes ) )
        {
            return new String[]{ repository };
        }

        final String[] remoteRepositories = ( repository + "," + remotes ).split( "," );
        return remoteRepositories;
    }

}
