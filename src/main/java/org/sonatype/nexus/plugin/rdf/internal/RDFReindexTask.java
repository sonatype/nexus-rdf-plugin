package org.sonatype.nexus.plugin.rdf.internal;

import javax.inject.Inject;

import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Requirement;
import org.slf4j.Logger;
import org.sonatype.nexus.feeds.FeedRecorder;
import org.sonatype.nexus.plugin.rdf.ItemPath;
import org.sonatype.nexus.plugin.rdf.RDFStore;
import org.sonatype.nexus.proxy.maven.MavenRepository;
import org.sonatype.nexus.scheduling.AbstractNexusRepositoriesPathAwareTask;
import org.sonatype.scheduling.SchedulerTask;

@Component( role = SchedulerTask.class, hint = RDFReindexTaskDescriptor.ID, instantiationStrategy = "per-lookup" )
public class RDFReindexTask
    extends AbstractNexusRepositoriesPathAwareTask<Object>
{

    @Requirement
    private RDFStore rdfStore;

    @Inject
    private Logger logger;

    @Override
    protected String getRepositoryFieldId()
    {
        return RDFReindexTaskDescriptor.REPO_OR_GROUP_FIELD_ID;
    }

    @Override
    protected String getRepositoryPathFieldId()
    {
        return RDFReindexTaskDescriptor.RESOURCE_STORE_PATH_FIELD_ID;
    }

    @Override
    protected String getAction()
    {
        return FeedRecorder.SYSTEM_REINDEX_ACTION;
    }

    @Override
    protected String getMessage()
    {
        if ( getRepositoryId() != null )
        {
            return String.format( "Regenerating RDF index for repository [%s] from path [%s] and bellow",
                getRepositoryId(), getResourceStorePath() );
        }
        else
        {
            return "Regenerating RDF index for for all configured repositories";
        }
    }

    @Override
    protected Object doRun()
        throws Exception
    {
        MavenRepository repository =
            getRepositoryRegistry().getRepositoryWithFacet( getRepositoryId(), MavenRepository.class );

        rdfStore.scanAndIndex(
                new ItemPath( repository,
                              Utils.safeGetRepositoryLocalStorageAsFile( repository, logger ),
                              getResourceStorePath() ) );

        return null;
    }

}
