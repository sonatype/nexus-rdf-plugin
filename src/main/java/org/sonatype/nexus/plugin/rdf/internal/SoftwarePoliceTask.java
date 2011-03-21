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

    public String getVulnerabilitiesSPARQLEndpoints()
    {
        final String uris = getParameters().get( SoftwarePoliceTaskDescriptor.VULNERABILITIES_SPARQL_ENDPOINTS );
        return uris;
    }

    @Override
    protected String getAction()
    {
        return SoftwarePoliceFeedSource.ACTION;
    }

    @Override
    protected String getMessage()
    {
        if ( getRepositoryId() != null )
        {
            return String.format( "Checking repository [%s] for vulnerabilities against SPARQL endpoints %s",
                getRepositoryId(), getVulnerabilitiesSPARQLEndpoints() );
        }
        else
        {
            return String.format(
                "Checking all configured repositories for vulnerabilities against SPARQL endpoints %s",
                getVulnerabilitiesSPARQLEndpoints() );
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
            softwarePolice.check( repository, getVulnerabilitiesSPARQLEndpoints() );
        }
        else
        {
            List<MavenRepository> repositories =
                getRepositoryRegistry().getRepositoriesWithFacet( MavenRepository.class );
            for ( MavenRepository repository : repositories )
            {
                softwarePolice.check( repository, getVulnerabilitiesSPARQLEndpoints() );
            }
        }

        return null;
    }
}
