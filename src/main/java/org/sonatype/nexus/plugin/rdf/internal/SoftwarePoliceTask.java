package org.sonatype.nexus.plugin.rdf.internal;

import java.util.List;

import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Requirement;
import org.sonatype.nexus.plugin.rdf.SoftwarePolice;
import org.sonatype.nexus.proxy.maven.MavenRepository;
import org.sonatype.nexus.scheduling.AbstractNexusRepositoriesTask;
import org.sonatype.scheduling.SchedulerTask;

@Component( role = SchedulerTask.class, hint = SoftwarePoliceTaskDescriptor.ID, instantiationStrategy = "per-lookup" )
public class SoftwarePoliceTask
    extends AbstractNexusRepositoriesTask<Object>
{

    @Requirement
    private SoftwarePolice softwarePolice;

    @Override
    protected String getRepositoryFieldId()
    {
        return SoftwarePoliceTaskDescriptor.REPO_OR_GROUP_FIELD_ID;
    }

    public String getSPARQLEndpoint()
    {
        final String uris = getParameters().get( SoftwarePoliceTaskDescriptor.SPARQL_ENDPOINT );
        return uris;
    }

    @Override
    protected String getAction()
    {
        return SoftwarePoliceTaskDescriptor.ID;
    }

    @Override
    protected String getMessage()
    {
        if ( getRepositoryId() != null )
        {
            return String.format( "Software Police checking repository [%s] against SPARQL endpoint %s",
                getRepositoryId(), getSPARQLEndpoint() );
        }
        else
        {
            return String.format(
                "Software Police checking all configured repositories against SPARQL endpoint %s",
                getSPARQLEndpoint() );
        }
    }

    @Override
    protected Object doRun()
        throws Exception
    {
        String repositoryId = getRepositoryId();
        if ( repositoryId != null )
        {
            MavenRepository repository =
                getRepositoryRegistry().getRepositoryWithFacet( repositoryId, MavenRepository.class );
            softwarePolice.check( repository, getSPARQLEndpoint() );
        }
        else
        {
            List<MavenRepository> repositories =
                getRepositoryRegistry().getRepositoriesWithFacet( MavenRepository.class );
            for ( MavenRepository repository : repositories )
            {
                softwarePolice.check( repository, getSPARQLEndpoint() );
            }
        }

        return null;
    }
}
