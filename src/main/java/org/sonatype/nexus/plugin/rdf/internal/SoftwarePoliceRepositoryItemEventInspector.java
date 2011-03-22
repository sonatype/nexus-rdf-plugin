/**
 * Copyright (c) 2008-2011 Sonatype, Inc.
 *
 * All rights reserved. Includes the third-party code listed at http://www.sonatype.com/products/nexus/attributions.
 * Sonatype and Sonatype Nexus are trademarks of Sonatype, Inc. Apache Maven is a trademark of the Apache Foundation.
 * M2Eclipse is a trademark of the Eclipse Foundation. All other trademarks are the property of their respective owners.
 */
package org.sonatype.nexus.plugin.rdf.internal;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.slf4j.Logger;
import org.sonatype.nexus.proxy.events.EventInspector;
import org.sonatype.nexus.proxy.events.RepositoryItemEvent;
import org.sonatype.nexus.proxy.events.RepositoryItemEventCache;
import org.sonatype.nexus.proxy.events.RepositoryItemEventDelete;
import org.sonatype.nexus.proxy.events.RepositoryItemEventStore;
import org.sonatype.nexus.proxy.maven.MavenRepository;
import org.sonatype.nexus.proxy.repository.Repository;
import org.sonatype.nexus.scheduling.NexusScheduler;
import org.sonatype.plexus.appevents.Event;
import org.sonatype.scheduling.ScheduledTask;

@Named
@Singleton
public class SoftwarePoliceRepositoryItemEventInspector
    implements EventInspector
{

    private final NexusScheduler nexusScheduler;

    @Inject
    private Logger logger;

    @Inject
    public SoftwarePoliceRepositoryItemEventInspector( final NexusScheduler nexusScheduler )
    {
        assert nexusScheduler != null : "Nexuse scheduler must be specified (cannot be null)";
        this.nexusScheduler = nexusScheduler;
    }

    /**
     * Accepts {@link RepositoryItemEventStore}, {@link RepositoryItemEventCache} and {@link RepositoryItemEventDelete}
     * events ( files added/cached/removed) on an {@link MavenRepository}. {@inheritDoc}
     */
    public boolean accepts( final Event<?> evt )
    {
        if ( evt == null
             || !( evt instanceof RepositoryItemEventStore
                   || evt instanceof RepositoryItemEventCache
                   || evt instanceof RepositoryItemEventDelete ) )
        {
            return false;
        }

        final Repository repository = ( (RepositoryItemEvent) evt ).getRepository();

        return repository != null
               && repository.getRepositoryKind().isFacetAvailable( MavenRepository.class );
    }

    /**
     * {@inheritDoc}
     */
    public void inspect( final Event<?> evt )
    {
        if ( !accepts( evt ) )
        {
            return;
        }

        final RepositoryItemEvent event = (RepositoryItemEvent) evt;

        logger.debug(
              String.format(
                  "Begin handling event of type %s on item %s",
                  evt.getClass().getSimpleName(), event.getItem().getPath()
                    )
                   );

        try
        {
            if ( event.getItem().getPath().endsWith( ".pom" ) )
            {
                Map<String, List<ScheduledTask<?>>> tasksMap = nexusScheduler.getAllTasks();
                for ( Map.Entry<String, List<ScheduledTask<?>>> entry : tasksMap.entrySet() )
                {
                    List<ScheduledTask<?>> tasks = entry.getValue();
                    for ( ScheduledTask<?> task : tasks )
                    {
                        if ( task.isExposed() && task.isEnabled()
                            && SoftwarePoliceTaskDescriptor.ID.equals( task.getType() ) )
                        {
                            String taskRepositoryId = task.getTaskParams().get( "repositoryId" );
                            if ( "all_repo".equals( taskRepositoryId )
                                || event.getRepository().getId().equals( taskRepositoryId ) )
                            {
                                task.runNow();
                            }
                        }
                    }
                }
            }
        }
        catch ( Exception e )
        {
            logger.error( "Software police check skipped for event " + event, e );
        }
        finally
        {
            logger.debug(
                  String.format(
                      "End handling event of type %s on item %s", evt.getClass().getSimpleName(),
                      event.getItem().getPath() ) );
        }
    }
}
