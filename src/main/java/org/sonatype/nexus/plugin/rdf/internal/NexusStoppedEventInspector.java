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

import org.sonatype.nexus.proxy.events.EventInspector;
import org.sonatype.nexus.proxy.events.NexusStoppedEvent;
import org.sonatype.nexus.proxy.events.RepositoryRegistryEventAdd;
import org.sonatype.nexus.proxy.events.RepositoryRegistryEventRemove;
import org.sonatype.nexus.proxy.maven.MavenRepository;
import org.sonatype.plexus.appevents.Event;
import org.sonatype.sisu.rdf.RepositoryHub;

/**
 * Listens on nexus stopped event ({@see #accepts}) and disposes all RDF repositories.
 * 
 * @author Alin Dreghiciu
 */
@Named
@Singleton
public class NexusStoppedEventInspector
    implements EventInspector
{

    private final RepositoryHub repositoryHub;

    @Inject
    public NexusStoppedEventInspector( final RepositoryHub repositoryHub )
    {
        this.repositoryHub = repositoryHub;
    }

    /**
     * Accepts {@link RepositoryRegistryEventAdd} and {@link RepositoryRegistryEventRemove} events on
     * {@link MavenRepository}s. {@inheritDoc}
     */
    public boolean accepts( final Event<?> evt )
    {
        return evt instanceof NexusStoppedEvent;
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

        repositoryHub.shutdown();
    }

}
