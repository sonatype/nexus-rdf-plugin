/**
 * Copyright (c) 2008-2011 Sonatype, Inc.
 *
 * All rights reserved. Includes the third-party code listed at http://www.sonatype.com/products/nexus/attributions.
 * Sonatype and Sonatype Nexus are trademarks of Sonatype, Inc. Apache Maven is a trademark of the Apache Foundation.
 * M2Eclipse is a trademark of the Eclipse Foundation. All other trademarks are the property of their respective owners.
 */
package org.sonatype.nexus.plugin.rdf;

import java.io.File;

import org.sonatype.nexus.plugin.rdf.internal.Utils;
import org.sonatype.nexus.proxy.maven.MavenRepository;
import org.sonatype.sisu.rdf.ItemPath;

/**
 * Path to an item in a maven repository.
 *
 * @author Alin Dreghiciu
 */
public class NexusItemPath
    extends ItemPath
{

    /**
     * Maven repository containing the item.
     */
    private final MavenRepository repository;

    /**
     * Constructor.
     *
     * @param repository     maven repository containing the item
     * @param repositoryRoot repository root file
     * @param path           path to item in repository.
     */
    public NexusItemPath( final MavenRepository repository,
                          final File repositoryRoot,
                          final String path )
    {
        super( repositoryRoot, path );

        assert repository != null : "Item repository must be specified (cannot be null)";

        this.repository = repository;
    }

    /**
     * Getter.
     *
     * @return item repository
     */
    public MavenRepository repository()
    {
        return repository;
    }

    /**
     * Create a new item path relative to passed item path root.
     *
     * @param file path to item in repository.
     */
    public NexusItemPath relative( final File file )
    {
        assert file != null : "File must be specified (cannot be null)";

        return new NexusItemPath( repository(), repositoryRoot(), Utils.getRelativePath( repositoryRoot(), file ) );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString()
    {
        return String.format( "%s:%s", repository.getId(), path() );
    }

}
