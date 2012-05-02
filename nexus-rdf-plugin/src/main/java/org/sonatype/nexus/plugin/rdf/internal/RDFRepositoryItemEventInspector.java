/**
 * Copyright (c) 2008-2011 Sonatype, Inc.
 *
 * All rights reserved. Includes the third-party code listed at http://www.sonatype.com/products/nexus/attributions.
 * Sonatype and Sonatype Nexus are trademarks of Sonatype, Inc. Apache Maven is a trademark of the Apache Foundation.
 * M2Eclipse is a trademark of the Eclipse Foundation. All other trademarks are the property of their respective owners.
 */
package org.sonatype.nexus.plugin.rdf.internal;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.slf4j.Logger;
import org.sonatype.nexus.plugin.rdf.NexusItemPath;
import org.sonatype.nexus.plugin.rdf.RDFStore;
import org.sonatype.nexus.proxy.events.EventInspector;
import org.sonatype.nexus.proxy.events.RepositoryItemEvent;
import org.sonatype.nexus.proxy.events.RepositoryItemEventCache;
import org.sonatype.nexus.proxy.events.RepositoryItemEventDelete;
import org.sonatype.nexus.proxy.events.RepositoryItemEventStore;
import org.sonatype.nexus.proxy.maven.MavenRepository;
import org.sonatype.nexus.proxy.repository.Repository;
import org.sonatype.plexus.appevents.Event;

@Named
@Singleton
public class RDFRepositoryItemEventInspector
    implements EventInspector
{

    private final RDFStore rdfStore;

    @Inject
    private Logger logger;

    @Inject
    public RDFRepositoryItemEventInspector( final RDFStore rdfStore )
    {
        assert rdfStore != null : "RDF Store Service must be specified (cannot be null)";
        this.rdfStore = rdfStore;
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
        final MavenRepository repository = (MavenRepository) event.getRepository();

        logger.debug(
              String.format(
                  "Begin handling event of type %s on item %s",
                  evt.getClass().getSimpleName(), event.getItem().getPath()
                    )
                   );

        try
        {
            if ( event instanceof RepositoryItemEventStore || event instanceof RepositoryItemEventCache )
            {
                onItemAdded( repository, event );
            }
            else if ( event instanceof RepositoryItemEventDelete )
            {
                onItemRemoved( repository, event );
            }
        }
        catch ( Exception e )
        {
            logger.error( "Indexing skipped for event " + event, e );
        }
        finally
        {
            logger.debug(
                  String.format(
                      "End handling event of type %s on item %s", evt.getClass().getSimpleName(),
                      event.getItem().getPath() ) );
        }
    }

    /**
     * Handles the event of an stored / cached repository item.
     * 
     * @param repository maven repository containing the item
     * @param event stored / cached repository item event
     */
    private void onItemAdded( final MavenRepository repository,
                              final RepositoryItemEvent event )
    {
        rdfStore.index( new NexusItemPath( repository, Utils.safeGetRepositoryLocalStorageAsFile( repository, logger ),
            event.getItem().getPath() ) );
    }

    /**
     * Handles the event of an deleted repository item.
     * 
     * @param repository maven repository containing the item
     * @param event deleted repository item event
     */
    private void onItemRemoved( final MavenRepository repository,
                                final RepositoryItemEvent event )
    {
        rdfStore.remove( new NexusItemPath( repository, Utils.safeGetRepositoryLocalStorageAsFile( repository, logger ),
            event.getItem().getPath() ) );
    }

}
